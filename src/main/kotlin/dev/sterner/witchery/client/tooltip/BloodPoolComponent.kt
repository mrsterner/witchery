package dev.sterner.witchery.client.tooltip

import dev.sterner.witchery.core.api.client.WitcheryTooltipComponent
import net.minecraft.world.item.ItemStack

class BloodPoolComponent(val stack: ItemStack) :
    WitcheryTooltipComponent<BloodPoolComponent, ClientBloodPoolComponent>() {
    override fun getClientTooltipComponent(): ClientBloodPoolComponent {
        return ClientBloodPoolComponent(stack)
    }
}