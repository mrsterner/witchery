package dev.sterner.witchery.api.block

import dev.architectury.fluid.FluidStack
import dev.sterner.witchery.platform.WitcheryFluidStorageHelper
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.block.entity.BlockEntity
import java.util.*


class WitcheryFluidTank<T : BlockEntity>(val blockEntity: T, capacity: Long = FluidStack.bucketAmount()) {

    val fluidStorage = WitcheryFluidStorageHelper.createFluidStorage(capacity, blockEntity::setChanged)

    fun loadFluidAdditional(nbt: CompoundTag, registryLookup: HolderLookup.Provider) {
        if (nbt.contains("fluidStack")) {
            val fluidStack = nbt.getCompound("fluidStack")
            val fluid: Optional<FluidStack> = FluidStack.read(registryLookup, fluidStack)
            if (fluid.isPresent) fluidStorage.setFluidStack(fluid.get())
        }
    }

    fun saveFluidAdditional(nbt: CompoundTag, registryLookup: HolderLookup.Provider) {
        val fluid: Optional<FluidStack> = fluidStorage.getFluidStack()
        if (fluid.isPresent) {
            nbt.put("fluidStack", fluid.get().write(registryLookup, CompoundTag()))
        }
    }

    fun getUpdateTag(superTag: CompoundTag, registryLookup: HolderLookup.Provider): CompoundTag {
        val fluid: Optional<FluidStack> = fluidStorage.getFluidStack()
        if (fluid.isPresent) {
            superTag.put("fluidStack", fluid.get().write(registryLookup, CompoundTag()))
        }
        return superTag
    }
}