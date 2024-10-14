package dev.sterner.witchery.client.screen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.menu.AltarMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class AltarScreen(menu: AltarMenu, inventory: Inventory, title: Component) : AbstractContainerScreen<AltarMenu>(menu, NamelessInventory(inventory), title) {
    override fun isPauseScreen() = false

    init {
        this.imageWidth = 62
        this.imageHeight = 35
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val x = this.leftPos
        val y = this.topPos
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        // render current power, max power, multiplier
    }

    companion object {
        val TEXTURE = Witchery.id("textures/gui/altar.png")
    }

    class NamelessInventory(inventory: Inventory): Inventory(inventory.player) {
        override fun getDisplayName() = Component.literal("")
    }
}