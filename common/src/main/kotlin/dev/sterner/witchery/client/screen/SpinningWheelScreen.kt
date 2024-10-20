package dev.sterner.witchery.client.screen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.menu.DistilleryMenu
import dev.sterner.witchery.menu.SpinningWheelMenu
import dev.sterner.witchery.recipe.spinning_wheel.SpinningWheelRecipe
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory

class SpinningWheelScreen(menu: SpinningWheelMenu, inventory: Inventory, title: Component) :
    AbstractContainerScreen<SpinningWheelMenu>(menu, inventory, title) {

    val texture: ResourceLocation = Witchery.id("textures/gui/spinning_wheel.png")
    val textureArrow: ResourceLocation = Witchery.id("textures/gui/spinning_wheel_arrow.png")

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val i = this.leftPos
        val j = this.topPos
        guiGraphics.blit(this.texture, i, j, 0, 0, this.imageWidth, this.imageHeight)

        val k = 48
        val l = Mth.ceil(menu.getBurnProgress() * k)
        guiGraphics.blit(this.textureArrow, i + 79 - 18 - 2 + 8 - 4, j + 34 - 8 - 6, 0f, 0f, l, 44, 48, 44)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)
        this.renderTooltip(guiGraphics, mouseX, mouseY)
    }
}