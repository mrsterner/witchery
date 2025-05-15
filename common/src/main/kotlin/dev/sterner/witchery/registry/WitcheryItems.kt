package dev.sterner.witchery.registry

import com.google.common.base.Supplier
import dev.architectury.core.item.ArchitecturyBucketItem
import dev.architectury.core.item.ArchitecturySpawnEggItem
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.multiblock.MultiBlockItem
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.cauldron.CauldronBlock
import dev.sterner.witchery.block.distillery.DistilleryBlock
import dev.sterner.witchery.block.effigy.EffigyBlock
import dev.sterner.witchery.block.oven.OvenFumeExtensionBlock
import dev.sterner.witchery.block.werewolf_altar.WerewolfAltarBlock
import dev.sterner.witchery.item.*
import dev.sterner.witchery.item.accessories.*
import dev.sterner.witchery.item.brew.*
import dev.sterner.witchery.item.potion.WitcheryPotionItem
import dev.sterner.witchery.platform.BoatTypeHelper
import dev.sterner.witchery.platform.PlatformUtils
import net.minecraft.core.registries.Registries
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.food.Foods
import net.minecraft.world.item.*
import java.awt.Color


object WitcheryItems {

    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(Witchery.MODID, Registries.ITEM)
    val LANG_HELPER = mutableListOf<String>()

    fun <T : Item> register(name: String, addLang: Boolean = true, item: Supplier<T>): RegistrySupplier<T> {
        if (addLang) {
            LANG_HELPER.add(name)
        }
        return ITEMS.register(name, item)
    }

    val DEBUG = register("debug") {
        DebugWand(Item.Properties())
    }

    val DEATH_SICKLE = register("death_sickle") {
        DeathSickleItem(Item.Properties().stacksTo(1))
    }

    val HUNTSMAN_SPEAR
            = register("huntsman_spear") {
        HuntsmanSpearItem(Item.Properties().stacksTo(1))
    }

    val GLINTWEED: RegistrySupplier<BlockItem> = register("glintweed") {
        BlockItem(WitcheryBlocks.GLINTWEED.get(), Item.Properties())
    }

    val EMBER_MOSS: RegistrySupplier<BlockItem> = register("ember_moss") {
        BlockItem(WitcheryBlocks.EMBER_MOSS.get(), Item.Properties())
    }

    val SPANISH_MOSS: RegistrySupplier<BlockItem> = register("spanish_moss") {
        BlockItem(WitcheryBlocks.SPANISH_MOSS.get(), Item.Properties())
    }

    val INFINITY_EGG: RegistrySupplier<BlockItem> = register("infinity_egg") {
        BlockItem(WitcheryBlocks.INFINITY_EGG.get(), Item.Properties())
    }

    val WITCHES_HAND: RegistrySupplier<WitchesHandItem> = register("witches_hand") {
        WitchesHandItem(Item.Properties().stacksTo(1))
    }

    val BROOM: RegistrySupplier<BroomItem> = register("broom") {
        BroomItem(Item.Properties().stacksTo(1))
    }

    val SEER_STONE: RegistrySupplier<SeerStoneItem> = register("seer_stone") {
        SeerStoneItem(Item.Properties().stacksTo(1))
    }

    //start ARMOR

    val WITCHES_HAT: RegistrySupplier<ArmorItem> = register("witches_hat") {
        PlatformUtils.witchesRobes(
            WitcheryArmorMaterials.WITCHES_ROBES,
            ArmorItem.Type.HELMET,
            Item.Properties().stacksTo(1)
        )
    }

    val WITCHES_ROBES: RegistrySupplier<ArmorItem> = register("witches_robes") {
        PlatformUtils.witchesRobes(
            WitcheryArmorMaterials.WITCHES_ROBES,
            ArmorItem.Type.CHESTPLATE,
            Item.Properties().stacksTo(1)
        )
    }

    val WITCHES_SLIPPERS: RegistrySupplier<ArmorItem> = register("witches_slippers") {
        PlatformUtils.witchesRobes(
            WitcheryArmorMaterials.WITCHES_ROBES,
            ArmorItem.Type.BOOTS,
            Item.Properties().stacksTo(1)
        )
    }

    val BABA_YAGAS_HAT: RegistrySupplier<ArmorItem> = register("baba_yagas_hat") {
        PlatformUtils.witchesRobes(
            WitcheryArmorMaterials.WITCHES_ROBES,
            ArmorItem.Type.HELMET,
            Item.Properties().stacksTo(1)
        )
    }

    val HUNTER_HELMET: RegistrySupplier<ArmorItem> = register("hunter_helmet") {
        PlatformUtils.hunterArmor(WitcheryArmorMaterials.HUNTER, ArmorItem.Type.HELMET, Item.Properties().stacksTo(1))
    }

    val HUNTER_CHESTPLATE: RegistrySupplier<ArmorItem> = register("hunter_chestplate") {
        PlatformUtils.hunterArmor(
            WitcheryArmorMaterials.HUNTER,
            ArmorItem.Type.CHESTPLATE,
            Item.Properties().stacksTo(1)
        )
    }

    val HUNTER_LEGGINGS: RegistrySupplier<ArmorItem> = register("hunter_leggings") {
        PlatformUtils.hunterArmor(WitcheryArmorMaterials.HUNTER, ArmorItem.Type.LEGGINGS, Item.Properties().stacksTo(1))
    }

    val HUNTER_BOOTS: RegistrySupplier<ArmorItem> = register("hunter_boots") {
        PlatformUtils.hunterArmor(WitcheryArmorMaterials.HUNTER, ArmorItem.Type.BOOTS, Item.Properties().stacksTo(1))
    }

    val TOP_HAT: RegistrySupplier<ArmorItem> = register("top_hat") {
        PlatformUtils.dapper(
            WitcheryArmorMaterials.DAPPER,
            ArmorItem.Type.HELMET,
            Item.Properties().stacksTo(1)
        )
    }

    val DRESS_COAT: RegistrySupplier<ArmorItem> = register("dress_coat") {
        PlatformUtils.dapper(
            WitcheryArmorMaterials.DAPPER,
            ArmorItem.Type.CHESTPLATE,
            Item.Properties().stacksTo(1)
        )
    }

