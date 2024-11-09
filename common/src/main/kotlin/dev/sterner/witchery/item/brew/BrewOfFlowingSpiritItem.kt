package dev.sterner.witchery.item.brew

import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlockEntity
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DoorBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf
import net.minecraft.world.phys.BlockHitResult

class BrewOfFlowingSpiritItem(color: Int, properties: Properties) : ThrowableBrewItem(color, properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val clickedPos = context.clickedPos
        val state = level.getBlockState(clickedPos)

        if (lookForDoors(level, state, clickedPos)) {
            return InteractionResult.SUCCESS
        }

        return super.useOn(context)
    }

    private fun makePortal(level: Level, pos: BlockPos, direction: Direction) {

        SpiritPortalBlock.STRUCTURE.get().placeNoContext(level, pos, direction)

        level.setBlockAndUpdate(
            pos,
            WitcheryBlocks.SPIRIT_PORTAL.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, direction.opposite)
        )

        if (level.getBlockEntity(pos) is MultiBlockComponentBlockEntity) {
            (level.getBlockEntity(pos) as MultiBlockComponentBlockEntity).corePos = pos
        }
    }

    override fun applyEffectOnBlock(level: Level, blockHit: BlockHitResult) {
        var pos = blockHit.blockPos

        for (x in pos.x - 1 until pos.x + 1) {
            for (z in pos.z - 1 until pos.z + 1) {
                val state = level.getBlockState(BlockPos(x, pos.y, z))
                if (state.block is DoorBlock && state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
                    if (lookForDoors(level, state, BlockPos(x, pos.y, z))) {
                        return
                    }
                }
            }
        }

        if (level.getBlockState(pos).canBeReplaced()) {
            level.setBlockAndUpdate(pos, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get().defaultBlockState())
        } else {
            pos = pos.relative(blockHit.direction)
            level.setBlockAndUpdate(pos, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get().defaultBlockState())
        }
    }

    private fun lookForDoors(level: Level, state: BlockState, pos: BlockPos): Boolean {
        if (!WitcheryApi.isInSpiritWorld(level)) {
            return false
        }

        if (state.block is DoorBlock && state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)) {
            val half = state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF)
            val direction = state.getValue(BlockStateProperties.HORIZONTAL_FACING)

            val belowPops = if (half == DoubleBlockHalf.UPPER) pos.below() else pos
            if (level.getBlockState(belowPops.east()).block is DoorBlock) {
                when (direction) {
                    Direction.NORTH -> {
                        makePortal(level, belowPops, direction.opposite)
                    }
                    Direction.SOUTH -> {
                        makePortal(level, belowPops.east(), direction.opposite)
                    }
                    else -> {
                        makePortal(level, belowPops, direction)
                    }
                }
                return true
            }
            if (level.getBlockState(belowPops.west()).block is DoorBlock) {
                when (direction) {
                    Direction.NORTH -> {
                        makePortal(level, belowPops.west(), direction.opposite)
                    }
                    Direction.SOUTH -> {
                        makePortal(level, belowPops, direction.opposite)
                    }
                    else -> {
                        makePortal(level, belowPops, direction)
                    }
                }
                return true
            }
            if (level.getBlockState(belowPops.north()).block is DoorBlock) {
                when (direction) {
                    Direction.EAST -> {
                        makePortal(level, belowPops.north(), direction.opposite)
                    }
                    Direction.WEST -> {
                        makePortal(level, belowPops, direction.opposite)
                    }
                    else -> {
                        makePortal(level, belowPops, direction)
                    }
                }
                return true
            }
            if (level.getBlockState(belowPops.south()).block is DoorBlock) {
                when (direction) {
                    Direction.EAST -> {
                        makePortal(level, belowPops, direction.opposite)
                    }
                    Direction.WEST -> {
                        makePortal(level, belowPops.south(), direction.opposite)
                    }
                    else -> {
                        makePortal(level, belowPops, direction)
                    }
                }
                return true
            }
        }
        return false
    }
}