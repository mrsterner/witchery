package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.api.WitcheryFluidStorage

object WitcheryFluidStorageHelperImpl {

    @JvmStatic
    fun createFluidStorage(capacity: Long, setChanged: Runnable): WitcheryFluidStorage {
        return WitcheryFluidStorageImpl(capacity, setChanged)
    }
}