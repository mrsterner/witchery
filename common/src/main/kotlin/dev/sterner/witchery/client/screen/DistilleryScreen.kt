package dev.sterner.witchery.client.screen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.menu.DistilleryMenu
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory

class DistilleryScreen(menu: DistilleryMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<DistilleryMenu>(menu, inventory, title) {

    val texture: ResourceLocation = Witchery.id("textures/gui/distillery.png")
    val textureArrow: ResourceLocation = Witchery.id("textures/gui/distillery_arrow.png")

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val i = this.leftPos
        val j = this.topPos
        guiGraphics.blit(this.texture, i, j, 0, 0, this.imageWidth, this.imageHeight)

        val k = 40
        val l = Mth.ceil(menu.getBurnProgress() * k)
        guiGraphics.blit(this.textureArrow, i + 79 - 18 - 2 + 8, j + 34 - 8, 0f, 0f, l, 32, 40, 32)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        this.renderTooltip(guiGraphics, mouseX, mouseY)
    }
}