package dev.sterner.witchery.client.tarot

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data_attachment.TarotPlayerAttachment
import dev.sterner.witchery.core.registry.WitcheryTarotEffects
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import org.lwjgl.glfw.GLFW
import java.awt.Color

object TarotCardHudRenderer {

    private const val CARD_SCALE = 0.5f
    private const val CARD_SPACING = 4f
    private const val HUD_PADDING = 10f
    private const val ANIMATION_SPEED = 0.15f

    private var hoveredCardIndex: Int? = null
    private var isMenuOpen = false
    private var animationProgress = 0f

    @SubscribeEvent
    fun onRenderGuiOverlay(event: RenderGuiLayerEvent.Post) {
        if (event.name != VanillaGuiLayers.HOTBAR) return

        if (animationProgress <= 0f) return

        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return

        val data = TarotPlayerAttachment.getData(player)
        if (data.drawnCards.isEmpty()) return

        val guiGraphics = event.guiGraphics
        val screenWidth = minecraft.window.guiScaledWidth
        val screenHeight = minecraft.window.guiScaledHeight

        renderTarotCards(guiGraphics, data, screenWidth, screenHeight, event.partialTick.gameTimeDeltaTicks)

        if (hoveredCardIndex != null && isMenuOpen) {
            val cardNumber = data.drawnCards[hoveredCardIndex!!]
            val isReversed = data.reversedCards.getOrNull(hoveredCardIndex!!) ?: false
            renderCardTooltip(guiGraphics, cardNumber, isReversed, minecraft)
        }
    }

    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return

        val data = TarotPlayerAttachment.getData(player)
        val shouldBeOpen = data.drawnCards.isNotEmpty() && isLeftAltPressed()

        if (shouldBeOpen && !isMenuOpen) {
            openMenu()
        } else if (!shouldBeOpen && isMenuOpen) {
            closeMenu()
        }

        if (isMenuOpen && animationProgress < 1f) {
            animationProgress = minOf(1f, animationProgress + ANIMATION_SPEED)
        } else if (!isMenuOpen && animationProgress > 0f) {
            animationProgress = maxOf(0f, animationProgress - ANIMATION_SPEED)
        }

