package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import java.util.concurrent.CompletableFuture

class WitcheryLangProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricLanguageProvider(dataOutput, registryLookup) {

    override fun generateTranslations(registryLookup: HolderLookup.Provider?, builder: TranslationBuilder) {
        builder.add("witchery.main", "Witchery")

        builder.add("emi.category.witchery.cauldron_brewing", "Cauldron Brewing")
        builder.add("emi.category.witchery.cauldron_crafting", "Cauldron Crafting")
        builder.add("emi.category.witchery.oven_cooking", "Oven Fumigation")
        builder.add("container.witchery.oven_menu", "Witches Oven")
        builder.add("container.witchery.altar_menu", "Altar")

        builder.add(WitcheryItems.DEEPSLATE_ALTAR_BLOCK.get(), "Deepslate Altar Block")
        builder.add(WitcheryItems.GUIDEBOOK.get(), "Lesser Key of Solomon")
        builder.add(WitcheryItems.CAULDRON.get(), "Cauldron")
        builder.add(WitcheryItems.ALTAR.get(), "Altar")
        builder.add(WitcheryItems.MUTANDIS.get(), "Mutandis")
        builder.add(WitcheryItems.MUTANDIS_EXTREMIS.get(), "Mutandis Extremis")
        builder.add(WitcheryItems.MANDRAKE_ROOT.get(), "Mandrake Root")
        builder.add(WitcheryItems.GYPSUM.get(), "Gypsum")
        builder.add(WitcheryItems.WOOD_ASH.get(), "Wood Ash")
        builder.add(WitcheryItems.BELLADONNA_FLOWER.get(), "Belladonna Flower")
        builder.add(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), "Water Artichoke Globe")
        builder.add(WitcheryItems.BONE_NEEDLE.get(), "Bone Needle")
        builder.add(WitcheryItems.DEMON_HEART.get(), "Demon Heart")
        builder.add(WitcheryItems.RITUAL_CHALK.get(), "Ritual Chalk")
        builder.add(WitcheryItems.INFERNAL_CHALK.get(), "Infernal Chalk")
        builder.add(WitcheryItems.OTHERWHERE_CHALK.get(), "Otherwhere Chalk")
        builder.add(WitcheryItems.GOLDEN_CHALK.get(), "Golden Chalk")
        builder.add(WitcheryItems.ICY_NEEDLE.get(), "Icy Needle")
        builder.add(WitcheryItems.BELLADONNA_SEEDS.get(), "Belladonna Seeds")
        builder.add(WitcheryItems.MANDRAKE_SEEDS.get(), "Mandrake Seeds")
        builder.add(WitcheryItems.SNOWBELL_SEEDS.get(), "Snowbell Seeds")
        builder.add(WitcheryItems.WATER_ARTICHOKE_SEEDS.get(), "Water Artichoke Seeds")
        builder.add(WitcheryItems.WORMWOOD.get(), "Wormwood")
        builder.add(WitcheryItems.WORMWOOD_SEEDS.get(), "Wormwood Seeds")
        builder.add(WitcheryItems.WOLFSBANE.get(), "Wolfsbane")
        builder.add(WitcheryItems.GARLIC.get(), "Garlic")
        builder.add(WitcheryItems.WOLFSBANE_SEEDS.get(), "Wolfsbane Seeds")
        builder.add(WitcheryItems.TAGLOCK.get(), "Taglock")

        builder.add(WitcheryBlocks.GLINTWEED.get(), "Glintweed")
        builder.add(WitcheryBlocks.EMBER_MOSS.get(), "Ember Moss")
        builder.add(WitcheryBlocks.SPANISH_MOSS.get(), "Spanish Moss")
        builder.add(WitcheryBlocks.IRON_WITCHES_OVEN.get(), "Iron Witches Oven")
        builder.add(WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get(), "Iron Witches Oven Fume Filter")
        builder.add(WitcheryBlocks.COPPER_WITCHES_OVEN.get(), "Copper Witches Oven")
        builder.add(WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(), "Copper Witches Oven Fume Filter")

