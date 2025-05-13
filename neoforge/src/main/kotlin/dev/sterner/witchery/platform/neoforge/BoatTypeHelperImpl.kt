package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.asm.EnumExtension
import net.minecraft.world.entity.vehicle.Boat

object BoatTypeHelperImpl {
    @JvmStatic
    fun getRowanBoatType(): Boat.Type = EnumExtension.ROWAN_BOAT_TYPE_PROXY.value

    @JvmStatic
    fun getAlderBoatType(): Boat.Type = EnumExtension.ALDER_BOAT_TYPE_PROXY.value

    @JvmStatic
    fun getHawthornBoatType(): Boat.Type = EnumExtension.HAWTHORN_BOAT_TYPE_PROXY.value
}