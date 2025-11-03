package dev.sterner.witchery.client.tarot

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.network.LockInTarotCardsC2SPayload
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryTarotEffects
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

class TarotReadingScreen : Screen(Component.literal("Tarot Reading")) {

    private var player: Player? = null
    private val cards = mutableListOf<TarotCardObject>()
    private val deckCard = TarotCardObject(0)
    private var animationTicks = 0
    private var isDrawing = false
    private var hasDrawn = false
    private var hoveredCard: TarotCardObject? = null

    companion object {
        const val OFFSET = 25f
        const val NUM_CARDS = 3
        const val ANIMATION_DURATION = 30
        const val FLIP_DELAY = 10
        const val CRYSTAL_BALL_RANGE = 8.0
        const val CARD_Y_OFFSET = -40f

        fun open(player: Player) {
            Minecraft.getInstance().setScreen(TarotReadingScreen().apply {
                this.player = player
            })
        }
    }

    override fun init() {
        super.init()
        val padding = 20f
        val leftOffset = OFFSET
        deckCard.screenX = width - TarotCardObject.CARD_WIDTH - padding - leftOffset
        deckCard.screenY = height - TarotCardObject.CARD_HEIGHT - padding
        deckCard.targetX = deckCard.screenX
        deckCard.targetY = deckCard.screenY
    }

