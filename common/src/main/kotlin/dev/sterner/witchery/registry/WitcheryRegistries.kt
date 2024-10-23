package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.DynamicRitual
import dev.sterner.witchery.api.Ritual
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

object WitcheryRegistries {
    val RITUAL: ResourceKey<Registry<Ritual>> = ResourceKey.createRegistryKey(Witchery.id("ritual"))
    val DYNAMIC_RITUAL: ResourceKey<Registry<DynamicRitual>> = ResourceKey.createRegistryKey(Witchery.id("ritual/dynamic"))
}