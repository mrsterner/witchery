package dev.sterner.witchery.content.block.ritual


import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class GoldenChalkBlock(properties: Properties) :
    WitcheryBaseEntityBlock(properties.noOcclusion().noCollission()) {

    init {
        this.registerDefaultState(stateDefinition.any())
    }

    override fun canBeReplaced(state: BlockState, useContext: BlockPlaceContext): Boolean {
        return false
    }

    override fun canBeReplaced(state: BlockState, fluid: Fluid): Boolean {
        return false
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (!state.canSurvive(level, pos)) Blocks.AIR.defaultBlockState() else super.updateShape(
            state,
            direction,
            neighborState,
            level,
            pos,
            neighborPos
        )
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return !level.isEmptyBlock(pos.below())
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0 / 16, 1.0)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.GOLDEN_CHALK.get().create(pos, state)
    }
}