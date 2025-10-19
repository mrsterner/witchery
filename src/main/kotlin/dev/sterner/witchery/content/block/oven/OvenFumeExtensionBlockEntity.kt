package dev.sterner.witchery.content.block.oven


import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.state.BlockState

class OvenFumeExtensionBlockEntity(
    pos: BlockPos,
    state: BlockState
) : MultiBlockCoreEntity(
    WitcheryBlockEntityTypes.OVEN_FUME_EXTENSION.get(),
    OvenFumeExtensionBlock.STRUCTURE.get(),
    pos,
    state
) {

    var isFiltered = false

    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        isFiltered = pTag.getBoolean("Filtered")
        super.loadAdditional(pTag, pRegistries)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        tag.putBoolean("Filtered", isFiltered)
        super.saveAdditional(tag, registries)
    }
}