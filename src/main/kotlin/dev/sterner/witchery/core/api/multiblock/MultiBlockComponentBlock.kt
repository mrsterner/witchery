package dev.sterner.witchery.core.api.multiblock

import dev.sterner.witchery.block.WitcheryBaseEntityBlock
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState


open class MultiBlockComponentBlock(properties: Properties) :
    WitcheryBaseEntityBlock(properties) {

    override fun newBlockEntity(blockPos: BlockPos, blockState: BlockState): BlockEntity? {
        return WitcheryBlockEntityTypes.MULTI_BLOCK_COMPONENT.get().create(blockPos, blockState)
    }
}