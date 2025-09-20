package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.DeferredRegister


object WitcheryParticleTypes {

    var PARTICLES: DeferredRegister<ParticleType<*>> = DeferredRegister.create(Registries.PARTICLE_TYPE, Witchery.MODID)

    var COLOR_BUBBLE = PARTICLES.register(
        "color_bubble"
    ) {
        ColorBubbleParticleType()
    }

    var ZZZ = PARTICLES.register("zzz") {
        ZzzParticleType()
    }

    var SNEEZE = PARTICLES.register("sneeze") {
        SneezeParticleType()
    }

    var SPLASHING_BLOOD = PARTICLES.register("splashing_blood") {
        BloodSplashParticleType(true)
    }
}