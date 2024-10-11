package dev.sterner.witchery.api.block

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity


interface CustomUpdateTagHandlingBlockEntity {
    fun handleUpdateTag(tag: CompoundTag, lookupProvider: HolderLookup.Provider) {
        (this as BlockEntity).loadWithComponents(tag, lookupProvider)
    }
}