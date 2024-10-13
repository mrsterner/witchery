package dev.sterner.witchery.block.oven

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.block.cauldron.CauldronBlock.Companion.litBlockEmission
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import kotlin.experimental.or

class OvenBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.noOcclusion().lightLevel(litBlockEmission(8))) {

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

            // Check the presence of regular fume funnels
            oven.fumeHoodCount = checkFumeFunnels(pState, pLevel, pPos, regular = true)

            // Check the presence of filtered fume funnels
            oven.filteredFumeHoodCount = checkFumeFunnels(pState, pLevel, pPos, regular = false)
        }
    }

    // Helper to count funnels
    private fun checkFumeFunnels(state: BlockState, level: LevelReader, pos: BlockPos, regular: Boolean): Int {
        val blockFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING)
        val leftPos = pos.relative(blockFacing.counterClockWise)
        val rightPos = pos.relative(blockFacing.clockWise)

        // Check left and right positions for funnels
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

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.OVEN.get().create(pos, state)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT)
    }
}