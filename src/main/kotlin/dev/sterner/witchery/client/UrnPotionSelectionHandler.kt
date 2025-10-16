package dev.sterner.witchery.client

import com.mojang.blaze3d.platform.InputConstants
import dev.sterner.witchery.item.LeonardsUrnItem
import dev.sterner.witchery.item.QuartzSphereItem
import dev.sterner.witchery.payload.SelectUrnPotionC2SPayload
import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor
import org.lwjgl.glfw.GLFW
import java.awt.Color

object UrnPotionSelectionHandler {

    private var isMenuOpen = false
    private var selectedIndex = 0
    private var animationProgress = 0f
    private val ANIMATION_SPEED = 0.15f

    fun tick(minecraft: Minecraft) {
        val player = minecraft.player ?: return
        val heldItem = player.mainHandItem

        val shouldBeOpen = heldItem.item is QuartzSphereItem &&
                LeonardsUrnItem.hasUrn(player) &&
                isLeftAltPressed()

        if (shouldBeOpen && !isMenuOpen) {
            openMenu(player)
        } else if (!shouldBeOpen && isMenuOpen) {
            closeMenu()
        }

        if (isMenuOpen && animationProgress < 1f) {
            animationProgress = minOf(1f, animationProgress + ANIMATION_SPEED)
        } else if (!isMenuOpen && animationProgress > 0f) {
            animationProgress = maxOf(0f, animationProgress - ANIMATION_SPEED)
        }
    }

    fun onMouseScroll(delta: Double): Boolean {
        if (!isMenuOpen) return false

        val player = Minecraft.getInstance().player ?: return false
        val urn = LeonardsUrnItem.findUrn(player) ?: return false
        val potions = LeonardsUrnItem.getStoredPotions(urn)

        if (potions.isEmpty()) return false

        if (delta > 0) {
            selectedIndex = (selectedIndex - 1 + potions.size) % potions.size
        } else if (delta < 0) {
            selectedIndex = (selectedIndex + 1) % potions.size
        }

        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3f, 1.5f)
        return true
    }

    private fun openMenu(player: Player) {
        isMenuOpen = true
        selectedIndex = 0
        animationProgress = 0f
    }

    private fun closeMenu() {
        if (isMenuOpen && selectedIndex >= 0) {
            val packet = SelectUrnPotionC2SPayload(selectedIndex)
            PacketDistributor.sendToServer(packet)
        }
        isMenuOpen = false
    }

    private fun isLeftAltPressed(): Boolean {
        return InputConstants.isKeyDown(
            Minecraft.getInstance().window.window,
            GLFW.GLFW_KEY_LEFT_ALT
        )
    }

    fun render(guiGraphics: GuiGraphics, partialTick: DeltaTracker) {
        if (animationProgress <= 0f) return

        val minecraft = Minecraft.getInstance()
        val player = minecraft.player ?: return
        val urn = LeonardsUrnItem.findUrn(player) ?: return
        val potions = LeonardsUrnItem.getStoredPotions(urn)

        if (potions.isEmpty()) return

        val window = minecraft.window
        val screenWidth = window.guiScaledWidth
        val screenHeight = window.guiScaledHeight

        val targetY = screenHeight / 2 + 25
        val startY = screenHeight + 20
        val currentY = Mth.lerp(
            easeOutCubic(animationProgress),
            startY.toFloat(),
            targetY.toFloat()
        ).toInt()

        val itemSize = 18
        val spacing = 4
        val padding = 8

        val contentWidth = 3 * itemSize + (potions.size - 1) * spacing
        val panelWidth = contentWidth + padding * 2
        val panelHeight = itemSize + padding * 2 + 10

        val panelX = screenWidth / 2 - panelWidth / 2
        val panelY = currentY

        guiGraphics.fill(
            panelX,
            panelY,
            panelX + panelWidth,
            panelY + panelHeight,
            Color(80, 65, 50).rgb
        )

        guiGraphics.renderOutline(
            panelX,
            panelY,
            panelWidth,
            panelHeight,
            Color(210, 180, 140).rgb
        )

        val title = Component.translatable("gui.witchery.select_potion")
        guiGraphics.drawCenteredString(
            minecraft.font,
            title,
            screenWidth / 2,
            panelY + 4,
            Color(220, 200, 170).rgb
        )

        val startX = screenWidth / 2 - contentWidth / 2
        val itemY = panelY + 18

        for ((index, potion) in potions.withIndex()) {
            val itemX = startX + index * (itemSize + spacing)
            val isSelected = index == selectedIndex

            if (isSelected) {
                guiGraphics.fill(
                    itemX - 1,
                    itemY - 1,
                    itemX + itemSize + 1,
                    itemY + itemSize + 1,
                    0xFFFFAA00.toInt()
                )

                guiGraphics.fill(
                    itemX,
                    itemY,
                    itemX + itemSize,
                    itemY + itemSize,
                    0x44FFAA00.toInt()
                )
            }

            guiGraphics.renderItem(potion, itemX, itemY)
            guiGraphics.renderItemDecorations(minecraft.font, potion, itemX, itemY)

            val numberText = "${index + 1}"
            val numX = itemX + itemSize - minecraft.font.width(numberText) - 1
            val numY = itemY + itemSize - 8

            guiGraphics.drawString(
                minecraft.font,
                numberText,
                numX + 1,
                numY + 1,
                0x000000,
                false
            )
            guiGraphics.drawString(
                minecraft.font,
                numberText,
                numX,
                numY,
                if (isSelected) 0xFFFFFF else 0xAAAAAA,
                false
            )
        }
    }

    private fun easeOutCubic(t: Float): Float {
        val t1 = t - 1f
        return t1 * t1 * t1 + 1f
    }
}