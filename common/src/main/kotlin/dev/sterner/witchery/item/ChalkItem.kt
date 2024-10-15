package dev.sterner.witchery.item

import dev.sterner.witchery.block.ritual.GoldenChalkBlock
import dev.sterner.witchery.block.ritual.RitualChalkBlock
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Block

class ChalkItem(block: Block, properties: Properties) : ItemNameBlockItem(block, properties) {
    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val item = (context.itemInHand.item as ChalkItem)
        val state = level.getBlockState(pos)

        if (level.isClientSide) return InteractionResult.sidedSuccess(true)

        if (state.block is RitualChalkBlock && state.`is`(item.block)) {
            level.setBlockAndUpdate(pos, state.setValue(RitualChalkBlock.VARIANT,
                context.level.random.nextIntBetweenInclusive(0, RitualChalkBlock.VARIANTS)))
            return InteractionResult.CONSUME
        } else if (state.block is GoldenChalkBlock && state.`is`(item.block))
            return InteractionResult.FAIL

        return super.useOn(context)
    }
}