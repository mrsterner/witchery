package dev.sterner.witchery.block.altar

import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState


class AltarBlockEntity(pos: BlockPos, state: BlockState) : MultiBlockCoreEntity(
    WitcheryBlockEntityTypes.ALTAR.get(), AltarBlock.STRUCTURE.get(),
    pos, state
)