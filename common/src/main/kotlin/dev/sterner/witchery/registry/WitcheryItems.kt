package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.multiblock.MultiBlockItem
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.cauldron.CauldronBlock
import dev.sterner.witchery.block.distillery.DistilleryBlock
import dev.sterner.witchery.block.oven.OvenFumeExtensionBlock
import dev.sterner.witchery.item.*
import dev.sterner.witchery.platform.BoatTypeHelper
import dev.sterner.witchery.platform.PlatformUtils
import net.minecraft.core.registries.Registries
import net.minecraft.world.food.Foods
import net.minecraft.world.item.*


object WitcheryItems {

    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(Witchery.MODID, Registries.ITEM)

    val GLINTWEED: RegistrySupplier<BlockItem> = ITEMS.register("glintweed") {
        BlockItem(WitcheryBlocks.GLINTWEED.get(), Item.Properties())
    }

    val EMBER_MOSS: RegistrySupplier<BlockItem> = ITEMS.register("ember_moss") {
        BlockItem(WitcheryBlocks.EMBER_MOSS.get(), Item.Properties())
    }

    val SPANISH_MOSS: RegistrySupplier<BlockItem> = ITEMS.register("spanish_moss") {
        BlockItem(WitcheryBlocks.SPANISH_MOSS.get(), Item.Properties())
    }

    val INFINITY_EGG: RegistrySupplier<BlockItem> = ITEMS.register("infinity_egg") {
        BlockItem(WitcheryBlocks.INFINITY_EGG.get(), Item.Properties())
    }

    val WITCHES_HAND =  ITEMS.register("witches_hand") {
        WitchesHandItem(Item.Properties().stacksTo(1))
    }

    //start ARMOR

    val WITCHES_HAT = ITEMS.register("witches_hat") {
        PlatformUtils.witchesRobes(WitcheryArmorMaterials.WITCHES_ROBES, ArmorItem.Type.HELMET, Item.Properties().stacksTo(1))
    }

    val WITCHES_ROBES = ITEMS.register("witches_robes") {
        PlatformUtils.witchesRobes(WitcheryArmorMaterials.WITCHES_ROBES, ArmorItem.Type.CHESTPLATE, Item.Properties().stacksTo(1))
    }

    val WITCHES_SLIPPERS = ITEMS.register("witches_slippers") {
        PlatformUtils.witchesRobes(WitcheryArmorMaterials.WITCHES_ROBES, ArmorItem.Type.BOOTS, Item.Properties().stacksTo(1))
    }

    val BABA_YAGAS_HAT = ITEMS.register("baba_yagas_hat") {
        PlatformUtils.witchesRobes(WitcheryArmorMaterials.WITCHES_ROBES, ArmorItem.Type.HELMET, Item.Properties().stacksTo(1))
    }

    //start RESOURCES
    val MUTANDIS: RegistrySupplier<MutandisItem> = ITEMS.register("mutandis") {
        MutandisItem(Item.Properties())
    }

    val MUTANDIS_EXTREMIS: RegistrySupplier<MutandisItem> = ITEMS.register("mutandis_extremis") {
        MutandisItem(Item.Properties())
    }

    val MANDRAKE_SEEDS: RegistrySupplier<ItemNameBlockItem> = ITEMS.register("mandrake_seeds") {
        ItemNameBlockItem(WitcheryBlocks.MANDRAKE_CROP.get(), Item.Properties())
    }

    val SNOWBELL_SEEDS: RegistrySupplier<ItemNameBlockItem> = ITEMS.register("snowbell_seeds") {
        ItemNameBlockItem(WitcheryBlocks.SNOWBELL_CROP.get(), Item.Properties())
    }

    val ICY_NEEDLE: RegistrySupplier<Item> = ITEMS.register("icy_needle") {
        Item(Item.Properties())
    }

    val MANDRAKE_ROOT: RegistrySupplier<Item> = ITEMS.register("mandrake_root") {
        Item(Item.Properties())
    }

    val BELLADONNA_SEEDS: RegistrySupplier<ItemNameBlockItem> = ITEMS.register("belladonna_seeds") {
        ItemNameBlockItem(WitcheryBlocks.BELLADONNAE_CROP.get(), Item.Properties())
    }

