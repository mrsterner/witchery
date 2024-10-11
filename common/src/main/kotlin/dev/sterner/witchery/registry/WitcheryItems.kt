package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.multiblock.MultiBlockItem
import dev.sterner.witchery.block.altar.AltarBlock
import dev.sterner.witchery.block.cauldron.CauldronBlock
import dev.sterner.witchery.item.ChalkItem
import dev.sterner.witchery.item.GuideBookItem
import dev.sterner.witchery.item.MutandisItem
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemNameBlockItem
import net.minecraft.world.item.Items


object WitcheryItems {

    val ITEMS: DeferredRegister<Item> = DeferredRegister.create(Witchery.MODID, Registries.ITEM)

    val MUTANDIS = ITEMS.register("mutandis") {
        MutandisItem(Item.Properties())
    }

    val MUTANDIS_EXTREMIS = ITEMS.register("mutandis_extremis") {
        MutandisItem(Item.Properties())
    }

    val GLINTWEED = ITEMS.register("glintweed") {
        BlockItem(WitcheryBlocks.GLINTWEED.get(), Item.Properties())
    }
    val EMBER_MOSS = ITEMS.register("ember_moss") {
        BlockItem(WitcheryBlocks.EMBER_MOSS.get(), Item.Properties())
    }
    val SPANISH_MOSS = ITEMS.register("spanish_moss") {
        BlockItem(WitcheryBlocks.SPANISH_MOSS.get(), Item.Properties())
    }

    //start RESOURCES
    val MANDRAKE_SEEDS = ITEMS.register("mandrake_seeds") {
        ItemNameBlockItem(WitcheryBlocks.MANDRAKE_CROP.get(), Item.Properties())
    }

    val ICY_NEEDLE = ITEMS.register("icy_needle") {
        Item(Item.Properties())
    }

    val MANDRAKE_ROOT = ITEMS.register("mandrake_root") {
        Item(Item.Properties())
    }

    val BELLADONNA_SEEDS = ITEMS.register("belladonna_seeds") {
        ItemNameBlockItem(WitcheryBlocks.BELLADONNAE_CROP.get(), Item.Properties())
    }

    val BELLADONNA_FLOWER = ITEMS.register("belladonna_flower") {
        Item(Item.Properties())
    }

    val WATER_ARTICHOKE_GLOBE = ITEMS.register("water_artichoke_globe") {
        Item(Item.Properties())
    }

    val WOOD_ASH = ITEMS.register("wood_ash") {
        Item(Item.Properties())
    }

    val BONE_NEEDLE = ITEMS.register("bone_needle") {
        Item(Item.Properties())
    }

    val DEMON_HEART = ITEMS.register("demon_heart") {
        Item(Item.Properties())
    }

    val GYPSUM = ITEMS.register("gypsum") {
        Item(Item.Properties())
    }
    //end RESOURCES

    //start JARS
    val CLAY_JAR = ITEMS.register("clay_jar") {
        Item(Item.Properties())
    }

    val JAR = ITEMS.register("jar") {
        Item(Item.Properties())
    }

    val BREATH_OF_THE_GODDESS = ITEMS.register("breath_of_the_goddess") {
        Item(Item.Properties())
    }

    val WHIFF_OF_MAGIC = ITEMS.register("whiff_of_magic") {
        Item(Item.Properties())
    }

    val FOUL_FUME = ITEMS.register("foul_fume") {
        Item(Item.Properties())
    }

    val TEAR_OF_THE_GODDESS = ITEMS.register("tear_of_the_goddess") {
        Item(Item.Properties())
    }

    val OIL_OF_VITRIOL = ITEMS.register("oil_of_vitriol") {
        Item(Item.Properties())
    }

    val EXHALE_OF_THE_HORNED_ONE = ITEMS.register("exhale_of_the_horned_one") {
        Item(Item.Properties())
    }

    val HINT_OF_REBIRTH = ITEMS.register("hint_of_rebirth") {
        Item(Item.Properties())
    }

    val REEK_OF_MISFORTUNE = ITEMS.register("reek_of_misfortune") {
        Item(Item.Properties())
    }

    val ODOR_OF_PURITY = ITEMS.register("odor_of_purity") {
        Item(Item.Properties())
    }

    val DROP_OF_LUCK = ITEMS.register("drop_of_luck") {
        Item(Item.Properties())
    }

    val ENDER_DEW = ITEMS.register("ender_dew") {
        Item(Item.Properties())
    }

    val DEMON_BLOOD = ITEMS.register("demon_blood") {
        Item(Item.Properties())
    }

    //end JARS

    //start CHALK
    val RITUAL_CHALK = ITEMS.register("ritual_chalk") {
        ChalkItem(Item.Properties())
    }

    val GOLDEN_CHALK = ITEMS.register("golden_chalk") {
        ChalkItem(Item.Properties())
    }

    val INFERNAL_CHALK = ITEMS.register("infernal_chalk") {
        ChalkItem(Item.Properties())
    }

    val OTHERWHERE_CHALK = ITEMS.register("otherwhere_chalk") {
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

    val OVEN: RegistrySupplier<BlockItem> = ITEMS.register("oven") {
        BlockItem(WitcheryBlocks.OVEN.get(), Item.Properties())
    }
}