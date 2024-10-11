package dev.sterner.witchery.platform.fabric

import dev.architectury.fluid.FluidStack
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric
import dev.sterner.witchery.platform.WitcheryFluidStorage
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.world.level.material.Fluid
import java.util.*

object WitcheryFluidStorageHelperImpl  {

    @JvmStatic
    fun createFluidStorage(capacity: Long, setChanged: Runnable): WitcheryFluidStorage {
        return WitcheryFluidStorageImpl(capacity, setChanged)
    }
}