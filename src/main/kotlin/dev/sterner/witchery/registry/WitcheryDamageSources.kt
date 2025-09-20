package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageType

object WitcheryDamageSources {

    val IN_SUN: ResourceKey<DamageType> = ResourceKey.create(Registries.DAMAGE_TYPE, Witchery.id("in_sun"))
}