package dev.sterner.witchery.api.client

import net.minecraft.world.item.ItemStack

class BloodPoolComponent(val stack: ItemStack) :
    WitcheryTooltipComponent<BloodPoolComponent, ClientBloodPoolComponent>() {
    override fun getClientTooltipComponent(): ClientBloodPoolComponent {
        return ClientBloodPoolComponent(stack)
    }
}