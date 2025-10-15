package dev.sterner.witchery.client.tooltip

import dev.sterner.witchery.api.client.WitcheryTooltipComponent
import net.minecraft.world.item.ItemStack


class UrnTooltipComponent(val potions: List<ItemStack>) :
    WitcheryTooltipComponent<UrnTooltipComponent, ClientUrnTooltipComponent>() {
    override fun getClientTooltipComponent(): ClientUrnTooltipComponent {
        return ClientUrnTooltipComponent(potions)
    }
}
