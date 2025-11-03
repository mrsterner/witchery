package dev.sterner.witchery.content.block.life_blood

import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class LifeBloodBlockEntity(pos: BlockPos, blockState: BlockState) :
    BlockEntity(WitcheryBlockEntityTypes.LIFE_BLOOD.get(), pos, blockState) {
}