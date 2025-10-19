package dev.sterner.witchery.content.item

import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext

class ParasiticLouseItem(properties: Properties) : Item(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val leech = WitcheryEntityTypes.PARASITIC_LOUSE.get().create(level)
        val data = context.itemInHand.get(WitcheryDataComponents.LEECH_EFFECT.get())
        if (data != null) {
            leech!!.effect = data
        }

        leech!!.moveTo(context.clickLocation)
        level.addFreshEntity(leech)

        context.itemInHand.shrink(1)

        return InteractionResult.SUCCESS
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        if (stack.has(WitcheryDataComponents.LEECH_EFFECT.get())) {
            tooltipComponents.add(Component.translatable(stack.get(WitcheryDataComponents.LEECH_EFFECT.get())!!.descriptionId))
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}