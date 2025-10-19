package dev.sterner.witchery.content.worldgen

import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.placement.PlacedFeature

object WitcheryWorldgenKeys {

    val ROWAN_PLACED_KEY: ResourceKey<PlacedFeature> =
        ResourceKey.create(Registries.PLACED_FEATURE, Witchery.id("rowan_placed"))
    val ROWAN_KEY: ResourceKey<ConfiguredFeature<*, *>> =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, Witchery.id("rowan"))

    val ALDER_PLACED_KEY: ResourceKey<PlacedFeature> =
        ResourceKey.create(Registries.PLACED_FEATURE, Witchery.id("alder_placed"))
    val ALDER_KEY: ResourceKey<ConfiguredFeature<*, *>> =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, Witchery.id("alder"))

    val HAWTHORN_PLACED_KEY: ResourceKey<PlacedFeature> =
        ResourceKey.create(Registries.PLACED_FEATURE, Witchery.id("hawthorn_placed"))
    val HAWTHORN_KEY: ResourceKey<ConfiguredFeature<*, *>> =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, Witchery.id("hawthorn"))

    val WISPY_PLACED_KEY: ResourceKey<PlacedFeature> =
        ResourceKey.create(Registries.PLACED_FEATURE, Witchery.id("wispy_placed"))
    val WISPY_KEY: ResourceKey<ConfiguredFeature<*, *>> =
        ResourceKey.create(Registries.CONFIGURED_FEATURE, Witchery.id("wispy"))

    val WITCH_CIRCLE_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, Witchery.id("witch_circle_placed"))
    val WITCH_CIRCLE_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Witchery.id("witch_circle"))


    val DREAM: ResourceKey<Level> =
        ResourceKey.create(Registries.DIMENSION, Witchery.id("dream_world"))

    val NIGHTMARE: ResourceKey<Level> =
        ResourceKey.create(Registries.DIMENSION, Witchery.id("nightmare_world"))
}