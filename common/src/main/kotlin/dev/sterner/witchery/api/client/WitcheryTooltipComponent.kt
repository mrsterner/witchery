package dev.sterner.witchery.api.client

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent
import net.minecraft.world.inventory.tooltip.TooltipComponent

abstract class WitcheryTooltipComponent<T : WitcheryTooltipComponent<T, *>, C : ClientTooltipComponent> :
    TooltipComponent {
    abstract fun getClientTooltipComponent(): C
}