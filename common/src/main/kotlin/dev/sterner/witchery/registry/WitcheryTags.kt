package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.Witchery.MODID
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block


object WitcheryTags {

    val PLACEABLE_POPPETS: TagKey<Item> = TagKey.create(Registries.ITEM, Witchery.id("placeable_poppets"))
    val SPIRIT_WORLD_TRANSFERABLE: TagKey<Item> = TagKey.create(Registries.ITEM, Witchery.id("spirit_world_transferable"))

    val PLANTS: TagKey<Block> = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "plants"))

    val ROWAN_LOGS: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("rowan_logs"))
    val ROWAN_LOG_ITEMS: TagKey<Item> = TagKey.create(Registries.ITEM, Witchery.id("rowan_logs"))

    val ALDER_LOGS: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("alder_logs"))
    val ALDER_LOG_ITEMS: TagKey<Item> = TagKey.create(Registries.ITEM, Witchery.id("alder_logs"))

    val HAWTHORN_LOGS: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("hawthorn_logs"))
    val HAWTHORN_LOG_ITEMS: TagKey<Item> = TagKey.create(Registries.ITEM, Witchery.id("hawthorn_logs"))

    val LOGS: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("logs"))
    val LEAVES: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("leaves"))
    val LEAF_ITEMS: TagKey<Item> = TagKey.create(Registries.ITEM, Witchery.id("leaves"))

    val CANDELABRAS: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("candelabras"))
    val CANDELABRA_ITEMS: TagKey<Item> = TagKey.create(Registries.ITEM, Witchery.id("candelabras"))


    val BROWN_MUSHROOM: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("brown_mushroom"))
    val RED_MUSHROOM: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("red_mushroom"))


    val BAMBOO: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("bamboo"))
    val BIG_DRIPLEAF: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("big_dripleaf"))
    val CRIMSON_FUNGUS: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("crimson_fungus"))
    val KELP: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("kelp"))
    val MOSS: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("moss"))
    val MOSSY_BLOCKS: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("mossy_blocks"))
    val NETHER_FOLIAGE: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("nether_foliage"))
    val OVERWORLD_FOLIAGE: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("overworld_foliage"))
    val PITCHER: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("pitcher"))
    val SCULK: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("sculk"))
    val TORCHFLOWER: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("torchflower"))
    val WARPED_FUNGUS: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("warped_fungus"))
    val VINES: TagKey<Block> = TagKey.create(Registries.BLOCK, Witchery.id("vines"))
    val WITCH_CIRCLE_BIOMES = TagKey.create(Registries.BIOME, Witchery.id("witch_circle_biomes"))
}