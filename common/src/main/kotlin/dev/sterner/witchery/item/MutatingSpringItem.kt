package dev.sterner.witchery.item

import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

class MutatingSpringItem(properties: Properties) : Item(properties) {


    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos

        if (level.getBlockState(pos).`is`(Blocks.WHEAT)) {
            makeWormwood(level, pos)
            return InteractionResult.SUCCESS
        }

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

    private fun makeWormwood(level: Level, pos: BlockPos) {
        if (checkCardinal(level, pos, WitcheryBlocks.WISPY_COTTON.get()) && checkDiagonals(level, pos, Blocks.WATER)) {
            level.setBlockAndUpdate(pos, WitcheryBlocks.WORMWOOD_CROP.get().defaultBlockState())
            removeCardinal(level, pos)
            removeDiagonals(level, pos)
        }
    }

    private fun checkCardinal(level: Level, pos: BlockPos, block: Block): Boolean {
        return level.getBlockState(pos.north()).`is`(block) &&
                level.getBlockState(pos.south()).`is`(block) &&
                level.getBlockState(pos.east()).`is`(block) &&
                level.getBlockState(pos.west()).`is`(block)
    }

    private fun checkDiagonals(level: Level, pos: BlockPos, block: Block): Boolean {
        return level.getBlockState(pos.north().east().below()).`is`(block) &&
                level.getBlockState(pos.north().west().below()).`is`(block) &&
                level.getBlockState(pos.south().east().below()).`is`(block) &&
                level.getBlockState(pos.south().west().below()).`is`(block)
    }

    private fun removeCardinal(level: Level, pos: BlockPos) {
        level.setBlockAndUpdate(pos.north(), Blocks.AIR.defaultBlockState())
        level.setBlockAndUpdate(pos.south(), Blocks.AIR.defaultBlockState())
        level.setBlockAndUpdate(pos.east(), Blocks.AIR.defaultBlockState())
        level.setBlockAndUpdate(pos.west(), Blocks.AIR.defaultBlockState())
    }

    private fun removeDiagonals(level: Level, pos: BlockPos) {
        level.setBlockAndUpdate(pos.north().east().below(), Blocks.AIR.defaultBlockState())
        level.setBlockAndUpdate(pos.north().west().below(), Blocks.AIR.defaultBlockState())
        level.setBlockAndUpdate(pos.south().east().below(), Blocks.AIR.defaultBlockState())
        level.setBlockAndUpdate(pos.south().west().below(), Blocks.AIR.defaultBlockState())
    }
}