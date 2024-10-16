package dev.sterner.witchery.block.signs

import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.HangingSignBlockEntity
import net.minecraft.world.level.block.state.BlockState

class CustomHangingSignBE(pos: BlockPos, state: BlockState): HangingSignBlockEntity(pos, state) {
    override fun getType() = WitcheryBlockEntityTypes.CUSTOM_HANGING_SIGN.get()
}