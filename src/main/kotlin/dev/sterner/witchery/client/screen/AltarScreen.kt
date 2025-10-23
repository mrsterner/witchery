package dev.sterner.witchery.client.screen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.menu.AltarMenu
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.entity.player.Inventory

class AltarScreen(menu: AltarMenu, inventory: Inventory, title: Component) : AbstractContainerScreen<AltarMenu>(
    menu, NamelessInventory(inventory), Component.empty().append(title).withStyle(ChatFormatting.WHITE)
) {

    override fun isPauseScreen() = false

    init {
        // Scale is weirdly wierd
        this.imageWidth = 256
        this.imageHeight = 144
        this.titleLabelY += 32
    }

    override fun init() {
        super.init()

        this.titleLabelX = imageWidth / 2 - font.width(title) / 2
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val x = this.leftPos
        val y = this.topPos
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight)
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        super.render(guiGraphics, mouseX, mouseY, partialTick)

        guiGraphics.drawCenteredString(
            font, POWER_FORMAT.format(menu.getCurrentPower(), menu.getMaxPower(), menu.altar?.powerMultiplier ?: 1),
            this.width / 2, this.height / 2 - font.lineHeight / 2, ChatFormatting.WHITE.color ?: 0xFFFFFF
        )
    }

    companion object {
        val TEXTURE = Witchery.id("textures/gui/altar.png")

        const val POWER_FORMAT = "%s/%s (%sx)"
    }

    class NamelessInventory(inventory: Inventory) : Inventory(inventory.player) {
        override fun getDisplayName(): MutableComponent = Component.literal("")
    }
}