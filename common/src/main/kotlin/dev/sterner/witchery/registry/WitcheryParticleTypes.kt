package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.particle.BloodSplashParticleType
import dev.sterner.witchery.client.particle.ColorBubbleParticleType
import dev.sterner.witchery.client.particle.SneezeParticleType
import dev.sterner.witchery.client.particle.ZzzParticleType
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.Registries


object WitcheryParticleTypes {

    var PARTICLES: DeferredRegister<ParticleType<*>> = DeferredRegister.create(Witchery.MODID, Registries.PARTICLE_TYPE)

    var COLOR_BUBBLE: RegistrySupplier<ColorBubbleParticleType> = PARTICLES.register(
        "color_bubble"
    ) {
        ColorBubbleParticleType()
    }

    var ZZZ: RegistrySupplier<ZzzParticleType> = PARTICLES.register("zzz") {
        ZzzParticleType()
    }

    var SNEEZE: RegistrySupplier<SneezeParticleType> = PARTICLES.register("sneeze") {
        SneezeParticleType()
    }

    var SPLASHING_BLOOD: RegistrySupplier<BloodSplashParticleType> = PARTICLES.register("splashing_blood") {
        BloodSplashParticleType(true)
    }
}