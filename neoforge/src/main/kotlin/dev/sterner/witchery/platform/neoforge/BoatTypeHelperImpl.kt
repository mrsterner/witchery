package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.asm.EnumExtension

object BoatTypeHelperImpl {
    @JvmStatic
    fun getRowanBoatType() = EnumExtension.ROWAN_BOAT_TYPE_PROXY.value
}