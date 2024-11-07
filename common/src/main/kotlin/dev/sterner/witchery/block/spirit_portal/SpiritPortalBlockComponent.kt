package dev.sterner.witchery.block.spirit_portal

import dev.sterner.witchery.api.multiblock.MultiBlockComponentBlock
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock.Companion.EAST
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock.Companion.NORTH
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock.Companion.SOUTH
import dev.sterner.witchery.block.spirit_portal.SpiritPortalBlock.Companion.WEST
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class SpiritPortalBlockComponent(properties: Properties) : MultiBlockComponentBlock(properties.noOcclusion()) {

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val dir = state.getValue(HORIZONTAL_FACING)
        return when (dir) {
            Direction.NORTH -> {
                NORTH
            }
            Direction.EAST -> {
                EAST
            }
            Direction.WEST -> {
                WEST
            }
            else -> {
                SOUTH
            }
        }
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HORIZONTAL_FACING)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }
}