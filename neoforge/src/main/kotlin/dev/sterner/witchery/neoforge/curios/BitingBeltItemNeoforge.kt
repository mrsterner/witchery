package dev.sterner.witchery.neoforge.curios

import dev.sterner.witchery.item.accessories.BitingBeltItem
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import top.theillusivec4.curios.api.type.capability.ICurioItem

class BitingBeltItemNeoforge(settings: Properties?): Item(settings), BitingBeltItem, ICurioItem {

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {

        appendBitingHoverText(stack, context, tooltipComponents, tooltipFlag)

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}