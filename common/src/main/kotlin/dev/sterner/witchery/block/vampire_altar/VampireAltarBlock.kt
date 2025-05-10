package dev.sterner.witchery.block.vampire_altar

import dev.sterner.witchery.api.block.WitcheryBaseEntityBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class VampireAltarBlock(properties: Properties) : WitcheryBaseEntityBlock(properties.noOcclusion()) {
    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity? {
        return VampireAltarBlockEntity(pos, state)
    }
}