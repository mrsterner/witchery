package dev.sterner.witchery.block.bear_trap

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class BearTrapBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.OPEN, false)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.OPEN)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return BearTrapBlockEntity(pos, state)
    }
}