    val TROUSERS: RegistrySupplier<ArmorItem> = register("trousers") {
        PlatformUtils.dapper(WitcheryArmorMaterials.DAPPER, ArmorItem.Type.LEGGINGS, Item.Properties().stacksTo(1))
    }

    val OXFORD_BOOTS: RegistrySupplier<ArmorItem> = register("oxford_boots") {
        PlatformUtils.dapper(WitcheryArmorMaterials.DAPPER, ArmorItem.Type.BOOTS, Item.Properties().stacksTo(1))
    }


    //start RESOURCES
    val MUTANDIS: RegistrySupplier<MutandisItem> = register("mutandis") {
        MutandisItem(Item.Properties())
    }

    val MUTANDIS_EXTREMIS: RegistrySupplier<MutandisItem> = register("mutandis_extremis") {
        MutandisItem(Item.Properties())
    }

    val MANDRAKE_SEEDS: RegistrySupplier<ItemNameBlockItem> = register("mandrake_seeds") {
        ItemNameBlockItem(WitcheryBlocks.MANDRAKE_CROP.get(), Item.Properties())
    }

    val SNOWBELL_SEEDS: RegistrySupplier<ItemNameBlockItem> = register("snowbell_seeds") {
        ItemNameBlockItem(WitcheryBlocks.SNOWBELL_CROP.get(), Item.Properties())
    }

    val ICY_NEEDLE: RegistrySupplier<Item> = register("icy_needle") {
        IcyNeedleItem(Item.Properties())
    }

    val MANDRAKE_ROOT: RegistrySupplier<Item> = register("mandrake_root") {
        Item(Item.Properties())
    }

    val BELLADONNA_SEEDS: RegistrySupplier<ItemNameBlockItem> = register("belladonna_seeds") {
        ItemNameBlockItem(WitcheryBlocks.BELLADONNA_CROP.get(), Item.Properties())
    }

    val BELLADONNA_FLOWER: RegistrySupplier<Item> = register("belladonna_flower") {
        Item(Item.Properties())
    }

    val WATER_ARTICHOKE_SEEDS: RegistrySupplier<WaterCropBlockItem> = register("water_artichoke_seeds") {
        WaterCropBlockItem(WitcheryBlocks.WATER_ARTICHOKE_CROP.get(), Item.Properties())
    }

    val WATER_ARTICHOKE_GLOBE: RegistrySupplier<Item> = register("water_artichoke_globe") {
        Item(Item.Properties())
    }

    val GARLIC: RegistrySupplier<ItemNameBlockItem> = register("garlic") {
        ItemNameBlockItem(WitcheryBlocks.GARLIC_CROP.get(), Item.Properties())
    }

    val WORMWOOD_SEEDS: RegistrySupplier<ItemNameBlockItem> = register("wormwood_seeds") {
        ItemNameBlockItem(WitcheryBlocks.WORMWOOD_CROP.get(), Item.Properties())
    }

    val WORMWOOD: RegistrySupplier<Item> = register("wormwood") {
        Item(Item.Properties())
    }

    val WOLFSBANE_SEEDS: RegistrySupplier<ItemNameBlockItem> = register("wolfsbane_seeds") {
        ItemNameBlockItem(WitcheryBlocks.WOLFSFBANE_CROP.get(), Item.Properties())
    }

    val WOLFSBANE: RegistrySupplier<Item> = register("wolfsbane") {
        Item(Item.Properties())
    }

    val WOOD_ASH: RegistrySupplier<Item> = register("wood_ash") {
        Item(Item.Properties())
    }

    val ROWAN_BERRIES: RegistrySupplier<Item> = register("rowan_berries") {
        Item(Item.Properties().food(Foods.SWEET_BERRIES))
    }

    val BONE_NEEDLE: RegistrySupplier<Item> = register("bone_needle") {
        PlatformUtils.boneNeedle
    }

    val ATTUNED_STONE: RegistrySupplier<Item> = register("attuned_stone") {
        AttunedStoneItem(Item.Properties())
    }

    val DEMON_HEART: RegistrySupplier<Item> = register("demon_heart") {
        ItemNameBlockItem(WitcheryBlocks.DEMON_HEART.get(), Item.Properties())
    }

    val GYPSUM: RegistrySupplier<Item> = register("gypsum") {
        Item(Item.Properties())
    }

    val REFINED_EVIL: RegistrySupplier<Item> = register("refined_evil") {
        Item(Item.Properties())
    }

    val WOOL_OF_BAT: RegistrySupplier<Item> = register("wool_of_bat") {
        Item(Item.Properties())
    }

    val TONGUE_OF_DOG: RegistrySupplier<Item> = register("tongue_of_dog") {
        Item(Item.Properties())
    }

    val TOE_OF_FROG: RegistrySupplier<Item> = register("toe_of_frog") {
        Item(Item.Properties())
    }

    val OWLETS_WING: RegistrySupplier<Item> = register("owlets_wing") {
        Item(Item.Properties())
    }

    val ENT_TWIG: RegistrySupplier<Item> = register("ent_twig") {
        Item(Item.Properties())
    }

    val SPECTRAL_DUST: RegistrySupplier<Item> = register("spectral_dust") {
        Item(Item.Properties())
    }

    val REDSTONE_SOUP: RegistrySupplier<Item> = register("redstone_soup") {
        Item(Item.Properties())
    }

    val HAPPENSTANCE_OIL: RegistrySupplier<Item> = register("happenstance_oil") {
        Item(Item.Properties())
    }

    val FLYING_OINTMENT: RegistrySupplier<Item> = register("flying_ointment") {
        Item(Item.Properties())
    }

    val INFERNAL_ANIMUS: RegistrySupplier<Item> = register("infernal_animus") {
        Item(Item.Properties())
    }

    val GHOST_OF_THE_LIGHT: RegistrySupplier<Item> = register("ghost_of_the_light") {
        Item(Item.Properties())
    }

    val SOUL_OF_THE_WORLD: RegistrySupplier<Item> = register("soul_of_the_world") {
        Item(Item.Properties())
    }

    val SPIRIT_OF_OTHERWHERE: RegistrySupplier<Item> = register("spirit_of_otherwhere") {
        Item(Item.Properties())
    }

