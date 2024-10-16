package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.api.WitcheryFluidStorage

object WitcheryFluidStorageHelper {

    @JvmStatic
    @ExpectPlatform
    fun createFluidStorage(capacity: Long, setChanged: Runnable): WitcheryFluidStorage {
        throw AssertionError()
    }
}