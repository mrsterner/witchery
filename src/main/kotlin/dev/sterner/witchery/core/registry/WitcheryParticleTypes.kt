package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.particle.BloodSplashParticleType
import dev.sterner.witchery.client.particle.ColorBubbleParticleType
import dev.sterner.witchery.client.particle.SneezeParticleType
import dev.sterner.witchery.client.particle.ZzzParticleType
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.Registries
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier


object WitcheryParticleTypes {

    var PARTICLES: DeferredRegister<ParticleType<*>> = DeferredRegister.create(Registries.PARTICLE_TYPE, Witchery.MODID)

    var COLOR_BUBBLE = PARTICLES.register(
        "color_bubble", Supplier {
            ColorBubbleParticleType()
        })

    var ZZZ = PARTICLES.register("zzz", Supplier {
        ZzzParticleType()
    })

    var SNEEZE = PARTICLES.register("sneeze", Supplier {
        SneezeParticleType()
    })

    var SPLASHING_BLOOD = PARTICLES.register("splashing_blood", Supplier {
        BloodSplashParticleType(true)
    })
}