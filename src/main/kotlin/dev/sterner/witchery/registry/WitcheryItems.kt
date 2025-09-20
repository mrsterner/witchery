package dev.sterner.witchery.registry

import com.google.common.base.Supplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.block.altar.AltarBlock
import net.minecraft.core.registries.Registries
import net.minecraft.world.food.Foods
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.HangingSignItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.SignItem
import net.minecraft.world.item.SpawnEggItem
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.Tiers
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import team.lodestar.lodestone.systems.multiblock.MultiBlockItem
import java.awt.Color

object WitcheryItems {

    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(Registries.ITEM, Witchery.MODID)
    val LANG_HELPER = mutableListOf<String>()

    fun <T : Item> register(name: String, addLang: Boolean = true, item: Supplier<T>): DeferredHolder<Item, T> {
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

    val GLINTWEED = register("glintweed") {
        BlockItem(WitcheryBlocks.GLINTWEED.get(), Item.Properties())
    }

    val EMBER_MOSS = register("ember_moss") {
        BlockItem(WitcheryBlocks.EMBER_MOSS.get(), Item.Properties())
    }

    val SPANISH_MOSS = register("spanish_moss") {
        BlockItem(WitcheryBlocks.SPANISH_MOSS.get(), Item.Properties())
    }

    val INFINITY_EGG = register("infinity_egg") {
        BlockItem(WitcheryBlocks.INFINITY_EGG.get(), Item.Properties())
    }

    val WITCHES_HAND = register("witches_hand") {
        WitchesHandItem(Item.Properties().stacksTo(1))
    }

    val BROOM = register("broom") {
        BroomItem(Item.Properties().stacksTo(1))
    }

    val SEER_STONE = register("seer_stone") {
        SeerStoneItem(Item.Properties().stacksTo(1))
    }

    //start ARMOR

    val WITCHES_HAT = register("witches_hat") {
        PlatformUtils.witchesRobes(
            WitcheryArmorMaterials.WITCHES_ROBES,
            ArmorItem.Type.HELMET,
            Item.Properties().stacksTo(1)
        )
    }

    val WITCHES_ROBES = register("witches_robes") {
        PlatformUtils.witchesRobes(
            WitcheryArmorMaterials.WITCHES_ROBES,
            ArmorItem.Type.CHESTPLATE,
            Item.Properties().stacksTo(1)
        )
    }

    val WITCHES_SLIPPERS = register("witches_slippers") {
        PlatformUtils.witchesRobes(
            WitcheryArmorMaterials.WITCHES_ROBES,
            ArmorItem.Type.BOOTS,
            Item.Properties().stacksTo(1)
        )
    }

    val BABA_YAGAS_HAT = register("baba_yagas_hat") {
        PlatformUtils.witchesRobes(
            WitcheryArmorMaterials.WITCHES_ROBES,
            ArmorItem.Type.HELMET,
            Item.Properties().stacksTo(1)
        )
    }

    val HUNTER_HELMET = register("hunter_helmet") {
        PlatformUtils.hunterArmor(WitcheryArmorMaterials.HUNTER, ArmorItem.Type.HELMET, Item.Properties().stacksTo(1))
    }

    val HUNTER_CHESTPLATE = register("hunter_chestplate") {
        PlatformUtils.hunterArmor(
            WitcheryArmorMaterials.HUNTER,
            ArmorItem.Type.CHESTPLATE,
            Item.Properties().stacksTo(1)
        )
    }

    val HUNTER_LEGGINGS = register("hunter_leggings") {
        PlatformUtils.hunterArmor(WitcheryArmorMaterials.HUNTER, ArmorItem.Type.LEGGINGS, Item.Properties().stacksTo(1))
    }

    val HUNTER_BOOTS = register("hunter_boots") {
        PlatformUtils.hunterArmor(WitcheryArmorMaterials.HUNTER, ArmorItem.Type.BOOTS, Item.Properties().stacksTo(1))
    }

    val TOP_HAT = register("top_hat") {
        PlatformUtils.dapper(
            WitcheryArmorMaterials.DAPPER,
            ArmorItem.Type.HELMET,
            Item.Properties().stacksTo(1)
        )
    }

    val DRESS_COAT = register("dress_coat") {
        PlatformUtils.dapper(
            WitcheryArmorMaterials.DAPPER,
            ArmorItem.Type.CHESTPLATE,
            Item.Properties().stacksTo(1)
        )
    }

    val TROUSERS = register("trousers") {
        PlatformUtils.dapper(WitcheryArmorMaterials.DAPPER, ArmorItem.Type.LEGGINGS, Item.Properties().stacksTo(1))
    }

    val OXFORD_BOOTS = register("oxford_boots") {
        PlatformUtils.dapper(WitcheryArmorMaterials.DAPPER, ArmorItem.Type.BOOTS, Item.Properties().stacksTo(1))
    }


    //start RESOURCES
    val MUTANDIS = register("mutandis") {
        MutandisItem(Item.Properties())
    }

    val MUTANDIS_EXTREMIS = register("mutandis_extremis") {
        MutandisItem(Item.Properties())
    }

    val MANDRAKE_SEEDS = register("mandrake_seeds") {
        ItemNameBlockItem(WitcheryBlocks.MANDRAKE_CROP.get(), Item.Properties())
    }

    val SNOWBELL_SEEDS = register("snowbell_seeds") {
        ItemNameBlockItem(WitcheryBlocks.SNOWBELL_CROP.get(), Item.Properties())
    }

    val ICY_NEEDLE= register("icy_needle") {
        IcyNeedleItem(Item.Properties())
    }

    val MANDRAKE_ROOT= register("mandrake_root") {
        Item(Item.Properties())
    }

    val BELLADONNA_SEEDS = register("belladonna_seeds") {
        ItemNameBlockItem(WitcheryBlocks.BELLADONNA_CROP.get(), Item.Properties())
    }

    val BELLADONNA_FLOWER= register("belladonna_flower") {
        Item(Item.Properties())
    }

    val WATER_ARTICHOKE_SEEDS = register("water_artichoke_seeds") {
        WaterCropBlockItem(WitcheryBlocks.WATER_ARTICHOKE_CROP.get(), Item.Properties())
    }

    val WATER_ARTICHOKE_GLOBE= register("water_artichoke_globe") {
        Item(Item.Properties())
    }

    val GARLIC = register("garlic") {
        ItemNameBlockItem(WitcheryBlocks.GARLIC_CROP.get(), Item.Properties())
    }

    val WORMWOOD_SEEDS = register("wormwood_seeds") {
        ItemNameBlockItem(WitcheryBlocks.WORMWOOD_CROP.get(), Item.Properties())
    }

    val WORMWOOD= register("wormwood") {
        Item(Item.Properties())
    }

    val WOLFSBANE_SEEDS = register("wolfsbane_seeds") {
        ItemNameBlockItem(WitcheryBlocks.WOLFSFBANE_CROP.get(), Item.Properties())
    }

    val WOLFSBANE= register("wolfsbane") {
        Item(Item.Properties())
    }

    val WOOD_ASH= register("wood_ash") {
        Item(Item.Properties())
    }

    val ROWAN_BERRIES= register("rowan_berries") {
        Item(Item.Properties().food(Foods.SWEET_BERRIES))
    }

    val BONE_NEEDLE= register("bone_needle") {
        PlatformUtils.boneNeedle
    }

    val ATTUNED_STONE= register("attuned_stone") {
        AttunedStoneItem(Item.Properties())
    }

    val DEMON_HEART= register("demon_heart") {
        ItemNameBlockItem(WitcheryBlocks.DEMON_HEART.get(), Item.Properties())
    }

    val GYPSUM= register("gypsum") {
        Item(Item.Properties())
    }

    val REFINED_EVIL= register("refined_evil") {
        Item(Item.Properties())
    }

    val WOOL_OF_BAT= register("wool_of_bat") {
        Item(Item.Properties())
    }

    val TONGUE_OF_DOG= register("tongue_of_dog") {
        Item(Item.Properties())
    }

    val TOE_OF_FROG= register("toe_of_frog") {
        Item(Item.Properties())
    }

    val OWLETS_WING= register("owlets_wing") {
        Item(Item.Properties())
    }

    val ENT_TWIG= register("ent_twig") {
        Item(Item.Properties())
    }

    val SPECTRAL_DUST= register("spectral_dust") {
        Item(Item.Properties())
    }

    val REDSTONE_SOUP= register("redstone_soup") {
        Item(Item.Properties())
    }

    val HAPPENSTANCE_OIL= register("happenstance_oil") {
        Item(Item.Properties())
    }

    val FLYING_OINTMENT= register("flying_ointment") {
        Item(Item.Properties())
    }

    val INFERNAL_ANIMUS= register("infernal_animus") {
        Item(Item.Properties())
    }

    val GHOST_OF_THE_LIGHT= register("ghost_of_the_light") {
        Item(Item.Properties())
    }

    val SOUL_OF_THE_WORLD= register("soul_of_the_world") {
        Item(Item.Properties())
    }

    val SPIRIT_OF_OTHERWHERE= register("spirit_of_otherwhere") {
        Item(Item.Properties())
    }

    val NECROMANTIC_SOULBIND= register("necromantic_soulbind") {
        Item(Item.Properties())
    }

    val GOLDEN_THREAD= register("golden_thread") {
        Item(Item.Properties())
    }

    val IMPREGNATED_FABRIC= register("impregnated_fabric") {
        Item(Item.Properties())
    }

    val MUTATING_SPRING= register("mutating_spring") {
        MutatingSpringItem(Item.Properties())
    }

    val TORMENTED_TWINE= register("tormented_twine") {
        Item(Item.Properties())
    }

    val FANCIFUL_THREAD= register("fanciful_thread") {
        Item(Item.Properties())
    }

    val WINE_GLASS= register("wine_glass") {
        WineGlassItem(Item.Properties())
    }

    val NECROMANTIC_STONE= register("necromantic_stone") {
        object : Item(Properties()) {
            override fun isFoil(stack: ItemStack): Boolean {
                return true
            }
        }
    }

    val ETERNAL_CATALYST= register("eternal_catalyst") {
        object : Item(Properties()) {
            override fun isFoil(stack: ItemStack): Boolean {
                return true
            }
        }
    }

    //end RESOURCES

    //start POPPETS

    val POPPET= register("poppet") {
        PoppetItem(Item.Properties())
    }

    val ARMOR_PROTECTION_POPPET= register("armor_protection_poppet") {
        PoppetItem(Item.Properties().durability(4))
    }

    val DEATH_PROTECTION_POPPET= register("death_protection_poppet") {
        PoppetItem(Item.Properties().durability(1))
    }

    val HUNGER_PROTECTION_POPPET= register("hunger_protection_poppet") {
        PoppetItem(Item.Properties().durability(4))
    }

    val VAMPIRIC_POPPET= register("vampiric_poppet") {
        PoppetItem(Item.Properties().durability(128))
    }

    val VOODOO_POPPET= register("voodoo_poppet") {
        VoodooPoppetItem(Item.Properties().durability(1024))
    }

    val VOODOO_PROTECTION_POPPET= register("voodoo_protection_poppet") {
        PoppetItem(Item.Properties().durability(1))
    }

    //end POPPETS

    //start JARS

    val CLAY_JAR= register("clay_jar") {
        Item(Item.Properties())
    }

    val JAR= register("jar") {
        Item(Item.Properties())
    }

    val BREATH_OF_THE_GODDESS= register("breath_of_the_goddess") {
        Item(Item.Properties())
    }

    val WHIFF_OF_MAGIC= register("whiff_of_magic") {
        Item(Item.Properties())
    }

    val FOUL_FUME= register("foul_fume") {
        Item(Item.Properties())
    }

    val TEAR_OF_THE_GODDESS= register("tear_of_the_goddess") {
        Item(Item.Properties())
    }

    val OIL_OF_VITRIOL= register("oil_of_vitriol") {
        Item(Item.Properties())
    }

    val PHANTOM_VAPOR= register("phantom_vapor") {
        Item(Item.Properties())
    }

    val EXHALE_OF_THE_HORNED_ONE= register("exhale_of_the_horned_one") {
        Item(Item.Properties())
    }

    val HINT_OF_REBIRTH= register("hint_of_rebirth") {
        Item(Item.Properties())
    }

    val REEK_OF_MISFORTUNE= register("reek_of_misfortune") {
        Item(Item.Properties())
    }

    val ODOR_OF_PURITY= register("odor_of_purity") {
        Item(Item.Properties())
    }

    val DROP_OF_LUCK= register("drop_of_luck") {
        Item(Item.Properties())
    }

    val ENDER_DEW= register("ender_dew") {
        Item(Item.Properties())
    }

    val DEMONS_BLOOD= register("demons_blood") {
        Item(Item.Properties())
    }

    val MELLIFLUOUS_HUNGER= register("mellifluous_hunger") {
        Item(Item.Properties())
    }

    val CONDENSED_FEAR= register("condensed_fear") {
        Item(Item.Properties())
    }

    val FOCUSED_WILL= register("focused_will") {
        Item(Item.Properties())
    }

    //end JARS

    //start CHALK

    val RITUAL_CHALK = register("ritual_chalk") {
        ChalkItem(WitcheryBlocks.RITUAL_CHALK_BLOCK.get(), Item.Properties())
    }

    val GOLDEN_CHALK = register("golden_chalk") {
        ChalkItem(WitcheryBlocks.GOLDEN_CHALK_BLOCK.get(), Item.Properties())
    }

    val INFERNAL_CHALK = register("infernal_chalk") {
        ChalkItem(WitcheryBlocks.INFERNAL_CHALK_BLOCK.get(), Item.Properties())
    }

    val OTHERWHERE_CHALK = register("otherwhere_chalk") {
        ChalkItem(WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get(), Item.Properties())
    }
    //end CHALK

    val GUIDEBOOK = register("guidebook") {
        GuideBookItem(Item.Properties())
    }

    val WITCHERY_POTION = register("witchery_potion") {
        WitcheryPotionItem(Item.Properties())
    }

    val DEEPSLATE_ALTAR_BLOCK = register("deepslate_altar_block") {
        BlockItem(WitcheryBlocks.DEEPLSTAE_ALTAR_BLOCK.get(), Item.Properties())
    }

    val ALTAR = register("altar") {
        MultiBlockItem(WitcheryBlocks.ALTAR.get(), Item.Properties(), AltarBlock.STRUCTURE)
    }

    val CAULDRON = register("cauldron") {
        MultiBlockItem(WitcheryBlocks.CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val COPPER_CAULDRON = register("copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_COPPER_CAULDRON = register("waxed_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.WAXED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val EXPOSED_COPPER_CAULDRON = register("exposed_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.EXPOSED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_EXPOSED_COPPER_CAULDRON =
        register("waxed_exposed_copper_cauldron") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_EXPOSED_COPPER_CAULDRON.get(),
                Item.Properties(),
                CauldronBlock.STRUCTURE
            )
        }

    val WEATHERED_COPPER_CAULDRON = register("weathered_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.WEATHERED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_WEATHERED_COPPER_CAULDRON =
        register("waxed_weathered_copper_cauldron") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_WEATHERED_COPPER_CAULDRON.get(),
                Item.Properties(),
                CauldronBlock.STRUCTURE
            )
        }

    val OXIDIZED_COPPER_CAULDRON = register("oxidized_copper_cauldron") {
        MultiBlockItem(WitcheryBlocks.OXIDIZED_COPPER_CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val WAXED_OXIDIZED_COPPER_CAULDRON =
        register("waxed_oxidized_copper_cauldron") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_OXIDIZED_COPPER_CAULDRON.get(),
                Item.Properties(),
                CauldronBlock.STRUCTURE
            )
        }

    val IRON_WITCHES_OVEN_FUME_EXTENSION =
        register("iron_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val COPPER_WITCHES_OVEN_FUME_EXTENSION =
        register("copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION =
        register("exposed_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION =
        register("weathered_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION =
        register("oxidized_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION =
        register("waxed_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION =
        register("waxed_exposed_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION =
        register("waxed_weathered_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION =
        register("waxed_oxidized_copper_witches_oven_fume_extension") {
            MultiBlockItem(
                WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
                Item.Properties(),
                OvenFumeExtensionBlock.STRUCTURE
            )
        }

    val IRON_WITCHES_OVEN = register("iron_witches_oven") {
        BlockItem(WitcheryBlocks.IRON_WITCHES_OVEN.get(), Item.Properties())
    }

    val COPPER_WITCHES_OVEN = register("copper_witches_oven") {
        BlockItem(WitcheryBlocks.COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_COPPER_WITCHES_OVEN = register("waxed_copper_witches_oven") {
        BlockItem(WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val EXPOSED_COPPER_WITCHES_OVEN = register("exposed_copper_witches_oven") {
        BlockItem(WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_EXPOSED_COPPER_WITCHES_OVEN =
        register("waxed_exposed_copper_witches_oven") {
            BlockItem(WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get(), Item.Properties())
        }

    val WEATHERED_COPPER_WITCHES_OVEN = register("weathered_copper_witches_oven") {
        BlockItem(WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_WEATHERED_COPPER_WITCHES_OVEN =
        register("waxed_weathered_copper_witches_oven") {
            BlockItem(WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get(), Item.Properties())
        }

    val OXIDIZED_COPPER_WITCHES_OVEN = register("oxidized_copper_witches_oven") {
        BlockItem(WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAXED_OXIDIZED_COPPER_WITCHES_OVEN =
        register("waxed_oxidized_copper_witches_oven") {
            BlockItem(WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get(), Item.Properties())
        }

    val WAYSTONE = register("waystone") {
        WaystoneItem(Item.Properties().stacksTo(1).rarity(Rarity.COMMON))
    }

    val TAGLOCK = register("taglock") {
        TaglockItem(Item.Properties().stacksTo(1).rarity(Rarity.COMMON).craftRemainder(Items.GLASS_BOTTLE))
    }

    val DISTILLERY = register("distillery") {
        MultiBlockItem(WitcheryBlocks.DISTILLERY.get(), Item.Properties(), DistilleryBlock.STRUCTURE)
    }

    val BEAR_TRAP = register("bear_trap") {
        BlockItem(WitcheryBlocks.BEAR_TRAP.get(), Item.Properties())
    }

    val SPINNING_WHEEL = register("spinning_wheel") {
        BlockItem(WitcheryBlocks.SPINNING_WHEEL.get(), Item.Properties())
    }

    val BRAZIER = register("brazier") {
        BlockItem(WitcheryBlocks.BRAZIER.get(), Item.Properties())
    }

    val PHYLACTERY = register("phylactery") {
        BlockItem(WitcheryBlocks.PHYLACTERY.get(), Item.Properties())
    }

    val CENSER = register("censer") {
        CenserBlockItem(WitcheryBlocks.CENSER.get(), Item.Properties(), true)
    }

    val CENSER_LONG = register("censer_long") {
        CenserBlockItem(WitcheryBlocks.CENSER.get(), Item.Properties(), false)
    }

    val WEREWOLF_ALTAR = register("werewolf_altar") {
        MultiBlockItem(WitcheryBlocks.WEREWOLF_ALTAR.get(), Item.Properties(), WerewolfAltarBlock.STRUCTURE)
    }

    val BLOOD_CRUCIBLE = register("blood_crucible") {
        BlockItem(WitcheryBlocks.BLOOD_CRUCIBLE.get(), Item.Properties())
    }

    val COFFIN = register("coffin") {
        BlockItem(WitcheryBlocks.COFFIN.get(), Item.Properties())
    }

    // start WOOD

    val ROWAN_LOG = register("rowan_log") {
        BlockItem(WitcheryBlocks.ROWAN_LOG.get(), Item.Properties())
    }

    val ROWAN_WOOD = register("rowan_wood") {
        BlockItem(WitcheryBlocks.ROWAN_WOOD.get(), Item.Properties())
    }

    val STRIPPED_ROWAN_LOG = register("stripped_rowan_log") {
        BlockItem(WitcheryBlocks.STRIPPED_ROWAN_LOG.get(), Item.Properties())
    }

    val STRIPPED_ROWAN_WOOD = register("stripped_rowan_wood") {
        BlockItem(WitcheryBlocks.STRIPPED_ROWAN_WOOD.get(), Item.Properties())
    }

    val ROWAN_LEAVES = register("rowan_leaves") {
        BlockItem(WitcheryBlocks.ROWAN_LEAVES.get(), Item.Properties())
    }

    val ROWAN_BERRY_LEAVES = register("rowan_berry_leaves") {
        BlockItem(WitcheryBlocks.ROWAN_BERRY_LEAVES.get(), Item.Properties())
    }

    val ROWAN_PLANKS = register("rowan_planks") {
        BlockItem(WitcheryBlocks.ROWAN_PLANKS.get(), Item.Properties())
    }

    val ROWAN_STAIRS = register("rowan_stairs") {
        BlockItem(WitcheryBlocks.ROWAN_STAIRS.get(), Item.Properties())
    }

    val ROWAN_SLAB = register("rowan_slab") {
        BlockItem(WitcheryBlocks.ROWAN_SLAB.get(), Item.Properties())
    }

    val ROWAN_FENCE = register("rowan_fence") {
        BlockItem(WitcheryBlocks.ROWAN_FENCE.get(), Item.Properties())
    }

    val ROWAN_FENCE_GATE = register("rowan_fence_gate") {
        BlockItem(WitcheryBlocks.ROWAN_FENCE_GATE.get(), Item.Properties())
    }

    val ROWAN_DOOR = register("rowan_door") {
        BlockItem(WitcheryBlocks.ROWAN_DOOR.get(), Item.Properties())
    }

    val ROWAN_TRAPDOOR = register("rowan_trapdoor") {
        BlockItem(WitcheryBlocks.ROWAN_TRAPDOOR.get(), Item.Properties())
    }

    val ROWAN_PRESSURE_PLATE = register("rowan_pressure_plate") {
        BlockItem(WitcheryBlocks.ROWAN_PRESSURE_PLATE.get(), Item.Properties())
    }

    val ROWAN_BUTTON = register("rowan_button") {
        BlockItem(WitcheryBlocks.ROWAN_BUTTON.get(), Item.Properties())
    }

    val ROWAN_SAPLING = register("rowan_sapling") {
        BlockItem(WitcheryBlocks.ROWAN_SAPLING.get(), Item.Properties())
    }

    val ROWAN_SIGN = register("rowan_sign") {
        SignItem(Item.Properties(), WitcheryBlocks.ROWAN_SIGN.get(), WitcheryBlocks.ROWAN_WALL_SIGN.get())
    }

    val ROWAN_HANGING_SIGN = register("rowan_hanging_sign") {
        HangingSignItem(
            WitcheryBlocks.ROWAN_HANGING_SIGN.get(),
            WitcheryBlocks.ROWAN_WALL_HANGING_SIGN.get(),
            Item.Properties()
        )
    }

    val ROWAN_BOAT = register("rowan_boat") {
        CustomBoatItem(false, BoatTypeHelper.getRowanBoatType(), Item.Properties())
    }

    val ROWAN_CHEST_BOAT = register("rowan_chest_boat") {
        CustomBoatItem(true, BoatTypeHelper.getRowanBoatType(), Item.Properties())
    }

    val ALDER_LOG = register("alder_log") {
        BlockItem(WitcheryBlocks.ALDER_LOG.get(), Item.Properties())
    }

    val ALDER_WOOD = register("alder_wood") {
        BlockItem(WitcheryBlocks.ALDER_WOOD.get(), Item.Properties())
    }

    val STRIPPED_ALDER_LOG = register("stripped_alder_log") {
        BlockItem(WitcheryBlocks.STRIPPED_ALDER_LOG.get(), Item.Properties())
    }

    val STRIPPED_ALDER_WOOD = register("stripped_alder_wood") {
        BlockItem(WitcheryBlocks.STRIPPED_ALDER_WOOD.get(), Item.Properties())
    }

    val ALDER_LEAVES = register("alder_leaves") {
        BlockItem(WitcheryBlocks.ALDER_LEAVES.get(), Item.Properties())
    }

    val ALDER_PLANKS = register("alder_planks") {
        BlockItem(WitcheryBlocks.ALDER_PLANKS.get(), Item.Properties())
    }

    val ALDER_STAIRS = register("alder_stairs") {
        BlockItem(WitcheryBlocks.ALDER_STAIRS.get(), Item.Properties())
    }

    val ALDER_SLAB = register("alder_slab") {
        BlockItem(WitcheryBlocks.ALDER_SLAB.get(), Item.Properties())
    }

    val ALDER_FENCE = register("alder_fence") {
        BlockItem(WitcheryBlocks.ALDER_FENCE.get(), Item.Properties())
    }

    val ALDER_FENCE_GATE = register("alder_fence_gate") {
        BlockItem(WitcheryBlocks.ALDER_FENCE_GATE.get(), Item.Properties())
    }

    val ALDER_DOOR = register("alder_door") {
        BlockItem(WitcheryBlocks.ALDER_DOOR.get(), Item.Properties())
    }

    val ALDER_TRAPDOOR = register("alder_trapdoor") {
        BlockItem(WitcheryBlocks.ALDER_TRAPDOOR.get(), Item.Properties())
    }

    val ALDER_PRESSURE_PLATE = register("alder_pressure_plate") {
        BlockItem(WitcheryBlocks.ALDER_PRESSURE_PLATE.get(), Item.Properties())
    }

    val ALDER_BUTTON = register("alder_button") {
        BlockItem(WitcheryBlocks.ALDER_BUTTON.get(), Item.Properties())
    }

    val ALDER_SAPLING = register("alder_sapling") {
        BlockItem(WitcheryBlocks.ALDER_SAPLING.get(), Item.Properties())
    }

    val ALDER_SIGN = register("alder_sign") {
        SignItem(Item.Properties(), WitcheryBlocks.ALDER_SIGN.get(), WitcheryBlocks.ALDER_WALL_SIGN.get())
    }

    val ALDER_HANGING_SIGN = register("alder_hanging_sign") {
        HangingSignItem(
            WitcheryBlocks.ALDER_HANGING_SIGN.get(),
            WitcheryBlocks.ALDER_WALL_HANGING_SIGN.get(),
            Item.Properties()
        )
    }

    val ALDER_BOAT = register("alder_boat") {
        CustomBoatItem(false, BoatTypeHelper.getAlderBoatType(), Item.Properties())
    }

    val ALDER_CHEST_BOAT = register("alder_chest_boat") {
        CustomBoatItem(true, BoatTypeHelper.getAlderBoatType(), Item.Properties())
    }

    val HAWTHORN_LOG = register("hawthorn_log") {
        BlockItem(WitcheryBlocks.HAWTHORN_LOG.get(), Item.Properties())
    }

    val HAWTHORN_WOOD = register("hawthorn_wood") {
        BlockItem(WitcheryBlocks.HAWTHORN_WOOD.get(), Item.Properties())
    }

    val STRIPPED_HAWTHORN_LOG = register("stripped_hawthorn_log") {
        BlockItem(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get(), Item.Properties())
    }

    val STRIPPED_HAWTHORN_WOOD = register("stripped_hawthorn_wood") {
        BlockItem(WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get(), Item.Properties())
    }

    val HAWTHORN_LEAVES = register("hawthorn_leaves") {
        BlockItem(WitcheryBlocks.HAWTHORN_LEAVES.get(), Item.Properties())
    }

    val HAWTHORN_PLANKS = register("hawthorn_planks") {
        BlockItem(WitcheryBlocks.HAWTHORN_PLANKS.get(), Item.Properties())
    }

    val HAWTHORN_STAIRS = register("hawthorn_stairs") {
        BlockItem(WitcheryBlocks.HAWTHORN_STAIRS.get(), Item.Properties())
    }

    val HAWTHORN_SLAB = register("hawthorn_slab") {
        BlockItem(WitcheryBlocks.HAWTHORN_SLAB.get(), Item.Properties())
    }

    val HAWTHORN_FENCE = register("hawthorn_fence") {
        BlockItem(WitcheryBlocks.HAWTHORN_FENCE.get(), Item.Properties())
    }

    val HAWTHORN_FENCE_GATE = register("hawthorn_fence_gate") {
        BlockItem(WitcheryBlocks.HAWTHORN_FENCE_GATE.get(), Item.Properties())
    }

    val HAWTHORN_DOOR = register("hawthorn_door") {
        BlockItem(WitcheryBlocks.HAWTHORN_DOOR.get(), Item.Properties())
    }

    val HAWTHORN_TRAPDOOR = register("hawthorn_trapdoor") {
        BlockItem(WitcheryBlocks.HAWTHORN_TRAPDOOR.get(), Item.Properties())
    }

    val HAWTHORN_PRESSURE_PLATE = register("hawthorn_pressure_plate") {
        BlockItem(WitcheryBlocks.HAWTHORN_PRESSURE_PLATE.get(), Item.Properties())
    }

    val HAWTHORN_BUTTON = register("hawthorn_button") {
        BlockItem(WitcheryBlocks.HAWTHORN_BUTTON.get(), Item.Properties())
    }

    val HAWTHORN_SAPLING = register("hawthorn_sapling") {
        BlockItem(WitcheryBlocks.HAWTHORN_SAPLING.get(), Item.Properties())
    }

    val HAWTHORN_SIGN = register("hawthorn_sign") {
        SignItem(Item.Properties(), WitcheryBlocks.HAWTHORN_SIGN.get(), WitcheryBlocks.HAWTHORN_WALL_SIGN.get())
    }

    val HAWTHORN_HANGING_SIGN = register("hawthorn_hanging_sign") {
        HangingSignItem(
            WitcheryBlocks.HAWTHORN_HANGING_SIGN.get(),
            WitcheryBlocks.HAWTHORN_WALL_HANGING_SIGN.get(),
            Item.Properties()
        )
    }

    val HAWTHORN_BOAT = register("hawthorn_boat") {
        CustomBoatItem(false, BoatTypeHelper.getHawthornBoatType(), Item.Properties())
    }

    val HAWTHORN_CHEST_BOAT = register("hawthorn_chest_boat") {
        CustomBoatItem(true, BoatTypeHelper.getHawthornBoatType(), Item.Properties())
    }

    // end WOOD

    // start AUGMENTS

    val IRON_CANDELABRA = register("iron_candelabra") {
        BlockItem(WitcheryBlocks.IRON_CANDELABRA.get(), Item.Properties())
    }

    val WHITE_IRON_CANDELABRA = register("white_iron_candelabra") {
        BlockItem(WitcheryBlocks.WHITE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val ORANGE_IRON_CANDELABRA = register("orange_iron_candelabra") {
        BlockItem(WitcheryBlocks.ORANGE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val MAGENTA_IRON_CANDELABRA = register("magenta_iron_candelabra") {
        BlockItem(WitcheryBlocks.MAGENTA_IRON_CANDELABRA.get(), Item.Properties())
    }

    val LIGHT_BLUE_IRON_CANDELABRA = register("light_blue_iron_candelabra") {
        BlockItem(WitcheryBlocks.LIGHT_BLUE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val YELLOW_IRON_CANDELABRA = register("yellow_iron_candelabra") {
        BlockItem(WitcheryBlocks.YELLOW_IRON_CANDELABRA.get(), Item.Properties())
    }

    val LIME_IRON_CANDELABRA = register("lime_iron_candelabra") {
        BlockItem(WitcheryBlocks.LIME_IRON_CANDELABRA.get(), Item.Properties())
    }

    val PINK_IRON_CANDELABRA = register("pink_iron_candelabra") {
        BlockItem(WitcheryBlocks.PINK_IRON_CANDELABRA.get(), Item.Properties())
    }

    val GRAY_IRON_CANDELABRA = register("gray_iron_candelabra") {
        BlockItem(WitcheryBlocks.GRAY_IRON_CANDELABRA.get(), Item.Properties())
    }

    val LIGHT_GRAY_IRON_CANDELABRA = register("light_gray_iron_candelabra") {
        BlockItem(WitcheryBlocks.LIGHT_GRAY_IRON_CANDELABRA.get(), Item.Properties())
    }

    val CYAN_IRON_CANDELABRA = register("cyan_iron_candelabra") {
        BlockItem(WitcheryBlocks.CYAN_IRON_CANDELABRA.get(), Item.Properties())
    }

    val PURPLE_IRON_CANDELABRA = register("purple_iron_candelabra") {
        BlockItem(WitcheryBlocks.PURPLE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val BLUE_IRON_CANDELABRA = register("blue_iron_candelabra") {
        BlockItem(WitcheryBlocks.BLUE_IRON_CANDELABRA.get(), Item.Properties())
    }

    val BROWN_IRON_CANDELABRA = register("brown_iron_candelabra") {
        BlockItem(WitcheryBlocks.BROWN_IRON_CANDELABRA.get(), Item.Properties())
    }

    val GREEN_IRON_CANDELABRA = register("green_iron_candelabra") {
        BlockItem(WitcheryBlocks.GREEN_IRON_CANDELABRA.get(), Item.Properties())
    }

    val RED_IRON_CANDELABRA = register("red_iron_candelabra") {
        BlockItem(WitcheryBlocks.RED_IRON_CANDELABRA.get(), Item.Properties())
    }

    val BLACK_IRON_CANDELABRA = register("black_iron_candelabra") {
        BlockItem(WitcheryBlocks.BLACK_IRON_CANDELABRA.get(), Item.Properties())
    }

    val ARTHANA = register("arthana") {
        ArthanaItem(Item.Properties().attributes(SwordItem.createAttributes(Tiers.GOLD, 1, -2.4F)))
    }

    val CHALICE = register("chalice") {
        ChaliceBlockItem(WitcheryBlocks.CHALICE.get(), Item.Properties())
    }

    val PENTACLE = register("pentacle") {
        BlockItem(WitcheryBlocks.PENTACLE.get(), Item.Properties())
    }

    val DREAM_WEAVER = register("dream_weaver") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER.get(), Item.Properties())
    }

    val DREAM_WEAVER_OF_FLEET_FOOT = register("dream_weaver_of_fleet_foot") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER_OF_FLEET_FOOT.get(), Item.Properties())
    }

    val DREAM_WEAVER_OF_NIGHTMARES = register("dream_weaver_of_nightmares") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER_OF_NIGHTMARES.get(), Item.Properties())
    }

    val DREAM_WEAVER_OF_INTENSITY = register("dream_weaver_of_intensity") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER_OF_INTENSITY.get(), Item.Properties())
    }

    val DREAM_WEAVER_OF_FASTING = register("dream_weaver_of_fasting") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER_OF_FASTING.get(), Item.Properties())
    }

    val DREAM_WEAVER_OF_IRON_ARM = register("dream_weaver_of_iron_arm") {
        BlockItem(WitcheryBlocks.DREAM_WEAVER_OF_IRON_ARM.get(), Item.Properties())
    }

    val DISTURBED_COTTON = register("disturbed_cotton") {
        BlockItem(WitcheryBlocks.DISTURBED_COTTON.get(), Item.Properties())
    }

    val WISPY_COTTON = register("wispy_cotton") {
        BlockItem(WitcheryBlocks.WISPY_COTTON.get(), Item.Properties())
    }

    // start Mutated Plants

    val BLOOD_POPPY = register("blood_poppy") {
        ItemNameBlockItem(WitcheryBlocks.BLOOD_POPPY.get(), Item.Properties())
    }

    // end Mutated Plats

    // start Brews

    val BREW_OF_LOVE = register("brew_of_love") {
        BrewOfLoveItem(Color(255, 70, 180).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_INK = register("brew_of_ink") {
        BrewOfInkItem(Color(40, 40, 80).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_REVEALING = register("brew_of_revealing") {
        BrewOfRevealingItem(Color(175, 40, 200).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_EROSION = register("brew_of_erosion") {
        BrewOfErosionItem(Color(80, 100, 40).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_THE_DEPTHS = register("brew_of_the_depths") {
        BrewOfDepthItem(Color(80, 100, 240).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_WEBS = register("brew_of_webs") {
        BrewOfWebsItem(Color(230, 230, 230).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_WASTING = register("brew_of_wasting") {
        BrewOfWastingItem(Color(180, 50, 40).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_FROST = register("brew_of_frost") {
        BrewOfFrostItem(Color(125, 170, 230).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_RAISING = register("brew_of_raising") {
        BrewOfRaisingItem(Color(150, 70, 70).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_SLEEPING = register("brew_of_sleeping") {
        BrewOfSleepingItem(Color(255, 90, 130).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_OF_THE_GROTESQUE = register("brew_of_the_grotesque") {
        BrewOfTheGrotesqueItem(Color(170, 70, 70).rgb, Item.Properties().stacksTo(16))
    }

    val BREW_FLOWING_SPIRIT = register("brew_of_flowing_spirit") {
        BrewOfFlowingSpiritItem(Color(125, 170, 230).rgb, Item.Properties().stacksTo(16))
    }

    val FLOWING_SPIRIT_BUCKET= register(
        "flowing_spirit_bucket"
    ) {
        ArchitecturyBucketItem(
            WitcheryFluids.FLOWING_SPIRIT_STILL,
            Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)
        )
    }

    val GRAVESTONE = register("gravestone") {
        BlockItem(WitcheryBlocks.GRAVESTONE.get(), Item.Properties())
    }

    val SUSPICIOUS_GRAVEYARD_DIRT = register("suspicious_graveyard_dirt") {
        BlockItem(WitcheryBlocks.SUSPICIOUS_GRAVEYARD_DIRT.get(), Item.Properties())
    }

    val TORN_PAGE= register("torn_page") {
        TornPageItem(Item.Properties())
    }

    val QUARTZ_SPHERE = register("quartz_sphere") {
        QuartzSphereItem(Item.Properties())
    }


    val SUN_COLLECTOR = register("sunlight_collector") {
        BlockItem(WitcheryBlocks.SUNLIGHT_COLLECTOR.get(), Item.Properties().stacksTo(1))
    }

    val WOODEN_OAK_STAKE = register("wooden_oak_stake") {
        WoodenStakeItem(Item.Properties().stacksTo(1))
    }

    val WOODEN_HAWTHORN_STAKE = register("wooden_hawthorn_stake") {
        WoodenStakeItem(Item.Properties().stacksTo(1))
    }

    val WOVEN_CRUOR= register("woven_cruor") {
        Item(Item.Properties())
    }

    val BLOOD_STAINED_WOOL = register("blood_stained_wool") {
        BlockItem(WitcheryBlocks.BLOOD_STAINED_WOOL.get(), Item.Properties())
    }

    val BLOOD_STAINED_HAY = register("blood_stained_hay") {
        BlockItem(WitcheryBlocks.BLOOD_STAINED_HAY.get(), Item.Properties())
    }

    val CANE_SWORD = register("cane_sword") {
        CaneSwordItem(
            Tiers.DIAMOND, Item.Properties()
                .stacksTo(1)
                .durability(1561)
        )
    }

    val MOON_CHARM = register("moon_charm") {
        PlatformUtils.moonCharmItem
    }

    val BATWING_PENDANT = register("batwing_pendant") {
        PlatformUtils.batwingPendantItem
    }

    val SUNSTONE_PENDANT = register("sunstone_pendant") {
        PlatformUtils.sunstonePendantItem
    }

    val BLOODSTONE_PENDANT = register("bloodstone_pendant") {
        PlatformUtils.bloodstonePendantItem
    }

    val DREAMWEAVER_CHARM = register("dreamweaver_charm") {
        PlatformUtils.dreamweaverCharmItem
    }

    val BITING_BELT = register("biting_belt") {
        PlatformUtils.bitingBeltItem
    }

    val BARK_BELT = register("bark_belt") {
        PlatformUtils.barkBeltItem
    }

    val PARASITIC_LOUSE = register("parasitic_louse") {
        ParasiticLouseItem(Item.Properties())
    }

    val GRASSPER = register("grassper") {
        BlockItem(WitcheryBlocks.GRASSPER.get(), Item.Properties())
    }

    val CRITTER_SNARE = register("critter_snare") {
        CritterSnareBlockItem(WitcheryBlocks.CRITTER_SNARE.get(), Item.Properties())
    }

    val WITCHES_LADDER = register("witches_ladder") {
        MultiBlockItem(WitcheryBlocks.WITCHS_LADDER.get(), Item.Properties(), EffigyBlock.STRUCTURE)
    }

    val SCARECROW = register("scarecrow") {
        MultiBlockItem(WitcheryBlocks.SCARECROW.get(), Item.Properties(), EffigyBlock.STRUCTURE)
    }

    val CLAY_EFFIGY = register("clay_effigy") {
        BlockItem(WitcheryBlocks.CLAY_EFFIGY.get(), Item.Properties())
    }

    val MANDRAKE_SPAWN_EGG = register("mandrake_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.MANDRAKE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }

    val IMP_SPAWN_EGG = register("imp_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.IMP, Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }

    val DEMON_SPAWN_EGG = register("demon_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.DEMON,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val ENT_SPAWN_EGG = register("ent_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.ENT,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val OWL_SPAWN_EGG = register("owl_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.OWL,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val BANSHEE_SPAWN_EGG = register("banshee_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.BANSHEE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val SPECTRE_SPAWN_EGG = register("spectre_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.SPECTRE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val COVEN_WITCH_SPAWN_EGG = register("coven_witch_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.COVEN_WITCH,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val SPECTRAL_PIG_SPAWN_EGG = register("spectral_pig_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.SPECTRAL_PIG,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val NIGHTMARE_SPAWN_EGG = register("nightmare_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.NIGHTMARE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val VAMPIRE_SPAWN_EGG = register("vampire_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.VAMPIRE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val BABA_YAGA_SPAWN_EGG = register("baba_yaga_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.BABA_YAGA,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val WEREWOLF_SPAWN_EGG = register("werewolf_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.WEREWOLF,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val LILITH_SPAWN_EGG = register("lilith_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.LILITH,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val ELLE_SPAWN_EGG = register("elle_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.ELLE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
    val PARASITIC_LOUSE_SPAWN_EGG = register("parasitic_louse_spawn_egg") {
        ArchitecturySpawnEggItem(WitcheryEntityTypes.PARASITIC_LOUSE,Color.WHITE.rgb, Color.WHITE.rgb, Item.Properties())
    }
}