    private fun hasCrystalBallNearby(): Boolean {
        val player = this.player ?: return false
        val level = player.level()
        val pos = player.blockPosition()

        for (x in -CRYSTAL_BALL_RANGE.toInt()..CRYSTAL_BALL_RANGE.toInt()) {
            for (y in -CRYSTAL_BALL_RANGE.toInt()..CRYSTAL_BALL_RANGE.toInt()) {
                for (z in -CRYSTAL_BALL_RANGE.toInt()..CRYSTAL_BALL_RANGE.toInt()) {
                    val checkPos = pos.offset(x, y, z)
                    val state = level.getBlockState(checkPos)
                    if (state.block == WitcheryBlocks.CRYSTAL_BALL.get()) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun startDrawing() {
        if (isDrawing || hasDrawn) return

        isDrawing = true
        animationTicks = 0

        val hasCrystalBall = hasCrystalBallNearby()
        val reversedChance = if (hasCrystalBall) 0.35f else 0.5f

        val random = player?.level()?.random ?: return
        val availableCards = (1..22).toMutableList()

        val topDeckOffsetX = (5 - 1) * 1f
        val topDeckOffsetY = -(5 - 1) * 1f

        for (i in 0 until NUM_CARDS) {
            val cardIndex = availableCards.removeAt(random.nextInt(availableCards.size))
            val card = TarotCardObject(cardIndex)

            card.isReversed = random.nextFloat() < reversedChance

            card.screenX = deckCard.screenX + topDeckOffsetX
            card.screenY = deckCard.screenY + topDeckOffsetY
            card.rotationY = 180f

            val totalWidth = NUM_CARDS * TarotCardObject.CARD_WIDTH + (NUM_CARDS - 1) * 20f
            val startX = width / 2f - totalWidth / 2f

            card.targetX = startX + i * (TarotCardObject.CARD_WIDTH + 20f)
            card.targetY = height / 2f - TarotCardObject.CARD_HEIGHT / 2f + CARD_Y_OFFSET

            cards.add(card)
        }
    }

    override fun resize(minecraft: Minecraft, width: Int, height: Int) {
        super.resize(minecraft, width, height)

        val padding = 20f
        val leftOffset = OFFSET
        deckCard.screenX = width - TarotCardObject.CARD_WIDTH - padding - leftOffset
        deckCard.screenY = height - TarotCardObject.CARD_HEIGHT - padding
        deckCard.targetX = deckCard.screenX
        deckCard.targetY = deckCard.screenY

        if (hasDrawn || isDrawing) {
            val totalWidth = NUM_CARDS * TarotCardObject.CARD_WIDTH + (NUM_CARDS - 1) * 20f
            val startX = width / 2f - totalWidth / 2f
            val targetY = height / 2f - TarotCardObject.CARD_HEIGHT / 2f + CARD_Y_OFFSET

            for ((i, card) in cards.withIndex()) {
                card.targetX = startX + i * (TarotCardObject.CARD_WIDTH + 20f)
                card.targetY = targetY
            }
        }
    }


    override fun tick() {
        super.tick()

        if (isDrawing) {
            animationTicks++

            for ((index, card) in cards.withIndex()) {
                val cardStartTick = index * 10

                if (animationTicks > cardStartTick) {
                    val progress = ((animationTicks - cardStartTick) / ANIMATION_DURATION.toFloat()).coerceIn(0f, 1f)
                    val eased = easeOutCubic(progress)

                    card.screenX = Mth.lerp(eased, deckCard.screenX, card.targetX)
                    card.screenY = Mth.lerp(eased, deckCard.screenY, card.targetY)

                    val flipStartTick = cardStartTick + ANIMATION_DURATION + index * FLIP_DELAY
                    if (animationTicks > flipStartTick && !card.isFlipped) {
                        val flipProgress = ((animationTicks - flipStartTick) / 20f).coerceIn(0f, 1f)
                        card.rotationY = Mth.lerp(flipProgress, 180f, 0f)

                        if (flipProgress >= 1f) {
                            card.isFlipped = true
                        }
                    }
                }
            }

            if (animationTicks > ANIMATION_DURATION + (NUM_CARDS * FLIP_DELAY) + 40) {
                isDrawing = false
                hasDrawn = true
                sendCardsToServer()
            }
        }
    }

    private fun updateHoverEffects(mouseX: Int, mouseY: Int) {
        if (!hasDrawn || isDrawing) return

        hoveredCard = null

        for (card in cards) {
            if (!card.isFlipped) continue

            val cardLeft = card.screenX
            val cardRight = card.screenX + TarotCardObject.CARD_WIDTH
            val cardTop = card.screenY
            val cardBottom = card.screenY + TarotCardObject.CARD_HEIGHT

            if (mouseX >= cardLeft && mouseX <= cardRight && mouseY >= cardTop && mouseY <= cardBottom) {
                hoveredCard = card

                val centerX = card.screenX + TarotCardObject.CARD_WIDTH / 2f
                val centerY = card.screenY + TarotCardObject.CARD_HEIGHT / 2f

                val relX = ((mouseX - centerX) / (TarotCardObject.CARD_WIDTH / 2f))
                val relY = ((mouseY - centerY) / (TarotCardObject.CARD_HEIGHT / 2f))

                card.rotationY = -relX * 10f
                card.rotationX = -relY * 10f

                break
            } else {
                card.rotationY = Mth.lerp(0.2f, card.rotationY, 0f)
                card.rotationX = Mth.lerp(0.2f, card.rotationX, 0f)
            }
        }
    }

    private fun sendCardsToServer() {
        val cardNumbers = cards.map { it.cardNumber }
        val reversedStates = cards.map { it.isReversed }
        PacketDistributor.sendToServer(LockInTarotCardsC2SPayload(cardNumbers, reversedStates))
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {

        updateHoverEffects(mouseX, mouseY)

        for (i in 0 until 5) {
            val offsetCard = TarotCardObject(0)
            offsetCard.screenX = deckCard.screenX + (i * 1f)
            offsetCard.screenY = deckCard.screenY - (i * 1f)
            offsetCard.rotationY = deckCard.rotationY
            renderCard(guiGraphics, offsetCard, true, partialTick)
        }

        if (!hasDrawn && !isDrawing) {
            val text = Component.literal("Draw to seal your fate")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.ITALIC)

            val textWidth = minecraft!!.font.width(text)
            val textX = (deckCard.screenX + TarotCardObject.CARD_WIDTH / 2f - textWidth / 2f).toInt()
            val textY = (deckCard.screenY - 15).toInt()

            guiGraphics.drawString(
                minecraft!!.font,
                text,
                textX,
                textY,
                0xFFD700,
                true
            )
        }

        for (card in cards) {
            renderCard(guiGraphics, card, false, partialTick)
        }

        if (hoveredCard != null && hasDrawn) {
            renderTooltip(guiGraphics, mouseX, mouseY, hoveredCard!!)
        }

        for (renderable in this.renderables) {
            renderable.render(guiGraphics, mouseX, mouseY, partialTick)
        }
    }

    private fun renderCard(guiGraphics: GuiGraphics, card: TarotCardObject, isDeck: Boolean, partialTick: Float) {
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()

        poseStack.translate(card.screenX.toDouble(), card.screenY.toDouble(), 0.0)
        poseStack.translate(
            (TarotCardObject.CARD_WIDTH / 2f).toDouble(),
            (TarotCardObject.CARD_HEIGHT / 2f).toDouble(),
            0.0
        )

        poseStack.mulPose(Axis.YP.rotationDegrees(card.rotationY))
        if (card.isReversed && card.rotationY < 90f) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f))
        }

        if (card.isFlipped) {
            poseStack.mulPose(Axis.ZP.rotationDegrees(card.rotationZ))
            poseStack.mulPose(Axis.XP.rotationDegrees(card.rotationX))
        }

        poseStack.translate(
            -(TarotCardObject.CARD_WIDTH / 2f).toDouble(),
            -(TarotCardObject.CARD_HEIGHT / 2f).toDouble(),
            0.0
        )

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.disableDepthTest()
        RenderSystem.disableCull()

        if (card.rotationY >= 90f || isDeck) {
            guiGraphics.blit(
                getBackTexture(),
                0, 0,
                0f, 0f,
                TarotCardObject.CARD_WIDTH.toInt(),
                TarotCardObject.CARD_HEIGHT.toInt(),
                TarotCardObject.CARD_WIDTH.toInt(),
                TarotCardObject.CARD_HEIGHT.toInt()
            )
        }

        if (card.rotationY < 90f && !isDeck) {
            guiGraphics.blit(
                getCardTexture(card.cardNumber),
                0, 0,
                0f, 0f,
                TarotCardObject.CARD_WIDTH.toInt(),
                TarotCardObject.CARD_HEIGHT.toInt(),
                TarotCardObject.CARD_WIDTH.toInt(),
                TarotCardObject.CARD_HEIGHT.toInt()
            )
        }

        RenderSystem.enableCull()
        RenderSystem.enableDepthTest()
        RenderSystem.disableBlend()

        poseStack.popPose()
    }

    private fun renderTooltip(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, card: TarotCardObject) {
        val tooltip = mutableListOf<Component>()

        tooltip.add(
            Component.literal(TarotCardObject.getArcanaName(card.cardNumber, card.isReversed))
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
        )
        tooltip.add(Component.literal(""))

        if (card.isReversed) {
            tooltip.add(Component.literal("Reversed Card").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC))
            tooltip.add(Component.literal("Negative Effect").withStyle(ChatFormatting.DARK_RED))
        } else {
            tooltip.add(Component.literal("Upright Card").withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC))
            tooltip.add(Component.literal("Positive Effect").withStyle(ChatFormatting.DARK_GREEN))
        }

