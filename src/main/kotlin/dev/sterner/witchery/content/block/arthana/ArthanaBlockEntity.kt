package dev.sterner.witchery.content.block.arthana


import dev.sterner.witchery.content.block.WitcheryBaseBlockEntity
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

class ArthanaBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    WitcheryBaseBlockEntity(WitcheryBlockEntityTypes.ARTHANA.get(), blockPos, blockState) {
    var arthana = ItemStack(WitcheryItems.ARTHANA.get())
    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        ItemStack.parse(pRegistries, pTag.getCompound("arthana")).ifPresent {
            arthana = it
        }
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        val itemTag = arthana.save(registries, CompoundTag())
        tag.put("arthana", itemTag)
        super.saveAdditional(tag, registries)
    }
}