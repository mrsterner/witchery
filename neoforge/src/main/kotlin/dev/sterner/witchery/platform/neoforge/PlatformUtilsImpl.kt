package dev.sterner.witchery.platform.neoforge

import net.neoforged.fml.ModList

object PlatformUtilsImpl {

    fun isModLoaded(modId: String?): Boolean {
        return ModList.get().isLoaded(modId)
    }
}