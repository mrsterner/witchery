package dev.sterner.witchery.client.tooltip

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.world.item.ItemStack
import java.awt.Color

class ClientUrnTooltipComponent(private val potions: List<ItemStack>) : ClientTooltipComponent {

    override fun getHeight(): Int {
        return this.gridSizeY() * 20 + 2 + 4
    }

    override fun getWidth(font: Font): Int {
        return this.gridSizeX() * 18 + 2
    }

    override fun renderImage(font: Font, x: Int, y: Int, guiGraphics: GuiGraphics) {
        val gridX = this.gridSizeX()
        val gridY = this.gridSizeY()

        this.drawBorder(guiGraphics, x, y, gridX, gridY)

        var index = 0
        for (row in 0 until gridY) {
            for (col in 0 until gridX) {
                val itemX = x + col * 18 + 1
                val itemY = y + row * 20 + 1

                if (index >= potions.size) break

                this.renderSlot(guiGraphics, itemX, itemY, potions[index], font)
                index++
            }
        }
    }

    private fun renderSlot(guiGraphics: GuiGraphics, x: Int, y: Int, stack: ItemStack, font: Font) {
        this.drawSlot(guiGraphics, x, y)

        guiGraphics.renderItem(stack, x + 1, y + 1, 0)
        guiGraphics.renderItemDecorations(font, stack, x + 1, y + 1)
    }

    private fun drawBorder(guiGraphics: GuiGraphics, x: Int, y: Int, gridX: Int, gridY: Int) {
        guiGraphics.fill(x, y, x + gridX * 18 + 2, y + gridY * 20 + 2, Color(210, 180, 140).rgb)
    }

    private fun drawSlot(guiGraphics: GuiGraphics, x: Int, y: Int) {
        guiGraphics.fill(x, y, x + 18, y + 20, Color(139, 115, 85).rgb)
    }

    private fun gridSizeX(): Int {
        return when {
            potions.isEmpty() -> 0
            potions.size <= 3 -> potions.size
            else -> 3
        }
    }

    private fun gridSizeY(): Int {
        return when {
            potions.isEmpty() -> 0
            else -> (potions.size + 2) / 3
        }
    }
}