    val NECROMANTIC_SOULBIND: RegistrySupplier<Item> = register("necromantic_soulbind") {
        Item(Item.Properties())
    }

    val GOLDEN_THREAD: RegistrySupplier<Item> = register("golden_thread") {
        Item(Item.Properties())
    }

    val IMPREGNATED_FABRIC: RegistrySupplier<Item> = register("impregnated_fabric") {
        Item(Item.Properties())
    }

    val MUTATING_SPRING: RegistrySupplier<Item> = register("mutating_spring") {
        MutatingSpringItem(Item.Properties())
    }

    val TORMENTED_TWINE: RegistrySupplier<Item> = register("tormented_twine") {
        Item(Item.Properties())
    }

    val FANCIFUL_THREAD: RegistrySupplier<Item> = register("fanciful_thread") {
        Item(Item.Properties())
    }

    val WINE_GLASS: RegistrySupplier<Item> = register("wine_glass") {
        WineGlassItem(Item.Properties())
    }

    val NECROMANTIC_STONE: RegistrySupplier<Item> = register("necromantic_stone") {
        object : Item(Properties()) {
            override fun isFoil(stack: ItemStack): Boolean {
                return true
            }
        }
    }

    //end RESOURCES

    //start POPPETS

    val POPPET: RegistrySupplier<Item> = register("poppet") {
        PoppetItem(Item.Properties())
    }

    val ARMOR_PROTECTION_POPPET: RegistrySupplier<Item> = register("armor_protection_poppet") {
        PoppetItem(Item.Properties().durability(4))
    }

    val DEATH_PROTECTION_POPPET: RegistrySupplier<Item> = register("death_protection_poppet") {
        PoppetItem(Item.Properties().durability(1))
    }

    val HUNGER_PROTECTION_POPPET: RegistrySupplier<Item> = register("hunger_protection_poppet") {
        PoppetItem(Item.Properties().durability(4))
    }

    val VAMPIRIC_POPPET: RegistrySupplier<Item> = register("vampiric_poppet") {
        PoppetItem(Item.Properties().durability(128))
    }

    val VOODOO_POPPET: RegistrySupplier<Item> = register("voodoo_poppet") {
        VoodooPoppetItem(Item.Properties().durability(1024))
    }

    val VOODOO_PROTECTION_POPPET: RegistrySupplier<Item> = register("voodoo_protection_poppet") {
        PoppetItem(Item.Properties().durability(1))
    }

    //end POPPETS

    //start JARS

    val CLAY_JAR: RegistrySupplier<Item> = register("clay_jar") {
        Item(Item.Properties())
    }

    val JAR: RegistrySupplier<Item> = register("jar") {
        Item(Item.Properties())
    }

    val BREATH_OF_THE_GODDESS: RegistrySupplier<Item> = register("breath_of_the_goddess") {
        Item(Item.Properties())
    }

    val WHIFF_OF_MAGIC: RegistrySupplier<Item> = register("whiff_of_magic") {
        Item(Item.Properties())
    }

    val FOUL_FUME: RegistrySupplier<Item> = register("foul_fume") {
        Item(Item.Properties())
    }

    val TEAR_OF_THE_GODDESS: RegistrySupplier<Item> = register("tear_of_the_goddess") {
        Item(Item.Properties())
    }

    val OIL_OF_VITRIOL: RegistrySupplier<Item> = register("oil_of_vitriol") {
        Item(Item.Properties())
    }

    val PHANTOM_VAPOR: RegistrySupplier<Item> = register("phantom_vapor") {
        Item(Item.Properties())
    }

    val EXHALE_OF_THE_HORNED_ONE: RegistrySupplier<Item> = register("exhale_of_the_horned_one") {
        Item(Item.Properties())
    }

    val HINT_OF_REBIRTH: RegistrySupplier<Item> = register("hint_of_rebirth") {
        Item(Item.Properties())
    }

    val REEK_OF_MISFORTUNE: RegistrySupplier<Item> = register("reek_of_misfortune") {
        Item(Item.Properties())
    }

    val ODOR_OF_PURITY: RegistrySupplier<Item> = register("odor_of_purity") {
        Item(Item.Properties())
    }

    val DROP_OF_LUCK: RegistrySupplier<Item> = register("drop_of_luck") {
        Item(Item.Properties())
    }

    val ENDER_DEW: RegistrySupplier<Item> = register("ender_dew") {
        Item(Item.Properties())
    }

    val DEMONS_BLOOD: RegistrySupplier<Item> = register("demons_blood") {
        Item(Item.Properties())
    }

    val MELLIFLUOUS_HUNGER: RegistrySupplier<Item> = register("mellifluous_hunger") {
        Item(Item.Properties())
    }

    val CONDENSED_FEAR: RegistrySupplier<Item> = register("condensed_fear") {
        Item(Item.Properties())
    }

    val FOCUSED_WILL: RegistrySupplier<Item> = register("focused_will") {
        Item(Item.Properties())
    }

    //end JARS

    //start CHALK

    val RITUAL_CHALK: RegistrySupplier<ChalkItem> = register("ritual_chalk") {
        ChalkItem(WitcheryBlocks.RITUAL_CHALK_BLOCK.get(), Item.Properties())
    }

    val GOLDEN_CHALK: RegistrySupplier<ChalkItem> = register("golden_chalk") {
        ChalkItem(WitcheryBlocks.GOLDEN_CHALK_BLOCK.get(), Item.Properties())
    }

    val INFERNAL_CHALK: RegistrySupplier<ChalkItem> = register("infernal_chalk") {
        ChalkItem(WitcheryBlocks.INFERNAL_CHALK_BLOCK.get(), Item.Properties())
    }

    val OTHERWHERE_CHALK: RegistrySupplier<ChalkItem> = register("otherwhere_chalk") {
        ChalkItem(WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get(), Item.Properties())
    }
    //end CHALK

    val GUIDEBOOK: RegistrySupplier<GuideBookItem> = register("guidebook") {
        GuideBookItem(Item.Properties())
    }

    val WITCHERY_POTION: RegistrySupplier<WitcheryPotionItem> = register("witchery_potion") {
        WitcheryPotionItem(Item.Properties())
    }

    val DEEPSLATE_ALTAR_BLOCK: RegistrySupplier<BlockItem> = register("deepslate_altar_block") {
        BlockItem(WitcheryBlocks.DEEPLSTAE_ALTAR_BLOCK.get(), Item.Properties())
    }

