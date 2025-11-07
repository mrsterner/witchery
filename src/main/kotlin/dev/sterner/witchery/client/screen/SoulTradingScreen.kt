package dev.sterner.witchery.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.math.Axis
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.menu.SoulTradingMenu
import dev.sterner.witchery.core.util.RenderUtils
import dev.sterner.witchery.network.SelectSoulTradeC2SPayload
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.ImageButton
import net.minecraft.client.gui.components.WidgetSprites
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.network.PacketDistributor
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

class SoulTradingScreen(menu: SoulTradingMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<SoulTradingMenu>(menu, inventory, title) {

    private val texture: ResourceLocation = Witchery.id("textures/gui/soul_trading.png")
    private val soulHeadsTexture: ResourceLocation = Witchery.id("textures/gui/soul_heads.png")

    private var tradeScrollOffset = 0
    private var soulScrollOffset = 0

    private val tradeColumnX = 22
    private val tradeColumnY = 10
    private val tradeColumnWidth = 16
    private val tradeColumnHeight = 65

    private val soulColumnX = 145
    private val soulColumnY = 10
    private val soulColumnWidth = 18
    private val soulColumnHeight = 65

    private val scaleStandX = 176
    private val scaleStandY = 32
    private val scaleStandWidth = 20
    private val scaleStandHeight = 48

    private val scaleArmX = 176
    private val scaleArmY = 0
    private val scaleArmWidth = 56
    private val scaleArmHeight = 8

    private val scaleBowlX = 176
    private val scaleBowlY = 9
    private val scaleBowlWidth = 21
    private val scaleBowlHeight = 23

    private var currentRotation = 0f
    private var targetRotation = 0f
    private val rotationSpeed = 0.15f

    private var isDraggingTradeScroll = false

    companion object {
        val INCREMENT_SPRITE: WidgetSprites = WidgetSprites(
            Witchery.id("increment_button_highlighted"),
            Witchery.id("increment_button")
        )
        val DECREMENT_SPRITE: WidgetSprites = WidgetSprites(
            Witchery.id("decrement_button_highlighted"),
            Witchery.id("decrement_button")
        )
        val CONFIRM_SPRITE: WidgetSprites = WidgetSprites(
            Witchery.id("confirm_button_highlighted"),
            Witchery.id("confirm_button")
        )
        val SCROLLER_SPRITE: ResourceLocation = ResourceLocation.withDefaultNamespace("container/villager/scroller")
    }

    var tradeSlotButtons = mutableSetOf<Pair<ImageButton, ImageButton>>()

    init {
        this.imageWidth = 176
        this.imageHeight = 166
    }

    override fun init() {
        super.init()
        tradeSlotButtons.clear()

        for (slotIndex in 0 until 4) {
            val slotX = leftPos + 8
            val slotY = topPos + 10 + slotIndex * 16

            val plusButton = ImageButton(slotX, slotY, 13, 7, INCREMENT_SPRITE) {
                val index = tradeScrollOffset + slotIndex
                if (index >= menu.availableTrades.size) return@ImageButton

                PacketDistributor.sendToServer(
                    SelectSoulTradeC2SPayload(
                        SelectSoulTradeC2SPayload.Action.INCREMENT_AMOUNT,
                        hasShiftDown(),
                        index
                    )
                )
            }

            val minusButton = ImageButton(slotX, slotY + 8, 13, 7, DECREMENT_SPRITE) {
                val index = tradeScrollOffset + slotIndex
                if (index >= menu.availableTrades.size) return@ImageButton

                PacketDistributor.sendToServer(
                    SelectSoulTradeC2SPayload(
                        SelectSoulTradeC2SPayload.Action.DECREMENT_AMOUNT,
                        hasShiftDown(),
                        index
                    )
                )
            }

            addRenderableWidget(plusButton)
            addRenderableWidget(minusButton)
            tradeSlotButtons.add(Pair(plusButton, minusButton))
        }

        val confirmX = leftPos + 125
        val confirmY = topPos + 54
        addRenderableWidget(ImageButton(confirmX, confirmY, 13, 7, CONFIRM_SPRITE) {
            if (menu.canMakeTrade()) {
                PacketDistributor.sendToServer(
                    SelectSoulTradeC2SPayload(SelectSoulTradeC2SPayload.Action.CONFIRM_TRADE, false, 0)
                )
            }
        })
    }


    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun containerTick() {
        super.containerTick()

        val soul = menu.getSelectedSoul()
        val totalCost = menu.getTotalSoulCost()
        targetRotation = calculateScaleRotation(totalCost, soul)

        val diff = targetRotation - currentRotation
        currentRotation += diff * rotationSpeed

        if (abs(diff) < 0.01f) {
            currentRotation = targetRotation
        }
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, texture)

        val i = this.leftPos
        val j = this.topPos
        guiGraphics.blit(this.texture, i, j, 0f, 0f, 176, 166, 256, 256)

        renderTradeColumn(guiGraphics, i, j, mouseX, mouseY)
        renderSoulColumn(guiGraphics, i, j, mouseX, mouseY)
        renderScale(guiGraphics, i, j, partialTick)
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX + 48 + 5, this.titleLabelY, 4210752, false)
    }

    private fun renderTradeColumn(guiGraphics: GuiGraphics, screenX: Int, screenY: Int, mouseX: Int, mouseY: Int) {
        val trades = menu.availableTrades
        val maxVisible = 4
        val itemSize = 16

        for (i in 0 until maxVisible.coerceAtMost(trades.size - tradeScrollOffset)) {
            val index = i + tradeScrollOffset
            if (index >= trades.size) break

            val trade = trades[index]
            val x = screenX + tradeColumnX
            val y = screenY + tradeColumnY + i * itemSize

            val selectedTrade = menu.selectedTrades.find { it.tradeIndex == index }

            guiGraphics.renderItem(trade.output, x, y)

            if (selectedTrade != null) {
                guiGraphics.renderItemDecorations(font, trade.output, x, y, selectedTrade.amount.toString())
            }
        }

        if (trades.size > maxVisible) {
            val scrollBarX = screenX + tradeColumnX + tradeColumnWidth + 2
            val scrollBarY = screenY + tradeColumnY
            val scrollBarHeight = tradeColumnHeight
            val scrollHandleHeight = 27
            val totalScrollable = (trades.size - maxVisible).coerceAtLeast(1)
            val scrollProgress = tradeScrollOffset.toFloat() / totalScrollable

            val handleY = scrollBarY + (scrollProgress * (scrollBarHeight - scrollHandleHeight)).toInt()
            guiGraphics.blitSprite(SCROLLER_SPRITE, scrollBarX, handleY + scrollHandleHeight - 27, 0, 6, 27)
        }
    }

    private fun renderSoulColumn(guiGraphics: GuiGraphics, screenX: Int, screenY: Int, mouseX: Int, mouseY: Int) {
        val souls = menu.availableSouls
        val maxVisible = 4
        val itemSize = 16

        for (i in 0 until maxVisible.coerceAtMost(souls.size - soulScrollOffset)) {
            val index = i + soulScrollOffset
            if (index >= souls.size) break

            val soul = souls[index]
            val x = screenX + soulColumnX
            val y = screenY + soulColumnY + i * itemSize

            val headIndex = getSoulHeadIndex(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(soul.entityType)))
            val headU = (headIndex % 8) * 16
            val headV = (headIndex / 8) * 16

            RenderUtils.blitWithAlpha(guiGraphics.pose(), soulHeadsTexture, x.toFloat(), y.toFloat(), headU.toFloat(), headV.toFloat(), 16, 16, 128, 128)
        }

        if (souls.size > maxVisible) {
            val scrollBarX = screenX + soulColumnX + soulColumnWidth + 2
            val scrollBarY = screenY + soulColumnY
            val scrollBarHeight = soulColumnHeight
            val scrollHandleHeight = 27
            val scrollProgress = soulScrollOffset.toFloat() / (souls.size - maxVisible - 1)
            val handleY = scrollBarY + (scrollProgress * (scrollBarHeight - scrollHandleHeight)).toInt()

            guiGraphics.blitSprite(SCROLLER_SPRITE, scrollBarX, handleY + scrollHandleHeight - 27, 0, 6, 27)
        }
    }

    private fun renderScale(guiGraphics: GuiGraphics, screenX: Int, screenY: Int, partialTick: Float) {
        val standX = screenX + 79
        val standY = screenY + 20

        guiGraphics.blit(texture, standX, standY, scaleStandX.toFloat(), scaleStandY.toFloat(), scaleStandWidth, scaleStandHeight, 256, 256)

        val soul = menu.getSelectedSoul()
        val rotation = currentRotation + (targetRotation - currentRotation) * partialTick * rotationSpeed

        val armCenterX = standX + scaleStandWidth / 2
        val armCenterY = standY + 10

        renderRotatedArm(guiGraphics, armCenterX, armCenterY, rotation)

        val armLength = scaleArmWidth / 2 - 10
        val rad = Math.toRadians(-rotation.toDouble())
        val xSwingFactor = 0.2
        val dx = armLength.toDouble()
        val dy = 0.0

        val leftBowlX = (armCenterX - dx * cos(rad) - dy * sin(rad) + sin(rad) * armLength * xSwingFactor).toFloat()
        val leftBowlY = ((armCenterY - dx * sin(rad) + dy * cos(rad)) + (scaleBowlHeight / 2) + 2).toFloat()

        val rightBowlX = (armCenterX + dx * cos(rad) + dy * sin(rad) - sin(rad) * armLength * xSwingFactor).toFloat()
        val rightBowlY = ((armCenterY + dx * sin(rad) - dy * cos(rad)) + (scaleBowlHeight / 2) + 2).toFloat()

        RenderUtils.blitWithAlpha(guiGraphics.pose(), texture, leftBowlX - scaleBowlWidth / 2f, leftBowlY - scaleBowlHeight / 2f, 200f, scaleBowlX.toFloat(), scaleBowlY.toFloat(), scaleBowlWidth, scaleBowlHeight, 256, 256)
        RenderUtils.blitWithAlpha(guiGraphics.pose(), texture, rightBowlX - scaleBowlWidth / 2f, rightBowlY - scaleBowlHeight / 2f, 200f, scaleBowlX.toFloat(), scaleBowlY.toFloat(), scaleBowlWidth, scaleBowlHeight, 256, 256)

        val selectedTrades = menu.selectedTrades
        if (selectedTrades.isNotEmpty()) {
            val verticalSpacing = 4f
            val horizontalSpacing = 4f
            val startY = leftBowlY - 4f

            for ((idx, selected) in selectedTrades.withIndex().reversed()) {
                if (selected.tradeIndex < menu.availableTrades.size) {
                    val trade = menu.availableTrades[selected.tradeIndex]

                    val offsetY = startY - idx * verticalSpacing
                    val offsetX = leftBowlX - 8f + (idx % 2) * horizontalSpacing - horizontalSpacing / 2f

                    RenderUtils.renderItemWithAlpha(guiGraphics, trade.output, offsetX, offsetY, 1f)
                    RenderUtils.renderItemDecorations(guiGraphics, font, trade.output, offsetX, offsetY)
                }
            }
        }


        if (soul != null) {
            val headIndex = getSoulHeadIndex(BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(soul.entityType)))
            val headU = (headIndex % 8) * 16
            val headV = (headIndex / 8) * 16
            RenderUtils.blitWithAlpha(guiGraphics.pose(), soulHeadsTexture, rightBowlX - 8f, rightBowlY - 3f, headU.toFloat(), headV.toFloat(), 16, 16, 128, 128)
        }
    }

    private fun renderRotatedArm(guiGraphics: GuiGraphics, centerX: Int, centerY: Int, rotation: Float) {
        val poseStack = guiGraphics.pose()
        poseStack.pushPose()
        poseStack.translate(centerX.toDouble(), centerY.toDouble(), 0.0)
        poseStack.mulPose(Axis.ZN.rotationDegrees(rotation))
        poseStack.translate(-scaleArmWidth / 2.0, -scaleArmHeight / 2.0, 0.0)

        guiGraphics.blit(texture, 0, 0, scaleArmX.toFloat(), scaleArmY.toFloat(), scaleArmWidth, scaleArmHeight, 256, 256)

        poseStack.popPose()
    }

    private fun calculateScaleRotation(totalCost: Int, soul: SoulTradingMenu.SoulData?): Float {
        if (totalCost == 0 && soul == null) return 0f
        if (totalCost == 0) return -25f
        if (soul == null) return 25f

        val weightDiff = totalCost - soul.weight
        val sensitivity = 2.0f
        val maxRotation = 25f

        val rotation = weightDiff * sensitivity
        return rotation.coerceIn(-maxRotation, maxRotation)
    }


    private fun getSoulHeadIndex(entityType: EntityType<*>): Int {
        return when (entityType) {
            EntityType.VILLAGER -> 0
            EntityType.PILLAGER -> 1
            EntityType.VINDICATOR -> 1
            else -> 2
        }
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        val relX = mouseX - leftPos
        val relY = mouseY - topPos

        val trades = menu.availableTrades
        val maxVisible = 4

        if (trades.size > maxVisible) {
            val scrollBarX = tradeColumnX + tradeColumnWidth + 2
            val scrollBarY = tradeColumnY
            val scrollBarHeight = tradeColumnHeight
            val scrollHandleHeight = 27

            val totalScrollable = (trades.size - maxVisible).coerceAtLeast(1)
            val scrollProgress = tradeScrollOffset.toFloat() / totalScrollable
            val handleY = scrollBarY + (scrollProgress * (scrollBarHeight - scrollHandleHeight))

            if (relX in scrollBarX.toDouble()..(scrollBarX + 6.0) &&
                relY in handleY..(handleY + scrollHandleHeight)) {
                isDraggingTradeScroll = true
                return true
            }
        }

        if (relX >= soulColumnX && relX <= soulColumnX + soulColumnWidth &&
            relY >= soulColumnY && relY <= soulColumnY + soulColumnHeight) {
            val index = ((relY - soulColumnY) / 16).toInt() + soulScrollOffset
            if (index < menu.availableSouls.size) {
                PacketDistributor.sendToServer(
                    SelectSoulTradeC2SPayload(SelectSoulTradeC2SPayload.Action.SELECT_SOUL, false, index)
                )
                return true
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        if (isDraggingTradeScroll) {
            val trades = menu.availableTrades
            val maxVisible = 4
            if (trades.size > maxVisible) {
                val scrollBarHeight = tradeColumnHeight
                val scrollHandleHeight = 27
                val totalScrollable = (trades.size - maxVisible).coerceAtLeast(1)

                val deltaY = mouseY - topPos - tradeColumnY - (scrollHandleHeight / 2.0)
                val progress = (deltaY / (scrollBarHeight - scrollHandleHeight))
                    .toFloat()
                    .coerceIn(0f, 1f)

                tradeScrollOffset = (progress * totalScrollable).toInt()
            }
            return true
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (isDraggingTradeScroll) {
            isDraggingTradeScroll = false
            return true
        }
        return super.mouseReleased(mouseX, mouseY, button)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, scrollX: Double, scrollY: Double): Boolean {
        val relX = mouseX - leftPos
        val relY = mouseY - topPos

        if (relX >= tradeColumnX && relX <= tradeColumnX + tradeColumnWidth + 8 &&
            relY >= tradeColumnY && relY <= tradeColumnY + tradeColumnHeight) {
            tradeScrollOffset = (tradeScrollOffset - scrollY.toInt()).coerceIn(0, (menu.availableTrades.size - 4).coerceAtLeast(0))
            return true
        }

        if (relX >= soulColumnX && relX <= soulColumnX + soulColumnWidth + 8 &&
            relY >= soulColumnY && relY <= soulColumnY + soulColumnHeight) {
            soulScrollOffset = (soulScrollOffset - scrollY.toInt()).coerceIn(0, (menu.availableSouls.size - 4).coerceAtLeast(0))
            return true
        }

        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        this.renderTooltip(guiGraphics, mouseX, mouseY)
    }
}