        val tarotEffect = WitcheryTarotEffects.getByCardNumber(card.cardNumber)
        if (tarotEffect != null) {
            val effectText = tarotEffect.getDescription(card.isReversed).string

            tooltip.add(Component.literal(""))
            tooltip.add(Component.literal("Effect:").withStyle(ChatFormatting.GRAY))

            val maxWidth = 200
            val words = effectText.split(" ")
            var currentLine = ""

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                if (minecraft!!.font.width(testLine) <= maxWidth) {
                    currentLine = testLine
                } else {
                    if (currentLine.isNotEmpty()) {
                        tooltip.add(Component.literal("  $currentLine").withStyle(ChatFormatting.DARK_GRAY))
                    }
                    currentLine = word
                }
            }

            if (currentLine.isNotEmpty()) {
                tooltip.add(Component.literal("  $currentLine").withStyle(ChatFormatting.DARK_GRAY))
            }
        }

        val lines = tooltip.map { it.visualOrderText }
        guiGraphics.renderTooltip(minecraft!!.font, lines, mouseX, mouseY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (!hasDrawn && !isDrawing) {
            val deckLeft = deckCard.screenX
            val deckRight = deckCard.screenX + TarotCardObject.CARD_WIDTH
            val deckTop = deckCard.screenY
            val deckBottom = deckCard.screenY + TarotCardObject.CARD_HEIGHT

            if (mouseX >= deckLeft && mouseX <= deckRight && mouseY >= deckTop && mouseY <= deckBottom) {
                startDrawing()
                return true
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        if (minecraft!!.options.keyInventory.matches(keyCode, scanCode)) {
            onClose()
            return true
        }
        return super.keyPressed(keyCode, scanCode, modifiers)
    }

    override fun isPauseScreen(): Boolean = false

    private fun getCardTexture(cardNumber: Int): ResourceLocation {
        return Witchery.id("textures/gui/arcana/$cardNumber.png")
    }

    private fun getBackTexture(): ResourceLocation {
        return Witchery.id("textures/gui/arcana/back.png")
    }

    private fun easeOutCubic(t: Float): Float {
        val t1 = t - 1f
        return t1 * t1 * t1 + 1f
    }
}