    val ALTAR: RegistrySupplier<MultiBlockItem> = register("altar") {
        MultiBlockItem(WitcheryBlocks.ALTAR.get(), Item.Properties(), AltarBlock.STRUCTURE)
    }

    val CAULDRON: RegistrySupplier<MultiBlockItem> = register("cauldron") {
        MultiBlockItem(WitcheryBlocks.CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> = register("copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> = register("waxed_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.WAXED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val EXPOSED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> = register("exposed_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_EXPOSED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> =
        register("waxed_exposed_copper_cauldron") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_EXPOSED_COPPER_CAULDRON.get(),
                Item.Properties(),
                CauldronBlock.STRUCTURE
            )
        }

    val WEATHERED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> = register("weathered_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_WEATHERED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> =
        register("waxed_weathered_copper_cauldron") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_WEATHERED_COPPER_CAULDRON.get(),
                Item.Properties(),
                CauldronBlock.STRUCTURE
            )
        }

    val OXIDIZED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> = register("oxidized_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.OXIDIZED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_OXIDIZED_COPPER_CAULDRON: RegistrySupplier<MultiBlockItem> =
        register("waxed_oxidized_copper_cauldron") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_OXIDIZED_COPPER_CAULDRON.get(),
                Item.Properties(),
                CauldronBlock.STRUCTURE
            )
        }

