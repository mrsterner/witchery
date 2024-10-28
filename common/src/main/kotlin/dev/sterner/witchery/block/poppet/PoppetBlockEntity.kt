package dev.sterner.witchery.block.poppet

import dev.sterner.witchery.api.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class PoppetBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.POPPET.get(), blockPos, blockState) {
}