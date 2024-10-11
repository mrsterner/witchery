package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.platform.WitcheryFluidStorage
import net.neoforged.neoforge.fluids.capability.templates.FluidTank

object WitcheryFluidStorageHelperImpl  {

    @JvmStatic
    fun createFluidStorage(capacity: Long, setChanged: Runnable?): WitcheryFluidStorage {
        return WitcheryFluidStorageImpl(FluidTank(capacity.toInt()), setChanged)
    }
}