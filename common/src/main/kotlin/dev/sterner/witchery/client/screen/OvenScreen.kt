package dev.sterner.witchery.client.screen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.menu.OvenMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory

class OvenScreen(menu: OvenMenu, inventory: Inventory, title: Component) : AbstractContainerScreen<OvenMenu>(menu, inventory, title) {

    val litProgressSprite: ResourceLocation = ResourceLocation.withDefaultNamespace("container/furnace/lit_progress")
    val burnProgressSprite: ResourceLocation = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress")
    val texture: ResourceLocation = Witchery.id("textures/gui/oven.png")

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val i = this.leftPos
        val j = this.topPos
        guiGraphics.blit(this.texture, i, j, 0, 0, this.imageWidth, this.imageHeight)
        if (menu.isLit()) {
            val k = 14
            val l = Mth.ceil(menu.getLitProgress() * 13.0f) + 1
            guiGraphics.blitSprite(this.litProgressSprite, k, k, 0, k - l, i + 56 - 18 - 2, j + 36 + k - l, k, l)
        }
        val k = 24
        val l = Mth.ceil(menu.getBurnProgress() * k)
        guiGraphics.blitSprite(this.burnProgressSprite, 24, 16, 0, 0, i + 79 - 18 - 2, j + 34, l, 16)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        this.renderTooltip(guiGraphics, mouseX, mouseY)
    }
}