        builder.add(WitcheryItems.WAYSTONE.get(), "Waystone")
        builder.add(WitcheryItems.CLAY_JAR.get(), "Clay Jar")
        builder.add(WitcheryItems.JAR.get(), "Jar")
        builder.add(WitcheryItems.BREATH_OF_THE_GODDESS.get(), "Breath of the Goddess")
        builder.add(WitcheryItems.WHIFF_OF_MAGIC.get(), "Whiff of Magic")
        builder.add(WitcheryItems.FOUL_FUME.get(), "Foul Fume")
        builder.add(WitcheryItems.TEAR_OF_THE_GODDESS.get(), "Tear of the Goddess")
        builder.add(WitcheryItems.OIL_OF_VITRIOL.get(), "Oil of Vitriol")
        builder.add(WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get(), "Exhale of the Horned One")
        builder.add(WitcheryItems.HINT_OF_REBIRTH.get(), "Hint of Rebirth")
        builder.add(WitcheryItems.REEK_OF_MISFORTUNE.get(), "Reek of Misfortune")
        builder.add(WitcheryItems.ODOR_OF_PURITY.get(), "Odor of Purity")
        builder.add(WitcheryItems.DROP_OF_LUCK.get(), "Drop of Luck")
        builder.add(WitcheryItems.ENDER_DEW.get(), "Ender Dew")
        builder.add(WitcheryItems.DEMONS_BLOOD.get(), "Demon Blood")
        builder.add(WitcheryItems.FOCUSED_WILL.get(), "Focused Will")
        builder.add(WitcheryItems.CONDENSED_FEAR.get(), "Condensed Fear")
        builder.add(WitcheryItems.MELLIFLUOUS_HUNGER.get(), "Mellifluous Hunger")

        builder.add(WitcheryBlocks.ROWAN_LOG.get(), "Rowan Log")
        builder.add(WitcheryBlocks.ROWAN_WOOD.get(), "Rowan Wood")
        builder.add(WitcheryBlocks.STRIPPED_ROWAN_LOG.get(), "Stripped Rowan Log")
        builder.add(WitcheryBlocks.STRIPPED_ROWAN_WOOD.get(), "Stripped Rowan Wood")
        builder.add(WitcheryBlocks.ROWAN_LEAVES.get(), "Rowan Leaves")
        builder.add(WitcheryBlocks.ROWAN_BERRY_LEAVES.get(), "Rowan Berry Leaves")
        builder.add(WitcheryBlocks.ROWAN_PLANKS.get(), "Rowan Planks")
        builder.add(WitcheryBlocks.ROWAN_STAIRS.get(), "Rowan Stairs")
        builder.add(WitcheryBlocks.ROWAN_SLAB.get(), "Rowan Slab")
        builder.add(WitcheryBlocks.ROWAN_FENCE.get(), "Rowan Fence")
        builder.add(WitcheryBlocks.ROWAN_FENCE_GATE.get(), "Rowan Fence Gate")
        builder.add(WitcheryBlocks.ROWAN_DOOR.get(), "Rowan Door")
        builder.add(WitcheryBlocks.ROWAN_TRAPDOOR.get(), "Rowan Trapdoor")
        builder.add(WitcheryBlocks.ROWAN_PRESSURE_PLATE.get(), "Rowan Pressure Plate")
        builder.add(WitcheryBlocks.ROWAN_BUTTON.get(), "Rowan Button")
        builder.add(WitcheryBlocks.ROWAN_SAPLING.get(), "Rowan Sapling")
        builder.add(WitcheryBlocks.POTTED_ROWAN_SAPLING.get(), "Potted Rowan Sapling")
        builder.add(WitcheryBlocks.ROWAN_SIGN.get(), "Rowan Sign")
        builder.add(WitcheryBlocks.ROWAN_HANGING_SIGN.get(), "Rowan Hanging Sign")
        builder.add(WitcheryItems.ROWAN_BOAT.get(), "Rowan Boat")
        builder.add(WitcheryItems.ROWAN_CHEST_BOAT.get(), "Rowan Chest Boat")

        builder.add(WitcheryEntityTypes.MANDRAKE.get(), "Mandrake")
        builder.add("entity.witchery.rowan_boat", "Rowan Boat")
        builder.add("entity.witchery.rowan_chest_boat", "Rowan Chest Boat")
    }
}