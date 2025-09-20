package dev.sterner.witchery.item

import dev.sterner.witchery.block.ChaliceBlock
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class ChaliceBlockItem(block: Block, properties: Properties) : BlockItem(block, properties) {
    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        if (stack.get(WitcheryDataComponents.HAS_SOUP.get()) == true)
            tooltipComponents.add(Component.literal("Filled").withColor(0xFF0000))

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }

    override fun placeBlock(context: BlockPlaceContext, state: BlockState): Boolean {
        return super.placeBlock(
            context,
            state.setValue(ChaliceBlock.HAS_SOUP, context.itemInHand.get(WitcheryDataComponents.HAS_SOUP.get()) == true)
        )
    }
}