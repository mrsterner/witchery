package dev.sterner.witchery.item

import dev.sterner.witchery.entity.BroomEntity
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.UseOnContext
import java.awt.Color

class BroomItem(properties: Properties) : Item(properties) {


    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val item = context.itemInHand
        val dir = context.clickedFace
        val pos = context.clickedPos

        if (item.get(WitcheryDataComponents.HAS_OINTMENT.get()) == true) {
            val broomEntity = BroomEntity(level)
            val vec = pos.relative(dir)
            broomEntity.moveTo(vec.center)
            level.addFreshEntity(broomEntity)
            item.shrink(1)
            return InteractionResult.SUCCESS
        }

        return super.useOn(context)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        if (stack.get(WitcheryDataComponents.HAS_OINTMENT.get()) == true) {
            tooltipComponents.add(
                Component.translatable("witchery.infusion.ointment")
                    .setStyle(Style.EMPTY.withColor(Color(250, 250, 100).rgb))
            )
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}