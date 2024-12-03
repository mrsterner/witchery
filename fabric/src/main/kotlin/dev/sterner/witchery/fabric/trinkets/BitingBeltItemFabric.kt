package dev.sterner.witchery.fabric.trinkets

import dev.emi.trinkets.api.SlotReference
import dev.emi.trinkets.api.TrinketItem
import dev.sterner.witchery.item.accessories.BarkBeltItem
import dev.sterner.witchery.item.accessories.BitingBeltItem
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag

class BitingBeltItemFabric(settings: Properties?): TrinketItem(settings), BitingBeltItem {

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