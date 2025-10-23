package dev.sterner.witchery.content.block.oven


import dev.sterner.witchery.content.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.content.block.cauldron.WitcheryCauldronBlock.Companion.litBlockEmission
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.Containers
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

open class OvenBlock(properties: Properties) :
    WitcheryBaseEntityBlock(properties.noOcclusion().lightLevel(litBlockEmission(8))) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
                .setValue(BlockStateProperties.LIT, false)
        )
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
    }

    override fun neighborChanged(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pBlock: Block,
        pFromPos: BlockPos,
        pIsMoving: Boolean
    ) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving)

        if (pLevel.getBlockEntity(pPos) is OvenBlockEntity) {
            val oven = pLevel.getBlockEntity(pPos) as OvenBlockEntity

            oven.fumeHoodCount = checkFumeFunnels(pState, pLevel, pPos, regular = true)

            oven.filteredFumeHoodCount = checkFumeFunnels(pState, pLevel, pPos, regular = false)
        }
    }

    private fun checkFumeFunnels(state: BlockState, level: LevelReader, pos: BlockPos, regular: Boolean): Int {
        val blockFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val leftPos = pos.relative(blockFacing.counterClockWise)
        val rightPos = pos.relative(blockFacing.clockWise)

        val leftState = level.getBlockEntity(leftPos)
        val rightState = level.getBlockEntity(rightPos)

        var count = 0
        if (regular) {
            if (leftState is OvenFumeExtensionBlockEntity && !leftState.isFiltered) count++
            if (rightState is OvenFumeExtensionBlockEntity && !rightState.isFiltered) count++
        } else {
            if (leftState is OvenFumeExtensionBlockEntity && leftState.isFiltered) count++
            if (rightState is OvenFumeExtensionBlockEntity && rightState.isFiltered) count++
        }
        return count
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        Containers.dropContentsOnDestroy(state, newState, level, pos)
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.OVEN.get().create(pos, state)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT)
    }
}