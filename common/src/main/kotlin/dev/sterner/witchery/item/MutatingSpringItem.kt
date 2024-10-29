package dev.sterner.witchery.item

import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.block.Blocks

class MutatingSpringItem(properties: Properties) : Item(properties) {


    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos

        val blockState = level.getBlockState(pos)
        if (blockState.`is`(Blocks.GRASS_BLOCK)) {
            level.setBlockAndUpdate(pos, Blocks.MYCELIUM.defaultBlockState())
            return InteractionResult.SUCCESS
        } else if (blockState.`is`(Blocks.DIRT)) {
            level.setBlockAndUpdate(pos, Blocks.CLAY.defaultBlockState())
            return InteractionResult.SUCCESS
        } else if (blockState.`is`(Blocks.CLAY)) {
            level.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState())
            return InteractionResult.SUCCESS
        } else if (blockState.`is`(Blocks.MYCELIUM)) {
            level.setBlockAndUpdate(pos, Blocks.GRASS_BLOCK.defaultBlockState())
            return InteractionResult.SUCCESS
        }


        return super.useOn(context)
    }
}