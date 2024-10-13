package dev.sterner.witchery.block.oven

import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.api.multiblock.MultiBlockStructure
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class OvenFumeExtensionBlockEntity(
    pos: BlockPos,
    state: BlockState
) : MultiBlockCoreEntity(WitcheryBlockEntityTypes.OVEN_FUME_EXTENSION.get(), OvenFumeExtensionBlock.STRUCTURE.get(), pos, state) {
}