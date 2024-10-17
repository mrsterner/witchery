package dev.sterner.witchery.platform

import dev.architectury.injectables.annotations.ExpectPlatform
import net.minecraft.world.entity.vehicle.Boat

object BoatTypeHelper {
    @JvmStatic
    @ExpectPlatform
    fun getRowanBoatType(): Boat.Type {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getAlderBoatType(): Boat.Type {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getHawthornBoatType(): Boat.Type {
        throw AssertionError()
    }
}