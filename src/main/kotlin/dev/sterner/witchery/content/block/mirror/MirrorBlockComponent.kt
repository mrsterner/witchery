package dev.sterner.witchery.content.block.mirror

import dev.sterner.witchery.content.block.mirror.MirrorBlock.Companion.EAST_AABB
import dev.sterner.witchery.content.block.mirror.MirrorBlock.Companion.FACING
import dev.sterner.witchery.content.block.mirror.MirrorBlock.Companion.NORTH_AABB
import dev.sterner.witchery.content.block.mirror.MirrorBlock.Companion.SOUTH_AABB
import dev.sterner.witchery.content.block.mirror.MirrorBlock.Companion.WEST_AABB
import dev.sterner.witchery.core.api.multiblock.MultiBlockComponentBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class MirrorBlockComponent(properties: Properties) : MultiBlockComponentBlock(properties.noOcclusion()) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
    }

    override fun canBeReplaced(state: BlockState, fluid: Fluid): Boolean {
        return false
    }

    override fun canBeReplaced(state: BlockState, useContext: BlockPlaceContext): Boolean {
        return false
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        val direction = state.getValue(FACING)

        return when (direction) {
            Direction.SOUTH -> SOUTH_AABB
            Direction.WEST ->  WEST_AABB
            Direction.NORTH -> NORTH_AABB
            else -> EAST_AABB
        }
    }

    companion object {

    }
}