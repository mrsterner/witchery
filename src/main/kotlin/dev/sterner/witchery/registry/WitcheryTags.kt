package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.Witchery.Companion.MODID
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid


object WitcheryTags {

    val WEREWOLF_ALTAR_ITEM: TagKey<Item> = TagKey.create(Registries.ITEM, Witchery.id("werewolf_altar_item"))
    val PLACEABLE_POPPETS: TagKey<Item> = TagKey.create(Registries.ITEM, Witchery.id("placeable_poppets"))
    val FROM_SPIRIT_WORLD_TRANSFERABLE: TagKey<Item> =
        TagKey.create(Registries.ITEM, Witchery.id("from_spirit_world_transferable"))
    val TO_SPIRIT_WORLD_TRANSFERABLE: TagKey<Item> =
        TagKey.create(Registries.ITEM, Witchery.id("to_spirit_world_transferable"))

    val WOODEN_WEAPONS: TagKey<Item> = TagKey.create(Registries.ITEM, Witchery.id("wooden_weapons"))

    val SMASH_STONE: TagKey<Block> =
        TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MODID, "smash_stone"))

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

    val MUSHROOMS: TagKey<Item> =
        TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "mushrooms"))

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

    val NECROMANCER_SUMMONABLE = TagKey.create(Registries.ENTITY_TYPE, Witchery.id("necromancer_summonable"))
    val SCARED_BY_GROTESQUE = TagKey.create(Registries.ENTITY_TYPE, Witchery.id("scared_by_grotesque"))


    val INVENTORY_CARRIERS  = TagKey.create(Registries.ENTITY_TYPE, Witchery.id("inventory_carriers"))
    val SLEEPERS  = TagKey.create(Registries.ENTITY_TYPE, Witchery.id("sleepers"))
    val ITEM_USERS  = TagKey.create(Registries.ENTITY_TYPE, Witchery.id("item_users"))
    val POSSESSABLE = TagKey.create(Registries.ENTITY_TYPE, Witchery.id("possessable"))
    val REGULAR_EATER = TagKey.create(Registries.ENTITY_TYPE, Witchery.id("regular_eater"))
    val IMMOVABLE = TagKey.create(Registries.ENTITY_TYPE, Witchery.id("immovable"))

    val EMPTY_FLUID = TagKey.create(Registries.FLUID, Witchery.id("empty"))


}