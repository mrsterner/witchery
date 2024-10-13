package dev.sterner.witchery.client.screen

import dev.sterner.witchery.menu.OvenMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.Items

class OvenScreen(menu: OvenMenu, inventory: Inventory, title: Component) : AbstractContainerScreen<OvenMenu>(menu, inventory, title) {

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        guiGraphics.renderItem(Items.EGG.defaultInstance, 16, 16)
    }
}