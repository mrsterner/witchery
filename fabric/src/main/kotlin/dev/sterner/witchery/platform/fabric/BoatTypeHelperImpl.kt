package dev.sterner.witchery.platform.fabric

import net.minecraft.world.entity.vehicle.Boat

object BoatTypeHelperImpl {
    @JvmStatic
    fun getRowanBoatType(): Boat.Type = Boat.Type.valueOf("WITCHERY_ROWAN")

    @JvmStatic
    fun getAlderBoatType(): Boat.Type = Boat.Type.valueOf("WITCHERY_ALDER")

    @JvmStatic
    fun getHawthornBoatType(): Boat.Type = Boat.Type.valueOf("WITCHERY_HAWTHORN")
}