    val BELLADONNA_FLOWER: RegistrySupplier<Item> = ITEMS.register("belladonna_flower") {
        Item(Item.Properties())
    }

    val WATER_ARTICHOKE_SEEDS: RegistrySupplier<WaterCropBlockItem> = ITEMS.register("water_artichoke_seeds") {
        WaterCropBlockItem(WitcheryBlocks.WATER_ARTICHOKE_CROP.get(), Item.Properties())
    }

    val WATER_ARTICHOKE_GLOBE: RegistrySupplier<Item> = ITEMS.register("water_artichoke_globe") {
        Item(Item.Properties())
    }

    val GARLIC: RegistrySupplier<ItemNameBlockItem> = ITEMS.register("garlic") {
        ItemNameBlockItem(WitcheryBlocks.GARLIC_CROP.get(), Item.Properties())
    }

    val WORMWOOD_SEEDS: RegistrySupplier<ItemNameBlockItem> = ITEMS.register("wormwood_seeds") {
        ItemNameBlockItem(WitcheryBlocks.WORMWOOD_CROP.get(), Item.Properties())
    }

    val WORMWOOD: RegistrySupplier<Item> = ITEMS.register("wormwood") {
        Item(Item.Properties())
    }

    val WOLFSBANE_SEEDS: RegistrySupplier<ItemNameBlockItem> = ITEMS.register("wolfsbane_seeds") {
        ItemNameBlockItem(WitcheryBlocks.WOLFSFBANE_CROP.get(), Item.Properties())
    }

    val WOLFSBANE: RegistrySupplier<Item> = ITEMS.register("wolfsbane") {
        Item(Item.Properties())
    }

    val WOOD_ASH: RegistrySupplier<Item> = ITEMS.register("wood_ash") {
        Item(Item.Properties())
    }

    val ROWAN_BERRIES: RegistrySupplier<Item> = ITEMS.register("rowan_berries") {
        Item(Item.Properties().food(Foods.SWEET_BERRIES))
    }

    val BONE_NEEDLE: RegistrySupplier<Item> = ITEMS.register("bone_needle") {
        PlatformUtils.boneNeedle
    }

    val ATTUNED_STONE: RegistrySupplier<Item> = ITEMS.register("attuned_stone") {
        AttunedStoneItem(Item.Properties())
    }

    val DEMON_HEART: RegistrySupplier<Item> = ITEMS.register("demon_heart") {
        ItemNameBlockItem(WitcheryBlocks.DEMON_HEART.get(), Item.Properties())
    }

    val GYPSUM: RegistrySupplier<Item> = ITEMS.register("gypsum") {
        Item(Item.Properties())
    }

    val REFINED_EVIL: RegistrySupplier<Item> = ITEMS.register("refined_evil") {
        Item(Item.Properties())
    }

    val WOOL_OF_BAT: RegistrySupplier<Item> = ITEMS.register("wool_of_bat") {
        Item(Item.Properties())
    }

    val TONGUE_OF_DOG: RegistrySupplier<Item> = ITEMS.register("tongue_of_dog") {
        Item(Item.Properties())
    }

    val TOE_OF_FROG: RegistrySupplier<Item> = ITEMS.register("toe_of_frog") {
        Item(Item.Properties())
    }

    val OWLETS_WING: RegistrySupplier<Item> = ITEMS.register("owlets_wing") {
        Item(Item.Properties())
    }

    val ENT_TWIG: RegistrySupplier<Item> = ITEMS.register("ent_twig") {
        Item(Item.Properties())
    }

    val REDSTONE_SOUP: RegistrySupplier<Item> = ITEMS.register("redstone_soup") {
        Item(Item.Properties())
    }

    val FLYING_OINTMENT: RegistrySupplier<Item> = ITEMS.register("flying_ointment") {
        Item(Item.Properties())
    }

    val INFERNAL_ANIMUS: RegistrySupplier<Item> = ITEMS.register("infernal_animus") {
        Item(Item.Properties())
    }

    val GHOST_OF_THE_LIGHT: RegistrySupplier<Item> = ITEMS.register("ghost_of_the_light") {
        Item(Item.Properties())
    }

    val SOUL_OF_THE_WORLD: RegistrySupplier<Item> = ITEMS.register("soul_of_the_world") {
        Item(Item.Properties())
    }

