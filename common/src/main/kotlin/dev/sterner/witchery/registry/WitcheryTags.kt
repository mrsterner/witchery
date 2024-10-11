package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery.MODID
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey


object WitcheryTags {
    val PLANTS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "plants"))
}