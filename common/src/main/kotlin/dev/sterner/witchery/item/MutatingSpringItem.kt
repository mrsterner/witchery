package dev.sterner.witchery.item

import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class MutatingSpringItem(properties: Properties) : Item(properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos

        if (level.getBlockState(pos).`is`(Blocks.WHEAT)) {
            makeWormwood(level, pos)
            return InteractionResult.SUCCESS
        }
        if (level.getBlockState(pos).`is`(Blocks.CHEST)) {
            makeGrassper(level, pos)
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
        if (checkCardinal(level, pos, WitcheryBlocks.WISPY_COTTON.get()) && checkWaterDiagonals(level, pos)) {
            level.setBlockAndUpdate(pos, WitcheryBlocks.WORMWOOD_CROP.get().defaultBlockState())
            removeCardinal(level, pos)
            removeDiagonals(level, pos)
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
        }
    }

    private fun makeGrassper(level: Level, pos: BlockPos){
        if (checkCardinal(level, pos, Blocks.SHORT_GRASS) && level.getBlockState(pos.below()).`is`(Blocks.WATER)) {
            removeCardinal(level, pos)
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
            level.setBlockAndUpdate(pos.below(), Blocks.AIR.defaultBlockState())
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
            level.setBlockAndUpdate(pos.south(), WitcheryBlocks.GRASSPER.get().defaultBlockState())
            level.setBlockAndUpdate(pos.north(), WitcheryBlocks.GRASSPER.get().defaultBlockState())
            level.setBlockAndUpdate(pos.west(), WitcheryBlocks.GRASSPER.get().defaultBlockState())
            level.setBlockAndUpdate(pos.east(), WitcheryBlocks.GRASSPER.get().defaultBlockState())
        }
    }

    private fun checkCardinal(level: Level, pos: BlockPos, block: Block): Boolean {
        return level.getBlockState(pos.north()).`is`(block) &&
                level.getBlockState(pos.south()).`is`(block) &&
                level.getBlockState(pos.east()).`is`(block) &&
                level.getBlockState(pos.west()).`is`(block)
    }

    private fun checkWaterDiagonals(level: Level, pos: BlockPos): Boolean {
        return isWaterloggedOrWater(level.getBlockState(pos.north().east().below())) &&
                isWaterloggedOrWater(level.getBlockState(pos.north().west().below())) &&
                isWaterloggedOrWater(level.getBlockState(pos.south().east().below())) &&
                isWaterloggedOrWater(level.getBlockState(pos.south().west().below()))
    }

    private fun isWaterloggedOrWater(state: BlockState): Boolean {
        return state.`is`(Blocks.WATER) || (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(
            BlockStateProperties.WATERLOGGED
        ))
    }

    private fun removeDiagonals(level: Level, pos: BlockPos) {
        listOf(
            pos.north().east().below(),
            pos.north().west().below(),
            pos.south().east().below(),
            pos.south().west().below()
        ).forEach { diagonalPos ->
            val state = level.getBlockState(diagonalPos)
            if (state.hasProperty(BlockStateProperties.WATERLOGGED) && state.getValue(BlockStateProperties.WATERLOGGED)) {
                level.setBlockAndUpdate(diagonalPos, state.setValue(BlockStateProperties.WATERLOGGED, false))
            } else if (state.`is`(Blocks.WATER)) {
                level.setBlockAndUpdate(diagonalPos, Blocks.AIR.defaultBlockState())
            }
            val center = diagonalPos.center
            for (i in 0..16) {
                level.addAlwaysVisibleParticle(
                    ParticleTypes.SMOKE,
                    true,
                    center.x + 0.0 + Mth.nextDouble(level.random, -0.5, 0.5),
                    (center.y + 0.0) + Mth.nextDouble(level.random, -1.25, 1.25),
                    center.z + 0.0 + Mth.nextDouble(level.random, -0.5, 0.5),
                    0.0, 0.0, 0.0
                )
            }
        }
    }

    private fun removeCardinal(level: Level, pos: BlockPos) {
        listOf(
            pos.east(),
            pos.north(),
            pos.south(),
            pos.west()
        ).forEach { cardinal ->
            level.setBlockAndUpdate(cardinal, Blocks.AIR.defaultBlockState())
            val center = cardinal.center
            for (i in 0..16) {
                level.addAlwaysVisibleParticle(
                    ParticleTypes.SMOKE,
                    true,
                    center.x + Mth.nextDouble(level.random, -0.5, 0.5),
                    (center.y) + Mth.nextDouble(level.random, -1.25, 1.25),
                    center.z + Mth.nextDouble(level.random, -0.5, 0.5),
                    0.0, 0.0, 0.0
                )
            }
        }
    }
}