        if (isMenuOpen && data.drawnCards.isNotEmpty()) {
            val mouseX = minecraft.mouseHandler.xpos() * minecraft.window.guiScaledWidth / minecraft.window.screenWidth
            val mouseY = minecraft.mouseHandler.ypos() * minecraft.window.guiScaledHeight / minecraft.window.screenHeight

            checkHover(data, minecraft.window.guiScaledWidth, minecraft.window.guiScaledHeight, mouseX.toFloat(), mouseY.toFloat())
        } else {
            hoveredCardIndex = null
        }
    }

    private fun openMenu() {
        isMenuOpen = true
        animationProgress = 0f
    }

    private fun closeMenu() {
        isMenuOpen = false
    }

    private fun isLeftAltPressed(): Boolean {
        return InputConstants.isKeyDown(
            Minecraft.getInstance().window.window,
            GLFW.GLFW_KEY_LEFT_ALT
        )
    }

    private fun checkHover(
        data: TarotPlayerAttachment.Data,
        screenWidth: Int,
        screenHeight: Int,
        mouseX: Float,
        mouseY: Float
    ) {
        hoveredCardIndex = null

        val scaledCardWidth = TarotCardObject.CARD_WIDTH * CARD_SCALE
        val scaledCardHeight = TarotCardObject.CARD_HEIGHT * CARD_SCALE

        val hotbarWidth = 182
        val hotbarX = (screenWidth - hotbarWidth) / 2f
        val hotbarY = screenHeight - 22f

        val startX = hotbarX + hotbarWidth + HUD_PADDING

        val targetY = hotbarY - scaledCardHeight / 2f + 11f
        val startY = screenHeight.toFloat()
        val currentY = Mth.lerp(
            easeOutCubic(animationProgress),
            startY,
            targetY
        )

        for (i in data.drawnCards.indices) {
            val x = startX + i * (scaledCardWidth + CARD_SPACING)
            val y = currentY

            if (mouseX >= x && mouseX <= x + scaledCardWidth &&
                mouseY >= y && mouseY <= y + scaledCardHeight) {
                hoveredCardIndex = i
                break
            }
        }
    }

    private fun renderTarotCards(
        guiGraphics: GuiGraphics,
        data: TarotPlayerAttachment.Data,
        screenWidth: Int,
        screenHeight: Int,
        partialTick: Float
    ) {
        val poseStack = guiGraphics.pose()

        val scaledCardWidth = TarotCardObject.CARD_WIDTH * CARD_SCALE
        val scaledCardHeight = TarotCardObject.CARD_HEIGHT * CARD_SCALE

        val hotbarWidth = 182
        val hotbarX = (screenWidth - hotbarWidth) / 2f
        val hotbarY = screenHeight - 22f

        val startX = hotbarX + hotbarWidth + HUD_PADDING

        val targetY = hotbarY - scaledCardHeight / 2f
        val startY = screenHeight.toFloat()
        val currentY = Mth.lerp(
            easeOutCubic(animationProgress),
            startY,
            targetY
        )

        for (i in data.drawnCards.indices) {
            val cardNumber = data.drawnCards[i]
            val isReversed = data.reversedCards.getOrNull(i) ?: false

            val x = startX + i * (scaledCardWidth + CARD_SPACING)
            val y = currentY

            val isHovered = hoveredCardIndex == i
            renderMiniCard(guiGraphics, poseStack, cardNumber, isReversed, x, y, isHovered, partialTick)
        }
    }

    private fun renderMiniCard(
        guiGraphics: GuiGraphics,
        poseStack: PoseStack,
        cardNumber: Int,
        isReversed: Boolean,
        x: Float,
        y: Float,
        isHovered: Boolean,
        partialTick: Float
    ) {
        poseStack.pushPose()

        poseStack.translate(x.toDouble(), y.toDouble(), 0.0)
        poseStack.scale(CARD_SCALE, CARD_SCALE, 1f)

        if (isReversed) {
            poseStack.translate(
                (TarotCardObject.CARD_WIDTH / 2f).toDouble(),
                (TarotCardObject.CARD_HEIGHT / 2f).toDouble(),
                0.0
            )
            poseStack.mulPose(Axis.ZP.rotationDegrees(180f))
            poseStack.translate(
                -(TarotCardObject.CARD_WIDTH / 2f).toDouble(),
                -(TarotCardObject.CARD_HEIGHT / 2f).toDouble(),
                0.0
            )
        }

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        val alpha = if (isHovered) animationProgress else animationProgress * 0.85f
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha)

        val texture = getCardTexture(cardNumber)

        guiGraphics.blit(
            texture,
            0, 0,
            0f, 0f,
            TarotCardObject.CARD_WIDTH.toInt(),
            TarotCardObject.CARD_HEIGHT.toInt(),
            TarotCardObject.CARD_WIDTH.toInt(),
            TarotCardObject.CARD_HEIGHT.toInt()
        )

        if (isHovered) {
            RenderSystem.setShaderColor(1.0f, 0.84f, 0.0f, animationProgress) // Gold color
            guiGraphics.renderOutline(
                0, 0,
                TarotCardObject.CARD_WIDTH.toInt(),
                TarotCardObject.CARD_HEIGHT.toInt(),
                Color(255, 215, 0, (255 * animationProgress).toInt()).rgb
            )
        }

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.disableBlend()

        poseStack.popPose()
    }

    private fun renderCardTooltip(
        guiGraphics: GuiGraphics,
        cardNumber: Int,
        isReversed: Boolean,
        minecraft: Minecraft
    ) {
        val tooltip = mutableListOf<Component>()

        tooltip.add(
            Component.literal(TarotCardObject.getArcanaName(cardNumber, isReversed))
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD)
        )

        if (isReversed) {
            tooltip.add(Component.literal("Reversed").withStyle(ChatFormatting.RED, ChatFormatting.ITALIC))
        } else {
            tooltip.add(Component.literal("Upright").withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC))
        }

        val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
        if (effect != null) {
            tooltip.add(Component.literal(""))
            tooltip.add(effect.getDescription(isReversed))
        }

        val mouseX = (minecraft.mouseHandler.xpos() * minecraft.window.guiScaledWidth / minecraft.window.screenWidth).toInt()
        val mouseY = (minecraft.mouseHandler.ypos() * minecraft.window.guiScaledHeight / minecraft.window.screenHeight).toInt()

        guiGraphics.renderComponentTooltip(minecraft.font, tooltip, mouseX, mouseY)
    }

    private fun getCardTexture(cardNumber: Int): ResourceLocation {
        return Witchery.id("textures/gui/arcana/$cardNumber.png")
    }

    private fun easeOutCubic(t: Float): Float {
        val t1 = t - 1f
        return t1 * t1 * t1 + 1f
    }
}