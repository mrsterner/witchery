package dev.sterner.witchery.platform.fabric

import net.minecraft.world.entity.vehicle.Boat

object BoatTypeHelperImpl {
    @JvmStatic
    fun getRowanBoatType() = Boat.Type.valueOf("WITCHERY_ROWAN")
}