package dev.sterner.witchery.content.block.grassper


import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class GrassperBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.GRASSPER.get().create(pos, state)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(6.0 / 16, 0.0, 6.0 / 16, 10.0 / 16, 8.0 / 16, 10.0 / 16)
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (direction == Direction.DOWN && !this.canSurvive(
                state,
                level,
                pos
            )
        ) Blocks.AIR.defaultBlockState() else super.updateShape(
            state,
            direction,
            neighborState,
            level,
            pos,
            neighborPos
        )
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return canSupportCenter(level, pos.below(), Direction.UP)
    }
}