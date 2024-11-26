package dev.sterner.witchery.block.effigy

import dev.sterner.witchery.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.registry.WitcheryDataComponents
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

class EffigyBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.EFFIGY.get(), EffigyBlock.STRUCTURE.get(), blockPos, blockState) {

    var bansheeCount = 0
    var specterCount = 0
    var poltergeistCount = 0


    override fun loadAdditional(pTag: CompoundTag, pRegistries: HolderLookup.Provider) {
        super.loadAdditional(pTag, pRegistries)
        bansheeCount = pTag.getInt("Banshee")
        specterCount = pTag.getInt("Spectre")
        poltergeistCount = pTag.getInt("Poltergeist")
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("Banshee", bansheeCount)
        tag.putInt("Spectre", specterCount)
        tag.putInt("Poltergeist", poltergeistCount)
    }

    override fun onPlace(pPlacer: LivingEntity?, pStack: ItemStack) {
        val bl = pStack.has(WitcheryDataComponents.BANSHEE_COUNT.get())
        val bl2 = pStack.has(WitcheryDataComponents.SPECTRE_COUNT.get())
        val bl3 = pStack.has(WitcheryDataComponents.POLTERGEIST_COUNT.get())

        if (bl) {
            bansheeCount = pStack.get(WitcheryDataComponents.BANSHEE_COUNT.get()) ?: 0
        }
        if (bl2) {
            specterCount = pStack.get(WitcheryDataComponents.SPECTRE_COUNT.get()) ?: 0
        }
        if (bl3) {
            poltergeistCount = pStack.get(WitcheryDataComponents.POLTERGEIST_COUNT.get()) ?: 0
        }
        setChanged()

        super.onPlace(pPlacer, pStack)
    }
}