    val SPIRIT_OF_OTHERWHERE: RegistrySupplier<Item> = ITEMS.register("spirit_of_otherwhere") {
        Item(Item.Properties())
    }

    val GOLDEN_THREAD: RegistrySupplier<Item> = ITEMS.register("golden_thread") {
        Item(Item.Properties())
    }

    val IMPREGNATED_FABRIC: RegistrySupplier<Item> = ITEMS.register("impregnated_fabric") {
        Item(Item.Properties())
    }

    val MUTATING_SPRING: RegistrySupplier<Item> = ITEMS.register("mutating_spring") {
        Item(Item.Properties())
    }

    //end RESOURCES

    //start POPPETS

    val POPPET: RegistrySupplier<Item> = ITEMS.register("poppet") {
        PoppetItem(Item.Properties())
    }

    val ARMOR_PROTECTION_POPPET: RegistrySupplier<Item> = ITEMS.register("armor_protection_poppet") {
        PoppetItem(Item.Properties())
    }

    val DEATH_PROTECTION_POPPET: RegistrySupplier<Item> = ITEMS.register("death_protection_poppet") {
        PoppetItem(Item.Properties())
    }

    val HUNGER_PROTECTION_POPPET: RegistrySupplier<Item> = ITEMS.register("hunger_protection_poppet") {
        PoppetItem(Item.Properties())
    }

    val VAMPIRIC_POPPET: RegistrySupplier<Item> = ITEMS.register("vampiric_poppet") {
        PoppetItem(Item.Properties())
    }

    val VOODOO_POPPET: RegistrySupplier<Item> = ITEMS.register("voodoo_poppet") {
        PoppetItem(Item.Properties())
    }

    val VOODOO_PROTECTION_POPPET: RegistrySupplier<Item> = ITEMS.register("voodoo_protection_poppet") {
        PoppetItem(Item.Properties())
    }

    //end POPPETS

    //start JARS
    val CLAY_JAR: RegistrySupplier<Item> = ITEMS.register("clay_jar") {
        Item(Item.Properties())
    }

    val JAR: RegistrySupplier<Item> = ITEMS.register("jar") {
        Item(Item.Properties())
    }

    val BREATH_OF_THE_GODDESS: RegistrySupplier<Item> = ITEMS.register("breath_of_the_goddess") {
        Item(Item.Properties())
    }

    val WHIFF_OF_MAGIC: RegistrySupplier<Item> = ITEMS.register("whiff_of_magic") {
        Item(Item.Properties())
    }

    val FOUL_FUME: RegistrySupplier<Item> = ITEMS.register("foul_fume") {
        Item(Item.Properties())
    }

    val TEAR_OF_THE_GODDESS: RegistrySupplier<Item> = ITEMS.register("tear_of_the_goddess") {
        Item(Item.Properties())
    }

    val OIL_OF_VITRIOL: RegistrySupplier<Item> = ITEMS.register("oil_of_vitriol") {
        Item(Item.Properties())
    }

    val PHANTOM_VAPOR: RegistrySupplier<Item> = ITEMS.register("phantom_vapor") {
        Item(Item.Properties())
    }

    val EXHALE_OF_THE_HORNED_ONE: RegistrySupplier<Item> = ITEMS.register("exhale_of_the_horned_one") {
        Item(Item.Properties())
    }

    val HINT_OF_REBIRTH: RegistrySupplier<Item> = ITEMS.register("hint_of_rebirth") {
        Item(Item.Properties())
    }

    val REEK_OF_MISFORTUNE: RegistrySupplier<Item> = ITEMS.register("reek_of_misfortune") {
        Item(Item.Properties())
    }

    val ODOR_OF_PURITY: RegistrySupplier<Item> = ITEMS.register("odor_of_purity") {
        Item(Item.Properties())
    }

    val DROP_OF_LUCK: RegistrySupplier<Item> = ITEMS.register("drop_of_luck") {
        Item(Item.Properties())
    }

    val ENDER_DEW: RegistrySupplier<Item> = ITEMS.register("ender_dew") {
        Item(Item.Properties())
    }

    val DEMONS_BLOOD: RegistrySupplier<Item> = ITEMS.register("demons_blood") {
        Item(Item.Properties())
    }

