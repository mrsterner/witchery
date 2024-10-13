package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.multiblock.MultiBlockItem
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.cauldron.CauldronBlock
import dev.sterner.witchery.block.oven.OvenFumeExtensionBlock
import dev.sterner.witchery.item.*
import net.minecraft.core.registries.Registries
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

    val BONE_NEEDLE: RegistrySupplier<Item> = ITEMS.register("bone_needle") {
        Item(Item.Properties())
    }

    val DEMON_HEART: RegistrySupplier<Item> = ITEMS.register("demon_heart") {
        Item(Item.Properties())
    }

    val GYPSUM: RegistrySupplier<Item> = ITEMS.register("gypsum") {
        Item(Item.Properties())
    }
    //end RESOURCES

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

    //end JARS

    //start CHALK
    val RITUAL_CHALK: RegistrySupplier<ChalkItem> = ITEMS.register("ritual_chalk") {
        ChalkItem(Item.Properties())
    }

    val GOLDEN_CHALK: RegistrySupplier<ChalkItem> = ITEMS.register("golden_chalk") {
        ChalkItem(Item.Properties())
    }

    val INFERNAL_CHALK: RegistrySupplier<ChalkItem> = ITEMS.register("infernal_chalk") {
        ChalkItem(Item.Properties())
    }

    val OTHERWHERE_CHALK: RegistrySupplier<ChalkItem> = ITEMS.register("otherwhere_chalk") {
        ChalkItem(Item.Properties())
    }
    //end CHALK

    val GUIDEBOOK: RegistrySupplier<GuideBookItem> = ITEMS.register("guidebook") {
        GuideBookItem(Item.Properties())
    }

    val ALTAR: RegistrySupplier<MultiBlockItem> = ITEMS.register("altar") {
        MultiBlockItem(WitcheryBlocks.ALTAR.get(), Item.Properties(), AltarBlock.STRUCTURE)
    }

    val CAULDRON: RegistrySupplier<MultiBlockItem> = ITEMS.register("cauldron") {
        MultiBlockItem(WitcheryBlocks.CAULDRON.get(), Item.Properties(), CauldronBlock.STRUCTURE)
    }

    val IRON_WITCHES_OVEN_FUME_EXTENSION: RegistrySupplier<MultiBlockItem> = ITEMS.register("iron_witches_oven_fume_extension") {
        MultiBlockItem(WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get(), Item.Properties(), OvenFumeExtensionBlock.STRUCTURE)
    }

    val IRON_WITCHES_OVEN: RegistrySupplier<BlockItem> = ITEMS.register("iron_witches_oven") {
        BlockItem(WitcheryBlocks.IRON_WITCHES_OVEN.get(), Item.Properties())
    }

    val COPPER_WITCHES_OVEN: RegistrySupplier<BlockItem> = ITEMS.register("copper_witches_oven") {
        BlockItem(WitcheryBlocks.COPPER_WITCHES_OVEN.get(), Item.Properties())
    }

    val WAYSTONE = ITEMS.register("waystone") {
        WaystoneItem(Item.Properties().stacksTo(1).rarity(Rarity.COMMON))
    }
}