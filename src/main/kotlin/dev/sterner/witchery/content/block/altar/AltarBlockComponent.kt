package dev.sterner.witchery.content.block.altar

import dev.sterner.witchery.core.api.multiblock.MultiBlockComponentBlock
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class AltarBlockComponent(properties: Properties) : MultiBlockComponentBlock(properties.noOcclusion()) {

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    override fun getStateForPlacement(blockPlaceContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(
            BlockStateProperties.HORIZONTAL_FACING,
            blockPlaceContext.horizontalDirection.opposite
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }
}