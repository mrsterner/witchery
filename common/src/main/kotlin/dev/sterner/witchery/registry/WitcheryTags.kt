package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.Witchery.MODID
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey


object WitcheryTags {
    val PLANTS = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "plants"))

    val ROWAN_LOGS = TagKey.create(Registries.BLOCK, Witchery.id("rowan_logs"))
    val LEAVES = TagKey.create(Registries.BLOCK, Witchery.id("leaves"))
    val BROWN_MUCHROOM = TagKey.create(Registries.BLOCK, Witchery.id("brown_mushroom"))
    val RED_MUSHROOM = TagKey.create(Registries.BLOCK, Witchery.id("red_mushroom"))


    val BAMBOO = TagKey.create(Registries.BLOCK, Witchery.id("bamboo"))
    val BIG_DRIPLEAF = TagKey.create(Registries.BLOCK, Witchery.id("big_dripleaf"))
    val CRIMSON_FUNGUS = TagKey.create(Registries.BLOCK, Witchery.id("crimson_fungus"))
    val KELP = TagKey.create(Registries.BLOCK, Witchery.id("kelp"))
    val MOSS = TagKey.create(Registries.BLOCK, Witchery.id("moss"))
    val MOSSY_BLOCKS = TagKey.create(Registries.BLOCK, Witchery.id("mossy_blocks"))
    val NETHER_FOLIAGE = TagKey.create(Registries.BLOCK, Witchery.id("nether_foliage"))
    val OVERWORLD_FOLIAGE = TagKey.create(Registries.BLOCK, Witchery.id("overworld_foliage"))
    val PITCHER = TagKey.create(Registries.BLOCK, Witchery.id("pitcher"))
    val SCULK = TagKey.create(Registries.BLOCK, Witchery.id("sculk"))
    val TORCHFLOWER = TagKey.create(Registries.BLOCK, Witchery.id("torchflower"))
    val WARPED_FUNGUS = TagKey.create(Registries.BLOCK, Witchery.id("warped_fungus"))
    val VINES = TagKey.create(Registries.BLOCK, Witchery.id("vines"))
}