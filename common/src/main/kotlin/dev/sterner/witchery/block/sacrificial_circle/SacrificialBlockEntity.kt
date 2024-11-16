package dev.sterner.witchery.block.sacrificial_circle

import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState

class SacrificialBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.SACRIFICIAL_CIRCLE.get(), SacrificialBlock.STRUCTURE.get(), blockPos, blockState) {

        val candleCount = 0

}