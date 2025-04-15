package dev.sterner.witchery.api

import net.minecraft.world.item.ItemStack

class BloodPoolComponent(val stack: ItemStack) :
    WitcheryTooltipComponent<BloodPoolComponent, ClientBloodPoolComponent>() {
    override fun getClientTooltipComponent(): ClientBloodPoolComponent {
        return ClientBloodPoolComponent(stack)
    }
}