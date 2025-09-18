package dev.sterner.witchery.block.phylactery

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class PhylacteryBlock(properties: Properties) : WitcheryBaseEntityBlock(properties) {
    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return PhylacteryBlockEntity(pos, state)
    }
}