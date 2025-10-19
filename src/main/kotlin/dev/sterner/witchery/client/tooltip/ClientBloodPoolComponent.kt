package dev.sterner.witchery.client.tooltip

import dev.sterner.witchery.item.CaneSwordItem
import dev.sterner.witchery.registry.WitcheryDataComponents
import dev.sterner.witchery.core.util.RenderUtils
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.world.item.ItemStack

class ClientBloodPoolComponent(val stack: ItemStack) : ClientTooltipComponent {

    override fun getHeight(): Int {
        return 12
    }

    override fun getWidth(font: Font): Int {
        return 18
    }

    override fun renderImage(font: Font, x: Int, y: Int, guiGraphics: GuiGraphics) {
        val bl = stack.has(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get())
        if (bl) {
            val amount = stack.get(WitcheryDataComponents.CANE_BLOOD_AMOUNT.get()) ?: 0
            RenderUtils.innerRenderBlood(guiGraphics, CaneSwordItem.Companion.MAX_STORED_BLOOD, amount, y, x + 14)
        }

        super.renderImage(font, x, y, guiGraphics)
    }
}