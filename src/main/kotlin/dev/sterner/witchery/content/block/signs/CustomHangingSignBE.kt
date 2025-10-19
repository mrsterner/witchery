package dev.sterner.witchery.content.block.signs

import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.SignBlockEntity
import net.minecraft.world.level.block.state.BlockState

class CustomHangingSignBE(pos: BlockPos, state: BlockState) :
    SignBlockEntity(WitcheryBlockEntityTypes.CUSTOM_HANGING_SIGN.get(), pos, state) {
    override fun getType(): BlockEntityType<SignBlockEntity> = WitcheryBlockEntityTypes.CUSTOM_HANGING_SIGN.get()
}