    val IRON_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        register("iron_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        register("copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        register("exposed_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        register("weathered_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        register("oxidized_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        register("waxed_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        register("waxed_exposed_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        register("waxed_weathered_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> =
        register("waxed_oxidized_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val IRON_WITCHES_OVEN: RegistrySupplier<BlockItem> = register("iron_witches_oven") {
        BlockItem(WitcheryBlocks.IRON_WITCHES_OVEN.get(), Item.Properties())
    }

    val COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = register("copper_witches_oven") {
        BlockItem(WitcheryBlocks.COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = register("waxed_copper_witches_oven") {
        BlockItem(WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val EXPOSED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = register("exposed_copper_witches_oven") {
        BlockItem(WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_EXPOSED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> =
        register("waxed_exposed_copper_witches_oven") {
            BlockItem(WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get(), Item.Properties())
        }

    val WEATHERED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = register("weathered_copper_witches_oven") {
        BlockItem(WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_WEATHERED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> =
        register("waxed_weathered_copper_witches_oven") {
            BlockItem(WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get(), Item.Properties())
        }

    val OXIDIZED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = register("oxidized_copper_witches_oven") {
        BlockItem(WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_OXIDIZED_COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> =
        register("waxed_oxidized_copper_witches_oven") {
            BlockItem(WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get(), Item.Properties())
        }

    val WAYSTONE: RegistrySupplier<WaystoneItem> = register("waystone") {
        WaystoneItem(Item.Properties().stacksTo(1).rarity(Rarity.COMMON))
    }

    val TAGLOCK: RegistrySupplier<TaglockItem> = register("taglock") {
        TaglockItem(Item.Properties().stacksTo(1).rarity(Rarity.COMMON).craftRemainder(Items.GLASS_BOTTLE))
    }

    val DISTILLERY: RegistrySupplier<MultiBlockItem> = register("distillery") {
        MultiBlockItem(WitcheryBlocks.DISTILLERY.get(), Item.Properties(), DistilleryBlock.STRUCTURE)
    }

    val BEAR_TRAP: RegistrySupplier<BlockItem> = register("bear_trap") {
        BlockItem(WitcheryBlocks.BEAR_TRAP.get(), Item.Properties())
    }

    val SPINNING_WHEEL: RegistrySupplier<BlockItem> = register("spinning_wheel") {
        BlockItem(WitcheryBlocks.SPINNING_WHEEL.get(), Item.Properties())
    }

    val BRAZIER: RegistrySupplier<BlockItem> = register("brazier") {
        BlockItem(WitcheryBlocks.BRAZIER.get(), Item.Properties())
    }

    val WEREWOLF_ALTAR: RegistrySupplier<MultiBlockItem> = register("werewolf_altar") {
        MultiBlockItem(WitcheryBlocks.WEREWOLF_ALTAR.get(), Item.Properties(), WerewolfAltarBlock.STRUCTURE)
    }

    val BLOOD_CRUCIBLE = register("blood_crucible") {
        BlockItem(WitcheryBlocks.BLOOD_CRUCIBLE.get(), Item.Properties())
    }

    val COFFIN = register("coffin") {
        BlockItem(WitcheryBlocks.COFFIN.get(), Item.Properties())
    }

    // start WOOD

    val ROWAN_LOG: RegistrySupplier<BlockItem> = register("rowan_log") {
        BlockItem(WitcheryBlocks.ROWAN_LOG.get(), Item.Properties())
    }

    val ROWAN_WOOD: RegistrySupplier<BlockItem> = register("rowan_wood") {
        BlockItem(WitcheryBlocks.ROWAN_WOOD.get(), Item.Properties())
    }

    val STRIPPED_ROWAN_LOG: RegistrySupplier<BlockItem> = register("stripped_rowan_log") {
        BlockItem(WitcheryBlocks.STRIPPED_ROWAN_LOG.get(), Item.Properties())
    }

    val STRIPPED_ROWAN_WOOD: RegistrySupplier<BlockItem> = register("stripped_rowan_wood") {
        BlockItem(WitcheryBlocks.STRIPPED_ROWAN_WOOD.get(), Item.Properties())
    }

    val ROWAN_LEAVES: RegistrySupplier<BlockItem> = register("rowan_leaves") {
        BlockItem(WitcheryBlocks.ROWAN_LEAVES.get(), Item.Properties())
    }

    val ROWAN_BERRY_LEAVES: RegistrySupplier<BlockItem> = register("rowan_berry_leaves") {
        BlockItem(WitcheryBlocks.ROWAN_BERRY_LEAVES.get(), Item.Properties())
    }

    val ROWAN_PLANKS: RegistrySupplier<BlockItem> = register("rowan_planks") {
        BlockItem(WitcheryBlocks.ROWAN_PLANKS.get(), Item.Properties())
    }

    val ROWAN_STAIRS: RegistrySupplier<BlockItem> = register("rowan_stairs") {
        BlockItem(WitcheryBlocks.ROWAN_STAIRS.get(), Item.Properties())
    }

    val ROWAN_SLAB: RegistrySupplier<BlockItem> = register("rowan_slab") {
        BlockItem(WitcheryBlocks.ROWAN_SLAB.get(), Item.Properties())
    }

    val ROWAN_FENCE: RegistrySupplier<BlockItem> = register("rowan_fence") {
        BlockItem(WitcheryBlocks.ROWAN_FENCE.get(), Item.Properties())
    }

    val ROWAN_FENCE_GATE: RegistrySupplier<BlockItem> = register("rowan_fence_gate") {
        BlockItem(WitcheryBlocks.ROWAN_FENCE_GATE.get(), Item.Properties())
    }

    val ROWAN_DOOR: RegistrySupplier<BlockItem> = register("rowan_door") {
        BlockItem(WitcheryBlocks.ROWAN_DOOR.get(), Item.Properties())
    }

    val ROWAN_TRAPDOOR: RegistrySupplier<BlockItem> = register("rowan_trapdoor") {
        BlockItem(WitcheryBlocks.ROWAN_TRAPDOOR.get(), Item.Properties())
    }

    val ROWAN_PRESSURE_PLATE: RegistrySupplier<BlockItem> = register("rowan_pressure_plate") {
        BlockItem(WitcheryBlocks.ROWAN_PRESSURE_PLATE.get(), Item.Properties())
    }

    val ROWAN_BUTTON: RegistrySupplier<BlockItem> = register("rowan_button") {
        BlockItem(WitcheryBlocks.ROWAN_BUTTON.get(), Item.Properties())
    }

    val ROWAN_SAPLING: RegistrySupplier<BlockItem> = register("rowan_sapling") {
        BlockItem(WitcheryBlocks.ROWAN_SAPLING.get(), Item.Properties())
    }

    val ROWAN_SIGN: RegistrySupplier<SignItem> = register("rowan_sign") {
        SignItem(Item.Properties(), WitcheryBlocks.ROWAN_SIGN.get(), WitcheryBlocks.ROWAN_WALL_SIGN.get())
    }

    val ROWAN_HANGING_SIGN: RegistrySupplier<HangingSignItem> = register("rowan_hanging_sign") {
        HangingSignItem(
            WitcheryBlocks.ROWAN_HANGING_SIGN.get(),
            WitcheryBlocks.ROWAN_WALL_HANGING_SIGN.get(),
            Item.Properties()
        )
    }

    val ROWAN_BOAT: RegistrySupplier<CustomBoatItem> = register("rowan_boat") {
        CustomBoatItem(false, BoatTypeHelper.getRowanBoatType(), Item.Properties())
    }

    val ROWAN_CHEST_BOAT: RegistrySupplier<CustomBoatItem> = register("rowan_chest_boat") {
        CustomBoatItem(true, BoatTypeHelper.getRowanBoatType(), Item.Properties())
    }

    val ALDER_LOG: RegistrySupplier<BlockItem> = register("alder_log") {
        BlockItem(WitcheryBlocks.ALDER_LOG.get(), Item.Properties())
    }

    val ALDER_WOOD: RegistrySupplier<BlockItem> = register("alder_wood") {
        BlockItem(WitcheryBlocks.ALDER_WOOD.get(), Item.Properties())
    }

    val STRIPPED_ALDER_LOG: RegistrySupplier<BlockItem> = register("stripped_alder_log") {
        BlockItem(WitcheryBlocks.STRIPPED_ALDER_LOG.get(), Item.Properties())
    }

    val STRIPPED_ALDER_WOOD: RegistrySupplier<BlockItem> = register("stripped_alder_wood") {
        BlockItem(WitcheryBlocks.STRIPPED_ALDER_WOOD.get(), Item.Properties())
    }

    val ALDER_LEAVES: RegistrySupplier<BlockItem> = register("alder_leaves") {
        BlockItem(WitcheryBlocks.ALDER_LEAVES.get(), Item.Properties())
    }

    val ALDER_PLANKS: RegistrySupplier<BlockItem> = register("alder_planks") {
        BlockItem(WitcheryBlocks.ALDER_PLANKS.get(), Item.Properties())
    }

    val ALDER_STAIRS: RegistrySupplier<BlockItem> = register("alder_stairs") {
        BlockItem(WitcheryBlocks.ALDER_STAIRS.get(), Item.Properties())
    }

    val ALDER_SLAB: RegistrySupplier<BlockItem> = register("alder_slab") {
        BlockItem(WitcheryBlocks.ALDER_SLAB.get(), Item.Properties())
    }

    val ALDER_FENCE: RegistrySupplier<BlockItem> = register("alder_fence") {
        BlockItem(WitcheryBlocks.ALDER_FENCE.get(), Item.Properties())
    }

    val ALDER_FENCE_GATE: RegistrySupplier<BlockItem> = register("alder_fence_gate") {
        BlockItem(WitcheryBlocks.ALDER_FENCE_GATE.get(), Item.Properties())
    }

    val ALDER_DOOR: RegistrySupplier<BlockItem> = register("alder_door") {
        BlockItem(WitcheryBlocks.ALDER_DOOR.get(), Item.Properties())
    }

    val ALDER_TRAPDOOR: RegistrySupplier<BlockItem> = register("alder_trapdoor") {
        BlockItem(WitcheryBlocks.ALDER_TRAPDOOR.get(), Item.Properties())
    }

    val ALDER_PRESSURE_PLATE: RegistrySupplier<BlockItem> = register("alder_pressure_plate") {
        BlockItem(WitcheryBlocks.ALDER_PRESSURE_PLATE.get(), Item.Properties())
    }

    val ALDER_BUTTON: RegistrySupplier<BlockItem> = register("alder_button") {
        BlockItem(WitcheryBlocks.ALDER_BUTTON.get(), Item.Properties())
    }

    val ALDER_SAPLING: RegistrySupplier<BlockItem> = register("alder_sapling") {
        BlockItem(WitcheryBlocks.ALDER_SAPLING.get(), Item.Properties())
    }

    val ALDER_SIGN: RegistrySupplier<SignItem> = register("alder_sign") {
        SignItem(Item.Properties(), WitcheryBlocks.ALDER_SIGN.get(), WitcheryBlocks.ALDER_WALL_SIGN.get())
    }

    val ALDER_HANGING_SIGN: RegistrySupplier<HangingSignItem> = register("alder_hanging_sign") {
        HangingSignItem(
            WitcheryBlocks.ALDER_HANGING_SIGN.get(),
            WitcheryBlocks.ALDER_WALL_HANGING_SIGN.get(),
            Item.Properties()
        )
    }

    val ALDER_BOAT: RegistrySupplier<CustomBoatItem> = register("alder_boat") {
        CustomBoatItem(false, BoatTypeHelper.getAlderBoatType(), Item.Properties())
    }

    val ALDER_CHEST_BOAT: RegistrySupplier<CustomBoatItem> = register("alder_chest_boat") {
        CustomBoatItem(true, BoatTypeHelper.getAlderBoatType(), Item.Properties())
    }

    val HAWTHORN_LOG: RegistrySupplier<BlockItem> = register("hawthorn_log") {
        BlockItem(WitcheryBlocks.HAWTHORN_LOG.get(), Item.Properties())
    }

    val HAWTHORN_WOOD: RegistrySupplier<BlockItem> = register("hawthorn_wood") {
        BlockItem(WitcheryBlocks.HAWTHORN_WOOD.get(), Item.Properties())
    }

    val STRIPPED_HAWTHORN_LOG: RegistrySupplier<BlockItem> = register("stripped_hawthorn_log") {
        BlockItem(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get(), Item.Properties())
    }

    val STRIPPED_HAWTHORN_WOOD: RegistrySupplier<BlockItem> = register("stripped_hawthorn_wood") {
        BlockItem(WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get(), Item.Properties())
    }

    val HAWTHORN_LEAVES: RegistrySupplier<BlockItem> = register("hawthorn_leaves") {
        BlockItem(WitcheryBlocks.HAWTHORN_LEAVES.get(), Item.Properties())
    }

    val HAWTHORN_PLANKS: RegistrySupplier<BlockItem> = register("hawthorn_planks") {
        BlockItem(WitcheryBlocks.HAWTHORN_PLANKS.get(), Item.Properties())
    }

    val HAWTHORN_STAIRS: RegistrySupplier<BlockItem> = register("hawthorn_stairs") {
        BlockItem(WitcheryBlocks.HAWTHORN_STAIRS.get(), Item.Properties())
    }

    val HAWTHORN_SLAB: RegistrySupplier<BlockItem> = register("hawthorn_slab") {
        BlockItem(WitcheryBlocks.HAWTHORN_SLAB.get(), Item.Properties())
    }

    val HAWTHORN_FENCE: RegistrySupplier<BlockItem> = register("hawthorn_fence") {
        BlockItem(WitcheryBlocks.HAWTHORN_FENCE.get(), Item.Properties())
    }

    val HAWTHORN_FENCE_GATE: RegistrySupplier<BlockItem> = register("hawthorn_fence_gate") {
        BlockItem(WitcheryBlocks.HAWTHORN_FENCE_GATE.get(), Item.Properties())
    }

    val HAWTHORN_DOOR: RegistrySupplier<BlockItem> = register("hawthorn_door") {
        BlockItem(WitcheryBlocks.HAWTHORN_DOOR.get(), Item.Properties())
    }

    val HAWTHORN_TRAPDOOR: RegistrySupplier<BlockItem> = register("hawthorn_trapdoor") {
        BlockItem(WitcheryBlocks.HAWTHORN_TRAPDOOR.get(), Item.Properties())
    }

    val HAWTHORN_PRESSURE_PLATE: RegistrySupplier<BlockItem> = register("hawthorn_pressure_plate") {
        BlockItem(WitcheryBlocks.HAWTHORN_PRESSURE_PLATE.get(), Item.Properties())
    }

    val HAWTHORN_BUTTON: RegistrySupplier<BlockItem> = register("hawthorn_button") {
        BlockItem(WitcheryBlocks.HAWTHORN_BUTTON.get(), Item.Properties())
    }

    val HAWTHORN_SAPLING: RegistrySupplier<BlockItem> = register("hawthorn_sapling") {
        BlockItem(WitcheryBlocks.HAWTHORN_SAPLING.get(), Item.Properties())
    }

    val HAWTHORN_SIGN: RegistrySupplier<SignItem> = register("hawthorn_sign") {
        SignItem(Item.Properties(), WitcheryBlocks.HAWTHORN_SIGN.get(), WitcheryBlocks.HAWTHORN_WALL_SIGN.get())
    }

    val HAWTHORN_HANGING_SIGN: RegistrySupplier<HangingSignItem> = register("hawthorn_hanging_sign") {
        HangingSignItem(
            WitcheryBlocks.HAWTHORN_HANGING_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_HANGING_SIGN.get(),
            Item.Properties()
        )
    }

    val HAWTHORN_BOAT: RegistrySupplier<CustomBoatItem> = register("hawthorn_boat") {
        CustomBoatItem(false, BoatTypeHelper.getHawthornBoatType(), Item.Properties())
    }

    val HAWTHORN_CHEST_BOAT: RegistrySupplier<CustomBoatItem> = register("hawthorn_chest_boat") {
        CustomBoatItem(true, BoatTypeHelper.getHawthornBoatType(), Item.Properties())
    }

    // end WOOD

    // start AUGMENTS

    val IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("iron_candelabra") {
        BlockItem(WitcheryBlocks.IRON_CANDELABRA.get(), Item.Properties())
    }

    val WHITE_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("white_iron_candelabra") {
        BlockItem(WitcheryBlocks.WHITE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val ORANGE_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("orange_iron_candelabra") {
        BlockItem(WitcheryBlocks.ORANGE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val MAGENTA_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("magenta_iron_candelabra") {
        BlockItem(WitcheryBlocks.MAGENTA_IRON_CANDELABRA.get(), Item.Properties())
    }

    val LIGHT_BLUE_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("light_blue_iron_candelabra") {
        BlockItem(WitcheryBlocks.LIGHT_BLUE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val YELLOW_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("yellow_iron_candelabra") {
        BlockItem(WitcheryBlocks.YELLOW_IRON_CANDELABRA.get(), Item.Properties())
    }

    val LIME_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("lime_iron_candelabra") {
        BlockItem(WitcheryBlocks.LIME_IRON_CANDELABRA.get(), Item.Properties())
    }

    val PINK_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("pink_iron_candelabra") {
        BlockItem(WitcheryBlocks.PINK_IRON_CANDELABRA.get(), Item.Properties())
    }

    val GRAY_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("gray_iron_candelabra") {
        BlockItem(WitcheryBlocks.GRAY_IRON_CANDELABRA.get(), Item.Properties())
    }

    val LIGHT_GRAY_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("light_gray_iron_candelabra") {
        BlockItem(WitcheryBlocks.LIGHT_GRAY_IRON_CANDELABRA.get(), Item.Properties())
    }

    val CYAN_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("cyan_iron_candelabra") {
        BlockItem(WitcheryBlocks.CYAN_IRON_CANDELABRA.get(), Item.Properties())
    }

    val PURPLE_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("purple_iron_candelabra") {
        BlockItem(WitcheryBlocks.PURPLE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val BLUE_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("blue_iron_candelabra") {
        BlockItem(WitcheryBlocks.BLUE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val BROWN_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("brown_iron_candelabra") {
        BlockItem(WitcheryBlocks.BROWN_IRON_CANDELABRA.get(), Item.Properties())
    }

    val GREEN_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("green_iron_candelabra") {
        BlockItem(WitcheryBlocks.GREEN_IRON_CANDELABRA.get(), Item.Properties())
    }

    val RED_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("red_iron_candelabra") {
        BlockItem(WitcheryBlocks.RED_IRON_CANDELABRA.get(), Item.Properties())
    }

    val BLACK_IRON_CANDELABRA: RegistrySupplier<BlockItem> = register("black_iron_candelabra") {
        BlockItem(WitcheryBlocks.BLACK_IRON_CANDELABRA.get(), Item.Properties())
    }

    val ARTHANA: RegistrySupplier<ArthanaItem> = register("arthana") {
        ArthanaItem(Item.Properties().attributes(SwordItem.createAttributes(Tiers.GOLD, 1, -2.4F)))
    }

    val CHALICE: RegistrySupplier<ChaliceBlockItem> = register("chalice") {
        ChaliceBlockItem(WitcheryBlocks.CHALICE.get(), Item.Properties())
    }

    val PENTACLE: RegistrySupplier<BlockItem> = register("pentacle") {
        BlockItem(WitcheryBlocks.PENTACLE.get(), Item.Properties())
    }

    val DREAM_WEAVER: RegistrySupplier<BlockItem> = register("dream_weaver") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER.get(), Item.Properties())
    }

    val DREAM_WEAVER_OF_FLEET_FOOT: RegistrySupplier<BlockItem> = register("dream_weaver_of_fleet_foot") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER_OF_FLEET_FOOT.get(), Item.Properties())
    }

    val DREAM_WEAVER_OF_NIGHTMARES: RegistrySupplier<BlockItem> = register("dream_weaver_of_nightmares") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER_OF_NIGHTMARES.get(), Item.Properties())
    }

    val DREAM_WEAVER_OF_INTENSITY: RegistrySupplier<BlockItem> = register("dream_weaver_of_intensity") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER_OF_INTENSITY.get(), Item.Properties())
    }

    val DREAM_WEAVER_OF_FASTING: RegistrySupplier<BlockItem> = register("dream_weaver_of_fasting") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER_OF_FASTING.get(), Item.Properties())
    }

    val DREAM_WEAVER_OF_IRON_ARM: RegistrySupplier<BlockItem> = register("dream_weaver_of_iron_arm") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER_OF_IRON_ARM.get(), Item.Properties())
    }

    val DISTURBED_COTTON: RegistrySupplier<BlockItem> = register("disturbed_cotton") {
        BlockItem(WitcheryBlocks.DISTURBED_COTTON.get(), Item.Properties())
    }

    val WISPY_COTTON: RegistrySupplier<BlockItem> = register("wispy_cotton") {
        BlockItem(WitcheryBlocks.WISPY_COTTON.get(), Item.Properties())
    }

    // start Mutated Plants

    val BLOOD_POPPY = register("blood_poppy") {
        ItemNameBlockItem(WitcheryBlocks.BLOOD_POPPY.get(), Item.Properties())
    }

    // end Mutated Plats

    // start Brews

    val BREW_OF_LOVE: RegistrySupplier<BrewOfLoveItem> = register("brew_of_love") {
        BrewOfLoveItem(Color(255, 70, 180).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_INK: RegistrySupplier<BrewOfInkItem> = register("brew_of_ink") {
        BrewOfInkItem(Color(40, 40, 80).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_REVEALING: RegistrySupplier<BrewOfRevealingItem> = register("brew_of_revealing") {
        BrewOfRevealingItem(Color(175, 40, 200).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_EROSION: RegistrySupplier<BrewOfErosionItem> = register("brew_of_erosion") {
        BrewOfErosionItem(Color(80, 100, 40).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_THE_DEPTHS: RegistrySupplier<BrewOfDepthItem> = register("brew_of_the_depths") {
        BrewOfDepthItem(Color(80, 100, 240).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_WEBS: RegistrySupplier<BrewOfWebsItem> = register("brew_of_webs") {
        BrewOfWebsItem(Color(230, 230, 230).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_WASTING: RegistrySupplier<BrewOfWastingItem> = register("brew_of_wasting") {
        BrewOfWastingItem(Color(180, 50, 40).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_FROST: RegistrySupplier<BrewOfFrostItem> = register("brew_of_frost") {
        BrewOfFrostItem(Color(125, 170, 230).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_RAISING: RegistrySupplier<BrewOfRaisingItem> = register("brew_of_raising") {
        BrewOfRaisingItem(Color(150, 70, 70).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_SLEEPING: RegistrySupplier<BrewOfSleepingItem> = register("brew_of_sleeping") {
        BrewOfSleepingItem(Color(255, 90, 130).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_THE_GROTESQUE: RegistrySupplier<BrewOfTheGrotesqueItem> = register("brew_of_the_grotesque") {
        BrewOfTheGrotesqueItem(Color(170, 70, 70).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_FLOWING_SPIRIT: RegistrySupplier<BrewOfFlowingSpiritItem> = register("brew_of_flowing_spirit") {
        BrewOfFlowingSpiritItem(Color(125, 170, 230).rgb, Item.Properties().stacksTo(16))
    }

    val FLOWING_SPIRIT_BUCKET: RegistrySupplier<Item> = register(
        "flowing_spirit_bucket"
    ) {
        ArchitecturyBucketItem(
            WitcheryFluids.FLOWING_SPIRIT_STILL,
            Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
        )
    }

    val GRAVESTONE: RegistrySupplier<BlockItem> = register("gravestone") {
        BlockItem(WitcheryBlocks.GRAVESTONE.get(), Item.Properties())
    }

    val SUSPICIOUS_GRAVEYARD_DIRT: RegistrySupplier<BlockItem> = register("suspicious_graveyard_dirt") {
        BlockItem(WitcheryBlocks.SUSPICIOUS_GRAVEYARD_DIRT.get(), Item.Properties())
    }

    val TORN_PAGE: RegistrySupplier<Item> = register("torn_page") {
        TornPageItem(Item.Properties())
    }

    val QUARTZ_SPHERE: RegistrySupplier<QuartzSphereItem> = register("quartz_sphere") {
        QuartzSphereItem(Item.Properties())
    }


    val SUN_COLLECTOR: RegistrySupplier<BlockItem> = register("sunlight_collector") {
        BlockItem(WitcheryBlocks.SUNLIGHT_COLLECTOR.get(), Item.Properties().stacksTo(1))
    }

    val WOODEN_OAK_STAKE: RegistrySupplier<WoodenStakeItem> = register("wooden_oak_stake") {
        WoodenStakeItem(Item.Properties().stacksTo(1))
    }

    val WOODEN_HAWTHORN_STAKE: RegistrySupplier<WoodenStakeItem> = register("wooden_hawthorn_stake") {
        WoodenStakeItem(Item.Properties().stacksTo(1))
    }

    val WOVEN_CRUOR: RegistrySupplier<Item> = register("woven_cruor") {
        Item(Item.Properties())
    }

    val BLOOD_STAINED_WOOL: RegistrySupplier<BlockItem> = register("blood_stained_wool") {
        BlockItem(WitcheryBlocks.BLOOD_STAINED_WOOL.get(), Item.Properties())
    }

    val CANE_SWORD: RegistrySupplier<CaneSwordItem> = register("cane_sword") {
        CaneSwordItem(
            Tiers.DIAMOND, Item.Properties()
                .stacksTo(1)
                .durability(1561)
        )
    }

    val MOON_CHARM: RegistrySupplier<MoonCharmItem> = register("moon_charm") {
        PlatformUtils.moonCharmItem
    }

    val BATWING_PENDANT: RegistrySupplier<BatwingPendantItem> = register("batwing_pendant") {
        PlatformUtils.batwingPendantItem
    }

    val SUNSTONE_PENDANT: RegistrySupplier<SunstonePendantItem> = register("sunstone_pendant") {
        PlatformUtils.sunstonePendantItem
    }

    val BLOODSTONE_PENDANT: RegistrySupplier<BloodstonePendantItem> = register("bloodstone_pendant") {
        PlatformUtils.bloodstonePendantItem
    }

    val DREAMWEAVER_CHARM: RegistrySupplier<DreamweaverCharmItem> = register("dreamweaver_charm") {
        PlatformUtils.dreamweaverCharmItem
    }

    val BITING_BELT: RegistrySupplier<BitingBeltItem> = register("biting_belt") {
        PlatformUtils.bitingBeltItem
    }

    val BARK_BELT: RegistrySupplier<BarkBeltItem> = register("bark_belt") {
        PlatformUtils.barkBeltItem
    }

    val PARASITIC_LOUSE: RegistrySupplier<ParasiticLouseItem> = register("parasitic_louse") {
        ParasiticLouseItem(Item.Properties())
    }

    val GRASSPER: RegistrySupplier<BlockItem> = register("grassper") {
        BlockItem(WitcheryBlocks.GRASSPER.get(), Item.Properties())
    }

    val CRITTER_SNARE: RegistrySupplier<CritterSnareBlockItem> = register("critter_snare") {
        CritterSnareBlockItem(WitcheryBlocks.CRITTER_SNARE.get(), Item.Properties())
    }

    val WITCHES_LADDER: RegistrySupplier<MultiBlockItem> = register("witches_ladder") {
        MultiBlockItem(WitcheryBlocks.WITCHS_LADDER.get(), Item.Properties(), EffigyBlock.STRUCTURE)
    }

    val SCARECROW: RegistrySupplier<MultiBlockItem> = register("scarecrow") {
        MultiBlockItem(WitcheryBlocks.SCARECROW.get(), Item.Properties(), EffigyBlock.STRUCTURE)
    }

    val TRENT_EFFIGY: RegistrySupplier<MultiBlockItem> = register("trent_effigy") {
        MultiBlockItem(WitcheryBlocks.TRENT_EFFIGY.get(), Item.Properties(), EffigyBlock.STRUCTURE)
    }

    val MANDRAKE_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("mandrake_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.MANDRAKE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }

    val IMP_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("imp_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.IMP, Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }

    val DEMON_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("demon_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.DEMON,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val ENT_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("ent_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.ENT,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val OWL_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("owl_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.OWL,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val BANSHEE_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("banshee_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.BANSHEE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val SPECTRE_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("spectre_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.SPECTRE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val COVEN_WITCH_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("coven_witch_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.COVEN_WITCH,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val SPECTRAL_PIG_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("spectral_pig_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.SPECTRAL_PIG,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val NIGHTMARE_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("nightmare_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.NIGHTMARE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val VAMPIRE_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("vampire_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.VAMPIRE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val BABA_YAGA_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("baba_yaga_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.BABA_YAGA,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val WEREWOLF_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("werewolf_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.WEREWOLF,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val LILITH_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("lilith_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.LILITH,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val ELLE_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("elle_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.ELLE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val PARASITIC_LOUSE_SPAWN_EGG: RegistrySupplier<SpawnEggItem> = register("parasitic_louse_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.PARASITIC_LOUSE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }

}