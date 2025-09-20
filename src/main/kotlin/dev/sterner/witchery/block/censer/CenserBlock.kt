package dev.sterner.witchery.block.censer

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.AbstractCandleBlock.LIT
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.LanternBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import java.util.function.ToIntFunction

class CenserBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.lightLevel(
litBlockEmission(8)
)) {

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return CenserBlockEntity(pos, state)
    }

    init {
        this.registerDefaultState(
            this.stateDefinition.any().setValue(LanternBlock.HANGING, false)
                .setValue(LanternBlock.WATERLOGGED, false).setValue(TYPE, true).setValue(LIT, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(LanternBlock.HANGING, LanternBlock.WATERLOGGED, TYPE, LIT)
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape? {
        val bl = state.getValue(LanternBlock.HANGING)
        val bl2 = state.getValue(TYPE)
        return if (bl) {
            if (bl2) {
                SHAPE_HANGING_1
            } else {
                SHAPE_HANGING_2
            }
        } else {
            if (bl2) {
                SHAPE_1
            } else {
                SHAPE_2
            }
        }
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        val fluidState = context.level.getFluidState(context.clickedPos)

        for (direction in context.getNearestLookingDirections()) {
            if (direction.axis === Direction.Axis.Y) {
                val blockState = this.defaultBlockState()
                    .setValue(LanternBlock.HANGING, direction == Direction.UP)
                if (blockState.canSurvive(context.level, context.clickedPos)) {
                    return blockState.setValue(
                        LanternBlock.WATERLOGGED,
                        fluidState.type === Fluids.WATER
                    )
                }
            }
        }

        return null
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        val direction = getConnectedDirection(state).opposite
        return canSupportCenter(level, pos.relative(direction), direction.opposite)
    }

    fun getConnectedDirection(state: BlockState): Direction {
        return if (state.getValue(LanternBlock.HANGING)) Direction.DOWN else Direction.UP
    }

    fun setLit(level: Level, pos: BlockPos, lit: Boolean) {
        val state = level.getBlockState(pos)
        if (state.block == this && state.getValue(LIT) != lit) {
            level.setBlock(pos, state.setValue(LIT, lit), 3)
        }
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        if (state.getValue(LanternBlock.WATERLOGGED) as Boolean) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level))
        }

        return if (getConnectedDirection(state).opposite == direction && !state.canSurvive(level, pos))
            Blocks.AIR.defaultBlockState()
        else
            super.updateShape(state, direction, neighborState, level, pos, neighborPos)
    }

    override fun getFluidState(state: BlockState): FluidState {
        return if (state.getValue(LanternBlock.WATERLOGGED)) Fluids.WATER.getSource(false) else super.getFluidState(
            state
        )
    }

    override fun isPathfindable(state: BlockState, pathComputationType: PathComputationType): Boolean {
        return false
    }

    companion object {

        fun litBlockEmission(lightValue: Int): ToIntFunction<BlockState> {
            return ToIntFunction { blockState: BlockState ->
                if (blockState.getValue(
                        LIT
                    ) == true
                ) lightValue else 0
            }
        }

        val TYPE: BooleanProperty = BooleanProperty.create("type")

        var SHAPE_FOOT_1: VoxelShape =  box(5.0, 0.0, 5.0, 11.0, 2.0, 11.0)
        var SHAPE_BASE_1: VoxelShape =  box(6.0, 2.0, 6.0, 10.0, 4.0, 10.0)
        var SHAPE_CORE_1: VoxelShape =  box(4.5, 4.0, 4.5, 11.5, 10.0, 11.5)
        var SHAPE_TOP_1: VoxelShape =  box(5.5, 10.0, 5.5, 10.5, 12.0, 10.5)

        val SHAPE_HANGING_1: VoxelShape =
            Shapes.joinUnoptimized(SHAPE_BASE_1,
                Shapes.joinUnoptimized(SHAPE_TOP_1, SHAPE_CORE_1, BooleanOp.OR), BooleanOp.OR)

        val SHAPE_1: VoxelShape = Shapes.joinUnoptimized(SHAPE_FOOT_1, SHAPE_HANGING_1, BooleanOp.OR)


        var SHAPE_FOOT_2: VoxelShape =  box(6.0, 0.0, 6.0, 10.0, 2.0, 10.0)
        var SHAPE_BASE_2: VoxelShape =  box(7.0, 2.0, 7.0, 9.0, 4.0, 9.0)
        var SHAPE_CORE_2: VoxelShape =  box(5.5, 4.0, 5.5, 10.5, 14.0, 10.5)
        var SHAPE_TOP_2: VoxelShape =  box(7.0, 14.0, 7.0, 9.0, 16.0, 9.0)

        val SHAPE_HANGING_2: VoxelShape =
            Shapes.joinUnoptimized(SHAPE_BASE_2,
                Shapes.joinUnoptimized(SHAPE_TOP_2, SHAPE_CORE_2, BooleanOp.OR), BooleanOp.OR)

        val SHAPE_2: VoxelShape = Shapes.joinUnoptimized(SHAPE_FOOT_2, SHAPE_HANGING_2, BooleanOp.OR)
    }
}