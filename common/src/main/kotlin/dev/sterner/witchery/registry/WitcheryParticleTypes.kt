package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.sterner.witchery.Witchery
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.Registries


object WitcheryParticleTypes {

    var PARTICLES: DeferredRegister<ParticleType<*>> = DeferredRegister.create(Witchery.MODID, Registries.PARTICLE_TYPE)


}