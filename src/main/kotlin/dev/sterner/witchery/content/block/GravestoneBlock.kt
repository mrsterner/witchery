package dev.sterner.witchery.content.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class GravestoneBlock(properties: Properties) : Block(properties), SimpleWaterloggedBlock {

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.WATERLOGGED, false)
        )
    }


    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            rotation.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING))
        )
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.rotate(mirror.getRotation(state.getValue(BlockStateProperties.HORIZONTAL_FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.WATERLOGGED)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return when (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction.NORTH -> NORTH_SOUTH
            Direction.SOUTH -> NORTH_SOUTH
            else -> EAST_WEST
        }
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val fluidState = context.level.getFluidState(context.clickedPos)
        return super.getStateForPlacement(context)!!.setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            context.horizontalDirection.opposite
        )
            .setValue(BlockStateProperties.WATERLOGGED, fluidState.`is`(Fluids.WATER))
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.getValue(WaterloggedTransparentBlock.WATERLOGGED) as Boolean) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(WaterloggedTransparentBlock.WATERLOGGED)) Fluids.WATER.getSource(true) else super.getFluidState(
            state
        )
    }

    companion object {
        val EAST_WEST = Shapes.box(6.0 / 16, 0.0, 2.0 / 16, 10.0 / 16, 16.0 / 16, 14.0 / 16)
        val NORTH_SOUTH = Shapes.box(2.0 / 16, 0.0, 6.0 / 16, 14.0 / 16, 16.0 / 16, 10.0 / 16)
    }
}