    val MELLIFLUOUS_HUNGER: RegistrySupplier<Item> = ITEMS.register("mellifluous_hunger") {
        Item(Item.Properties())
    }

    val CONDENSED_FEAR: RegistrySupplier<Item> = ITEMS.register("condensed_fear") {
        Item(Item.Properties())
    }

    val FOCUSED_WILL: RegistrySupplier<Item> = ITEMS.register("focused_will") {
        Item(Item.Properties())
    }

    //end JARS

    //start CHALK
    val RITUAL_CHALK: RegistrySupplier<ChalkItem> = ITEMS.register("ritual_chalk") {
        ChalkItem(WitcheryBlocks.RITUAL_CHALK_BLOCK.get(), Item.Properties())
    }

    val GOLDEN_CHALK: RegistrySupplier<ChalkItem> = ITEMS.register("golden_chalk") {
        ChalkItem(WitcheryBlocks.GOLDEN_CHALK_BLOCK.get(), Item.Properties())
    }

    val INFERNAL_CHALK: RegistrySupplier<ChalkItem> = ITEMS.register("infernal_chalk") {
        ChalkItem(WitcheryBlocks.INFERNAL_CHALK_BLOCK.get(), Item.Properties())
    }

    val OTHERWHERE_CHALK: RegistrySupplier<ChalkItem> = ITEMS.register("otherwhere_chalk") {
        ChalkItem(WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get(), Item.Properties())
    }
    //end CHALK

    val GUIDEBOOK: RegistrySupplier<GuideBookItem> = ITEMS.register("guidebook") {
        GuideBookItem(Item.Properties())
    }

    val DEEPSLATE_ALTAR_BLOCK = ITEMS.register("deepslate_altar_block") {
        BlockItem(WitcheryBlocks.DEEPLSTAE_ALTAR_BLOCK.get(), Item.Properties())
    }

    val ALTAR: RegistrySupplier<MultiBlockItem> = ITEMS.register("altar") {
        MultiBlockItem(WitcheryBlocks.ALTAR.get(), Item.Properties(), AltarBlock.STRUCTURE)
    }

    val CAULDRON: RegistrySupplier<MultiBlockItem> = ITEMS.register("cauldron") {
        MultiBlockItem(WitcheryBlocks.CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> = ITEMS.register("copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> = ITEMS.register("waxed_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.WAXED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val EXPOSED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> = ITEMS.register("exposed_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_EXPOSED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("waxed_exposed_copper_cauldron") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_EXPOSED_COPPER_CAULDRON.get(),
                Item.Properties(),
                CauldronBlock.STRUCTURE
            )
        }

    val WEATHERED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> = ITEMS.register("weathered_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_WEATHERED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("waxed_weathered_copper_cauldron") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_WEATHERED_COPPER_CAULDRON.get(),
                Item.Properties(),
                CauldronBlock.STRUCTURE
            )
        }

    val OXIDIZED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> = ITEMS.register("oxidized_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.OXIDIZED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_OXIDIZED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("waxed_oxidized_copper_cauldron") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_OXIDIZED_COPPER_CAULDRON.get(),
                Item.Properties(),
                CauldronBlock.STRUCTURE
            )
        }

