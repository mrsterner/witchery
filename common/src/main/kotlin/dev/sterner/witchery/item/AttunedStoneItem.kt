package dev.sterner.witchery.item

import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import java.awt.Color

class AttunedStoneItem(properties: Properties) : Item(properties.fireResistant()) {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        if (stack.get(WitcheryDataComponents.ATTUNED.get()) == true) {
            tooltipComponents.add(Component.translatable("witchery.attuned.charged").setStyle(Style.EMPTY.withColor(Color(180,50,180).rgb)))
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}