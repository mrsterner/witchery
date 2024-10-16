package dev.sterner.witchery.block.signs

import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.level.block.state.BlockState

class CustomSignBE(pos: BlockPos, state: BlockState): SignBlockEntity(WitcheryBlockEntityTypes.CUSTOM_SIGN.get(), pos, state) {
    override fun getType() = WitcheryBlockEntityTypes.CUSTOM_SIGN.get()
}