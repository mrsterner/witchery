package dev.sterner.witchery.worldgen

import dev.sterner.witchery.Witchery
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey

object WitcheryWorldgenKeys {
    val ROWAN_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, Witchery.id("rowan_placed"))
    val ROWAN_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Witchery.id("rowan"))

    val ALDER_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, Witchery.id("alder_placed"))
    val ALDER_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Witchery.id("alder"))

    val HAWTHORN_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, Witchery.id("hawthorn_placed"))
    val HAWTHORN_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Witchery.id("hawthorn"))

    val WISPY_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, Witchery.id("wispy_placed"))
    val WISPY_KEY = ResourceKey.create(Registries.CONFIGURED_FEATURE, Witchery.id("wispy"))
}