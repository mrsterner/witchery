package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.world.entity.vehicle.Boat

object BoatTypeHelper {
    @JvmStatic
    @ExpectPlatform
    fun getRowanBoatType(): Boat.Type {
        throw AssertionError()
    }
}