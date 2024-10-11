package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.widget.SlotWidget

class WitcherySlotWidget(stack: EmiIngredient?, x: Int, y: Int) : SlotWidget(stack, x, y) {

    override fun shouldDrawSlotHighlight(mouseX: Int, mouseY: Int): Boolean {
        return false
    }
}