    val IRON_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("iron_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("exposed_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("weathered_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("oxidized_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("waxed_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("waxed_exposed_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("waxed_weathered_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        ITEMS.register("waxed_oxidized_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val IRON_WITCHES_OVEN: RegistrySupplier<BlockItem> = ITEMS.register("iron_witches_oven") {
        BlockItem(WitcheryBlocks.IRON_WITCHES_OVEN.get(), Item.Properties())
    }

    val COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = ITEMS.register("copper_witches_oven") {
        BlockItem(WitcheryBlocks.COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = ITEMS.register("waxed_copper_witches_oven") {
        BlockItem(WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val EXPOSED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = ITEMS.register("exposed_copper_witches_oven") {
        BlockItem(WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_EXPOSED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> =
        ITEMS.register("waxed_exposed_copper_witches_oven") {
            BlockItem(WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get(), Item.Properties())
        }

    val WEATHERED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = ITEMS.register("weathered_copper_witches_oven") {
        BlockItem(WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_WEATHERED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> =
        ITEMS.register("waxed_weathered_copper_witches_oven") {
            BlockItem(WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get(), Item.Properties())
        }

    val OXIDIZED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = ITEMS.register("oxidized_copper_witches_oven") {
        BlockItem(WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_OXIDIZED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> =
        ITEMS.register("waxed_oxidized_copper_witches_oven") {
            BlockItem(WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get(), Item.Properties())
        }

    val WAYSTONE = ITEMS.register("waystone") {
        WaystoneItem(Item.Properties().stacksTo(1).rarity(Rarity.COMMON))
    }

    val TAGLOCK = ITEMS.register("taglock") {
        TaglockItem(Item.Properties().stacksTo(1).rarity(Rarity.COMMON))
    }

    val DISTILLERY = ITEMS.register("distillery") {
        MultiBlockItem(WitcheryBlocks.DISTILLERY.get(), Item.Properties(), DistilleryBlock.STRUCTURE)
    }

    val SPINNING_WHEEL = ITEMS.register("spinning_wheel") {
        BlockItem(WitcheryBlocks.SPINNING_WHEEL.get(), Item.Properties())
    }

    // start WOOD

    val ROWAN_LOG = ITEMS.register("rowan_log") {
        BlockItem(WitcheryBlocks.ROWAN_LOG.get(), Item.Properties())
    }

    val ROWAN_WOOD = ITEMS.register("rowan_wood") {
        BlockItem(WitcheryBlocks.ROWAN_WOOD.get(), Item.Properties())
    }

    val STRIPPED_ROWAN_LOG = ITEMS.register("stripped_rowan_log") {
        BlockItem(WitcheryBlocks.STRIPPED_ROWAN_LOG.get(), Item.Properties())
    }

    val STRIPPED_ROWAN_WOOD = ITEMS.register("stripped_rowan_wood") {
        BlockItem(WitcheryBlocks.STRIPPED_ROWAN_WOOD.get(), Item.Properties())
    }

    val ROWAN_LEAVES = ITEMS.register("rowan_leaves") {
        BlockItem(WitcheryBlocks.ROWAN_LEAVES.get(), Item.Properties())
    }

    val ROWAN_BERRY_LEAVES = ITEMS.register("rowan_berry_leaves") {
        BlockItem(WitcheryBlocks.ROWAN_BERRY_LEAVES.get(), Item.Properties())
    }

    val ROWAN_PLANKS = ITEMS.register("rowan_planks") {
        BlockItem(WitcheryBlocks.ROWAN_PLANKS.get(), Item.Properties())
    }

    val ROWAN_STAIRS = ITEMS.register("rowan_stairs") {
        BlockItem(WitcheryBlocks.ROWAN_STAIRS.get(), Item.Properties())
    }

    val ROWAN_SLAB = ITEMS.register("rowan_slab") {
        BlockItem(WitcheryBlocks.ROWAN_SLAB.get(), Item.Properties())
    }

    val ROWAN_FENCE = ITEMS.register("rowan_fence") {
        BlockItem(WitcheryBlocks.ROWAN_FENCE.get(), Item.Properties())
    }

    val ROWAN_FENCE_GATE = ITEMS.register("rowan_fence_gate") {
        BlockItem(WitcheryBlocks.ROWAN_FENCE_GATE.get(), Item.Properties())
    }

    val ROWAN_DOOR = ITEMS.register("rowan_door") {
        BlockItem(WitcheryBlocks.ROWAN_DOOR.get(), Item.Properties())
    }

    val ROWAN_TRAPDOOR = ITEMS.register("rowan_trapdoor") {
        BlockItem(WitcheryBlocks.ROWAN_TRAPDOOR.get(), Item.Properties())
    }

    val ROWAN_PRESSURE_PLATE = ITEMS.register("rowan_pressure_plate") {
        BlockItem(WitcheryBlocks.ROWAN_PRESSURE_PLATE.get(), Item.Properties())
    }

    val ROWAN_BUTTON = ITEMS.register("rowan_button") {
        BlockItem(WitcheryBlocks.ROWAN_BUTTON.get(), Item.Properties())
    }

    val ROWAN_SAPLING = ITEMS.register("rowan_sapling") {
        BlockItem(WitcheryBlocks.ROWAN_SAPLING.get(), Item.Properties())
    }

    val ROWAN_SIGN = ITEMS.register("rowan_sign") {
        SignItem(Item.Properties(), WitcheryBlocks.ROWAN_SIGN.get(), WitcheryBlocks.ROWAN_WALL_SIGN.get())
    }

    val ROWAN_HANGING_SIGN = ITEMS.register("rowan_hanging_sign") {
        HangingSignItem(
            WitcheryBlocks.ROWAN_HANGING_SIGN.get(),
            WitcheryBlocks.ROWAN_WALL_HANGING_SIGN.get(),
            Item.Properties()
        )
    }

    val ROWAN_BOAT = ITEMS.register("rowan_boat") {
        CustomBoatItem(false, BoatTypeHelper.getRowanBoatType(), Item.Properties())
    }

    val ROWAN_CHEST_BOAT = ITEMS.register("rowan_chest_boat") {
        CustomBoatItem(true, BoatTypeHelper.getRowanBoatType(), Item.Properties())
    }


    val ALDER_LOG = ITEMS.register("alder_log") {
        BlockItem(WitcheryBlocks.ALDER_LOG.get(), Item.Properties())
    }

    val ALDER_WOOD = ITEMS.register("alder_wood") {
        BlockItem(WitcheryBlocks.ALDER_WOOD.get(), Item.Properties())
    }

    val STRIPPED_ALDER_LOG = ITEMS.register("stripped_alder_log") {
        BlockItem(WitcheryBlocks.STRIPPED_ALDER_LOG.get(), Item.Properties())
    }

    val STRIPPED_ALDER_WOOD = ITEMS.register("stripped_alder_wood") {
        BlockItem(WitcheryBlocks.STRIPPED_ALDER_WOOD.get(), Item.Properties())
    }

    val ALDER_LEAVES = ITEMS.register("alder_leaves") {
        BlockItem(WitcheryBlocks.ALDER_LEAVES.get(), Item.Properties())
    }

    val ALDER_PLANKS = ITEMS.register("alder_planks") {
        BlockItem(WitcheryBlocks.ALDER_PLANKS.get(), Item.Properties())
    }

    val ALDER_STAIRS = ITEMS.register("alder_stairs") {
        BlockItem(WitcheryBlocks.ALDER_STAIRS.get(), Item.Properties())
    }

    val ALDER_SLAB = ITEMS.register("alder_slab") {
        BlockItem(WitcheryBlocks.ALDER_SLAB.get(), Item.Properties())
    }

    val ALDER_FENCE = ITEMS.register("alder_fence") {
        BlockItem(WitcheryBlocks.ALDER_FENCE.get(), Item.Properties())
    }

    val ALDER_FENCE_GATE = ITEMS.register("alder_fence_gate") {
        BlockItem(WitcheryBlocks.ALDER_FENCE_GATE.get(), Item.Properties())
    }

    val ALDER_DOOR = ITEMS.register("alder_door") {
        BlockItem(WitcheryBlocks.ALDER_DOOR.get(), Item.Properties())
    }

    val ALDER_TRAPDOOR = ITEMS.register("alder_trapdoor") {
        BlockItem(WitcheryBlocks.ALDER_TRAPDOOR.get(), Item.Properties())
    }

    val ALDER_PRESSURE_PLATE = ITEMS.register("alder_pressure_plate") {
        BlockItem(WitcheryBlocks.ALDER_PRESSURE_PLATE.get(), Item.Properties())
    }

    val ALDER_BUTTON = ITEMS.register("alder_button") {
        BlockItem(WitcheryBlocks.ALDER_BUTTON.get(), Item.Properties())
    }

    val ALDER_SAPLING = ITEMS.register("alder_sapling") {
        BlockItem(WitcheryBlocks.ALDER_SAPLING.get(), Item.Properties())
    }

    val ALDER_SIGN = ITEMS.register("alder_sign") {
        SignItem(Item.Properties(), WitcheryBlocks.ALDER_SIGN.get(), WitcheryBlocks.ALDER_WALL_SIGN.get())
    }

    val ALDER_HANGING_SIGN = ITEMS.register("alder_hanging_sign") {
        HangingSignItem(
            WitcheryBlocks.ALDER_HANGING_SIGN.get(),
            WitcheryBlocks.ALDER_WALL_HANGING_SIGN.get(),
            Item.Properties()
        )
    }

    val ALDER_BOAT = ITEMS.register("alder_boat") {
        CustomBoatItem(false, BoatTypeHelper.getAlderBoatType(), Item.Properties())
    }

    val ALDER_CHEST_BOAT = ITEMS.register("alder_chest_boat") {
        CustomBoatItem(true, BoatTypeHelper.getAlderBoatType(), Item.Properties())
    }


    val HAWTHORN_LOG = ITEMS.register("hawthorn_log") {
        BlockItem(WitcheryBlocks.HAWTHORN_LOG.get(), Item.Properties())
    }

    val HAWTHORN_WOOD = ITEMS.register("hawthorn_wood") {
        BlockItem(WitcheryBlocks.HAWTHORN_WOOD.get(), Item.Properties())
    }

    val STRIPPED_HAWTHORN_LOG = ITEMS.register("stripped_hawthorn_log") {
        BlockItem(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get(), Item.Properties())
    }

    val STRIPPED_HAWTHORN_WOOD = ITEMS.register("stripped_hawthorn_wood") {
        BlockItem(WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get(), Item.Properties())
    }

    val HAWTHORN_LEAVES = ITEMS.register("hawthorn_leaves") {
        BlockItem(WitcheryBlocks.HAWTHORN_LEAVES.get(), Item.Properties())
    }

    val HAWTHORN_PLANKS = ITEMS.register("hawthorn_planks") {
        BlockItem(WitcheryBlocks.HAWTHORN_PLANKS.get(), Item.Properties())
    }

    val HAWTHORN_STAIRS = ITEMS.register("hawthorn_stairs") {
        BlockItem(WitcheryBlocks.HAWTHORN_STAIRS.get(), Item.Properties())
    }

    val HAWTHORN_SLAB = ITEMS.register("hawthorn_slab") {
        BlockItem(WitcheryBlocks.HAWTHORN_SLAB.get(), Item.Properties())
    }

    val HAWTHORN_FENCE = ITEMS.register("hawthorn_fence") {
        BlockItem(WitcheryBlocks.HAWTHORN_FENCE.get(), Item.Properties())
    }

    val HAWTHORN_FENCE_GATE = ITEMS.register("hawthorn_fence_gate") {
        BlockItem(WitcheryBlocks.HAWTHORN_FENCE_GATE.get(), Item.Properties())
    }

    val HAWTHORN_DOOR = ITEMS.register("hawthorn_door") {
        BlockItem(WitcheryBlocks.HAWTHORN_DOOR.get(), Item.Properties())
    }

    val HAWTHORN_TRAPDOOR = ITEMS.register("hawthorn_trapdoor") {
        BlockItem(WitcheryBlocks.HAWTHORN_TRAPDOOR.get(), Item.Properties())
    }

    val HAWTHORN_PRESSURE_PLATE = ITEMS.register("hawthorn_pressure_plate") {
        BlockItem(WitcheryBlocks.HAWTHORN_PRESSURE_PLATE.get(), Item.Properties())
    }

    val HAWTHORN_BUTTON = ITEMS.register("hawthorn_button") {
        BlockItem(WitcheryBlocks.HAWTHORN_BUTTON.get(), Item.Properties())
    }

    val HAWTHORN_SAPLING = ITEMS.register("hawthorn_sapling") {
        BlockItem(WitcheryBlocks.HAWTHORN_SAPLING.get(), Item.Properties())
    }

    val HAWTHORN_SIGN = ITEMS.register("hawthorn_sign") {
        SignItem(Item.Properties(), WitcheryBlocks.HAWTHORN_SIGN.get(), WitcheryBlocks.HAWTHORN_WALL_SIGN.get())
    }

    val HAWTHORN_HANGING_SIGN = ITEMS.register("hawthorn_hanging_sign") {
        HangingSignItem(
            WitcheryBlocks.HAWTHORN_HANGING_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_HANGING_SIGN.get(),
            Item.Properties()
        )
    }

    val HAWTHORN_BOAT = ITEMS.register("hawthorn_boat") {
        CustomBoatItem(false, BoatTypeHelper.getHawthornBoatType(), Item.Properties())
    }

    val HAWTHORN_CHEST_BOAT = ITEMS.register("hawthorn_chest_boat") {
        CustomBoatItem(true, BoatTypeHelper.getHawthornBoatType(), Item.Properties())
    }

    // end WOOD

    // start AUGMENTS

    val IRON_CANDELABRA = ITEMS.register("iron_candelabra") {
        BlockItem(WitcheryBlocks.IRON_CANDELABRA.get(), Item.Properties())
    }

    val WHITE_IRON_CANDELABRA = ITEMS.register("white_iron_candelabra") {
        BlockItem(WitcheryBlocks.WHITE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val ORANGE_IRON_CANDELABRA = ITEMS.register("orange_iron_candelabra") {
        BlockItem(WitcheryBlocks.ORANGE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val MAGENTA_IRON_CANDELABRA = ITEMS.register("magenta_iron_candelabra") {
        BlockItem(WitcheryBlocks.MAGENTA_IRON_CANDELABRA.get(), Item.Properties())
    }

    val LIGHT_BLUE_IRON_CANDELABRA = ITEMS.register("light_blue_iron_candelabra") {
        BlockItem(WitcheryBlocks.LIGHT_BLUE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val YELLOW_IRON_CANDELABRA = ITEMS.register("yellow_iron_candelabra") {
        BlockItem(WitcheryBlocks.YELLOW_IRON_CANDELABRA.get(), Item.Properties())
    }

    val LIME_IRON_CANDELABRA = ITEMS.register("lime_iron_candelabra") {
        BlockItem(WitcheryBlocks.LIME_IRON_CANDELABRA.get(), Item.Properties())
    }

    val PINK_IRON_CANDELABRA = ITEMS.register("pink_iron_candelabra") {
        BlockItem(WitcheryBlocks.PINK_IRON_CANDELABRA.get(), Item.Properties())
    }

    val GRAY_IRON_CANDELABRA = ITEMS.register("gray_iron_candelabra") {
        BlockItem(WitcheryBlocks.GRAY_IRON_CANDELABRA.get(), Item.Properties())
    }

    val LIGHT_GRAY_IRON_CANDELABRA = ITEMS.register("light_gray_iron_candelabra") {
        BlockItem(WitcheryBlocks.LIGHT_GRAY_IRON_CANDELABRA.get(), Item.Properties())
    }

    val CYAN_IRON_CANDELABRA = ITEMS.register("cyan_iron_candelabra") {
        BlockItem(WitcheryBlocks.CYAN_IRON_CANDELABRA.get(), Item.Properties())
    }

    val PURPLE_IRON_CANDELABRA = ITEMS.register("purple_iron_candelabra") {
        BlockItem(WitcheryBlocks.PURPLE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val BLUE_IRON_CANDELABRA = ITEMS.register("blue_iron_candelabra") {
        BlockItem(WitcheryBlocks.BLUE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val BROWN_IRON_CANDELABRA = ITEMS.register("brown_iron_candelabra") {
        BlockItem(WitcheryBlocks.BROWN_IRON_CANDELABRA.get(), Item.Properties())
    }

    val GREEN_IRON_CANDELABRA = ITEMS.register("green_iron_candelabra") {
        BlockItem(WitcheryBlocks.GREEN_IRON_CANDELABRA.get(), Item.Properties())
    }

    val RED_IRON_CANDELABRA = ITEMS.register("red_iron_candelabra") {
        BlockItem(WitcheryBlocks.RED_IRON_CANDELABRA.get(), Item.Properties())
    }

    val BLACK_IRON_CANDELABRA = ITEMS.register("black_iron_candelabra") {
        BlockItem(WitcheryBlocks.BLACK_IRON_CANDELABRA.get(), Item.Properties())
    }

    val ARTHANA = ITEMS.register("arthana") {
        ArthanaItem(Item.Properties())
    }

    val CHALICE = ITEMS.register("chalice") {
        ChaliceBlockItem(WitcheryBlocks.CHALICE.get(), Item.Properties())
    }

    val PENTACLE = ITEMS.register("pentacle") {
        BlockItem(WitcheryBlocks.PENTACLE.get(), Item.Properties())
    }

    // start Mutated Plants

    val BLOOD_POPPY = ITEMS.register("blood_poppy") {
        BlockItem(WitcheryBlocks.BLOOD_POPPY.get(), Item.Properties())
    }
}