package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.fabric.registry.WitcheryOxidizables
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryTags
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
        builder.add("emi.category.witchery.ritual", "Ritual")
        builder.add("emi.category.witchery.oven_cooking", "Oven Fumigation")
        builder.add("emi.category.witchery.distilling", "Distilling")
        builder.add("emi.category.witchery.spinning", "Spinning")
        builder.add("container.witchery.oven_menu", "Witches Oven")
        builder.add("container.witchery.altar_menu", "Altar")
        builder.add("container.witchery.spinning_wheel", "Spinning Wheel")
        builder.add("container.witchery.distillery", "Distillery")

        builder.add("accessories.slot.poppet", "Poppet")

        builder.add("witchery.secondbrewbonus.25", "+25% chance of second brew")
        builder.add("witchery.secondbrewbonus.35", "+35% chance of second brew")
        builder.add("witchery.thirdbrewbonus.25", "+25% chance of third brew")
        builder.add("witchery.infusion.ointment", "Flying Ointment")

        builder.add("witchery.blood", "Blood")
        builder.add("witchery.vampire_blood", "Blood?")
        builder.add( "witchery.use_with_needle", "Use with Bone Needle to fill")

        builder.add("witchery:all_worlds", "All Worlds")
        builder.add("witchery:dream_world", "Dream World")
        builder.add("witchery:nightmare_world", "Nightmare World")

        builder.add("witchery.item.tooltip.infinity_egg", "Creative Only")

        builder.add("witchery.celestial.day", "Day")
        builder.add("witchery.celestial.full", "Full Moon")
        builder.add("witchery.celestial.new", "New Moon")
        builder.add("witchery.celestial.waning", "Waning Moon")
        builder.add("witchery.celestial.waxing", "Waxing Moon")

        builder.add(WitcheryBlocks.SNOWBELL_CROP.get(), "Snowbell")
        builder.add(WitcheryBlocks.WATER_ARTICHOKE_CROP.get(), "Water Artichoke")
        builder.add(WitcheryBlocks.BELLADONNA_CROP.get(), "Belladonna")
        builder.add(WitcheryBlocks.WOLFSFBANE_CROP.get(), "Wolfsbane")
        builder.add(WitcheryBlocks.MANDRAKE_CROP.get(), "Mandrake")
        builder.add(WitcheryBlocks.GARLIC_CROP.get(), "Garlic")
        builder.add(WitcheryBlocks.WORMWOOD_CROP.get(), "Wormwood")
        builder.add(WitcheryItems.BLOOD_POPPY.get(), "Blood Poppy")
        builder.add("witchery.attuned.charged", "Attuned")

        builder.add(WitcheryItems.WOODEN_OAK_STAKE.get(), "Wooden Oak Stake")
        builder.add(WitcheryItems.WOODEN_HAWTHORN_STAKE.get(), "Wooden Hawthorn Stake")

        builder.add(WitcheryItems.INFINITY_EGG.get(), "Infinity Egg")
        builder.add(WitcheryItems.DEEPSLATE_ALTAR_BLOCK.get(), "Deepslate Altar Block")
        builder.add(WitcheryItems.DISTILLERY.get(), "Distillery")
        builder.add(WitcheryItems.SPINNING_WHEEL.get(), "Spinning Wheel")
        builder.add(WitcheryItems.GUIDEBOOK.get(), "Lesser Key of Solomon")
        builder.add(WitcheryItems.CAULDRON.get(), "Cauldron")
        builder.add(WitcheryItems.COPPER_CAULDRON.get(), "Copper Cauldron")

        builder.add(WitcheryItems.WAXED_COPPER_CAULDRON.get(), "Waxed Copper Cauldron")
        builder.add(WitcheryItems.EXPOSED_COPPER_CAULDRON.get(), "Exposed Copper Cauldron")
        builder.add(WitcheryItems.WAXED_EXPOSED_COPPER_CAULDRON.get(), "Waxed Exposed Copper Cauldron")
        builder.add(WitcheryItems.WEATHERED_COPPER_CAULDRON.get(), "Weathered Copper Cauldron")
        builder.add(WitcheryItems.WAXED_WEATHERED_COPPER_CAULDRON.get(), "Waxed Weathered Copper Cauldron")
        builder.add(WitcheryItems.OXIDIZED_COPPER_CAULDRON.get(), "Oxidized Copper Cauldron")
        builder.add(WitcheryItems.WAXED_OXIDIZED_COPPER_CAULDRON.get(), "Waxed Oxidized Copper Cauldron")

        builder.add(WitcheryItems.BLOOD_STAINED_WOOL.get(), "Blood-stained Wool")
        builder.add(WitcheryItems.WOVEN_CRUOR.get(), "Woven Cruor")

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
        builder.add(WitcheryItems.REFINED_EVIL.get(), "Refined Evil")
        builder.add(WitcheryItems.REDSTONE_SOUP.get(), "Redstone Soup")
        builder.add(WitcheryItems.FLYING_OINTMENT.get(), "Flying Ointment")
        builder.add(WitcheryItems.SOUL_OF_THE_WORLD.get(), "Soul of the Overworld")
        builder.add(WitcheryItems.SPIRIT_OF_OTHERWHERE.get(), "Spirit of Otherwhere")
        builder.add(WitcheryItems.GHOST_OF_THE_LIGHT.get(), "Ghost of the Light")
        builder.add(WitcheryItems.INFERNAL_ANIMUS.get(), "Infernal Animus")
        builder.add(WitcheryItems.TONGUE_OF_DOG.get(), "Tongue of Dog")
        builder.add(WitcheryItems.WOOL_OF_BAT.get(), "Wool of Bat")
        builder.add(WitcheryItems.TOE_OF_FROG.get(), "Toe of Frog")
        builder.add(WitcheryItems.OWLETS_WING.get(), "Owlet's Wing")
        builder.add(WitcheryItems.ENT_TWIG.get(), "Ent Twig")
        builder.add(WitcheryItems.SPECTRAL_DUST.get(), "Spectral Dust")
        builder.add(WitcheryItems.ATTUNED_STONE.get(), "Attuned Stone")
        builder.add(WitcheryItems.ROWAN_BERRIES.get(), "Rowan Berries")
        builder.add(WitcheryItems.LEECH.get(), "Leech")
        builder.add(WitcheryItems.WITCHES_HAND.get(), "Witches Hand")
        builder.add(WitcheryItems.WITCHES_HAT.get(), "Witches Hat")
        builder.add(WitcheryItems.WITCHES_ROBES.get(), "Witches Robes")
        builder.add(WitcheryItems.WITCHES_SLIPPERS.get(), "Witches Slippers")
        builder.add(WitcheryItems.HUNTER_HELMET.get(), "Hunter Helmet")
        builder.add(WitcheryItems.HUNTER_CHESTPLATE.get(), "Hunter Chestplate")
        builder.add(WitcheryItems.HUNTER_LEGGINGS.get(), "Hunter Leggings")
        builder.add(WitcheryItems.HUNTER_BOOTS.get(), "Hunter Boots")
        builder.add(WitcheryItems.TOP_HAT.get(), "Top Hat")
        builder.add(WitcheryItems.DRESS_COAT.get(), "Dress Coat")
        builder.add(WitcheryItems.TROUSERS.get(), "Trousers")
        builder.add(WitcheryItems.OXFORD_BOOTS.get(), "Oxford Boots")
        builder.add(WitcheryItems.GOLDEN_THREAD.get(), "Golden Thread")
        builder.add(WitcheryItems.POPPET.get(), "Poppet")
        builder.add(WitcheryItems.ARMOR_PROTECTION_POPPET.get(), "Armor Protection Poppet")
        builder.add(WitcheryItems.HUNGER_PROTECTION_POPPET.get(), "Hunger Protection Poppet")
        builder.add(WitcheryItems.DEATH_PROTECTION_POPPET.get(), "Death Protection Poppet")
        builder.add(WitcheryItems.VAMPIRIC_POPPET.get(), "Vampiric Poppet")
        builder.add(WitcheryItems.VOODOO_POPPET.get(), "Voodoo Poppet")
        builder.add(WitcheryItems.VOODOO_PROTECTION_POPPET.get(), "Voodoo Protection Poppet")
        builder.add(WitcheryItems.BABA_YAGAS_HAT.get(), "Baba Yaga's Hat")
        builder.add(WitcheryItems.IMPREGNATED_FABRIC.get(), "Impregnated Fabric")
        builder.add(WitcheryItems.MUTATING_SPRING.get(), "Mutating Spring")
        builder.add(WitcheryItems.BROOM.get(), "Broom")
        builder.add(WitcheryEntityTypes.BROOM.get(), "Broom")
        builder.add(WitcheryEntityTypes.DEMON.get(), "Demon")
        builder.add(WitcheryEntityTypes.IMP.get(), "Imp")
        builder.add(WitcheryEntityTypes.OWL.get(), "Owl")
        builder.add(WitcheryEntityTypes.ENT.get(), "Ent")
        builder.add(WitcheryEntityTypes.FLOATING_ITEM.get(), "Floating Item")
        builder.add(WitcheryEntityTypes.SLEEPING_PLAYER.get(), "Sleeping Player")
        builder.add(WitcheryEntityTypes.BANSHEE.get(), "Banshee")
        builder.add(WitcheryEntityTypes.NIGHTMARE.get(), "Nightmare")
        builder.add(WitcheryEntityTypes.VAMPIRE.get(), "Vampire")
        builder.add(WitcheryEntityTypes.WEREWOLF.get(), "Werewolf")
        builder.add(WitcheryEntityTypes.LILITH.get(), "Lilith")
        builder.add(WitcheryEntityTypes.ELLE.get(), "Elle")

        builder.add(WitcheryItems.CANE_SWORD.get(), "Cane Sword")
        builder.add(WitcheryBlocks.GLINTWEED.get(), "Glintweed")
        builder.add(WitcheryBlocks.EMBER_MOSS.get(), "Ember Moss")
        builder.add(WitcheryBlocks.SPANISH_MOSS.get(), "Spanish Moss")
        builder.add(WitcheryBlocks.IRON_WITCHES_OVEN.get(), "Iron Witches Oven")
        builder.add(WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION.get(), "Iron Witches Oven Fume Filter")
        builder.add(WitcheryBlocks.COPPER_WITCHES_OVEN.get(), "Copper Witches Oven")
        builder.add(WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN.get(), "Exposed Copper Witches Oven")
        builder.add(WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN.get(), "Weathered Copper Witches Oven")
        builder.add(WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN.get(), "Oxidized Copper Witches Oven")
        builder.add(WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN.get(), "Waxed Copper Witches Oven")
        builder.add(WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get(), "Waxed Exposed Copper Witches Oven")
        builder.add(WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get(), "Waxed Weathered Copper Witches Oven")
        builder.add(WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get(), "Waxed Oxidized Copper Witches Oven")
        builder.add(WitcheryBlocks.COPPER_WITCHES_OVEN_FUME_EXTENSION.get(), "Copper Witches Oven Fume Filter")
        builder.add(
            WitcheryBlocks.WAXED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            "Waxed Copper Witches Oven Fume Filter"
        )
        builder.add(
            WitcheryBlocks.EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            "Exposed Copper Witches Oven Fume Filter"
        )
        builder.add(
            WitcheryBlocks.WAXED_EXPOSED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            "Waxed Exposed Copper Witches Oven Fume Filter"
        )
        builder.add(
            WitcheryBlocks.WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            "Weathered Copper Witches Oven Fume Filter"
        )
        builder.add(
            WitcheryBlocks.WAXED_WEATHERED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            "Waxed Weathered Copper Witches Oven Fume Filter"
        )
        builder.add(
            WitcheryBlocks.OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            "Oxidized Copper Witches Oven Fume Filter"
        )
        builder.add(
            WitcheryBlocks.WAXED_OXIDIZED_COPPER_WITCHES_OVEN_FUME_EXTENSION.get(),
            "Waxed Oxidized Copper Witches Oven Fume Filter"
        )

        builder.add(WitcheryBlocks.IRON_CANDELABRA.get(), "Iron Candelabra")
        builder.add(WitcheryBlocks.WHITE_IRON_CANDELABRA.get(), "White Iron Candelabra")
        builder.add(WitcheryBlocks.ORANGE_IRON_CANDELABRA.get(), "Orange Iron Candelabra")
        builder.add(WitcheryBlocks.MAGENTA_IRON_CANDELABRA.get(), "Magenta Iron Candelabra")
        builder.add(WitcheryBlocks.LIGHT_BLUE_IRON_CANDELABRA.get(), "Light Blue Iron Candelabra")
        builder.add(WitcheryBlocks.YELLOW_IRON_CANDELABRA.get(), "Yellow Iron Candelabra")
        builder.add(WitcheryBlocks.LIME_IRON_CANDELABRA.get(), "Lime Iron Candelabra")
        builder.add(WitcheryBlocks.PINK_IRON_CANDELABRA.get(), "Pink Iron Candelabra")
        builder.add(WitcheryBlocks.GRAY_IRON_CANDELABRA.get(), "Gray Iron Candelabra")
        builder.add(WitcheryBlocks.LIGHT_GRAY_IRON_CANDELABRA.get(), "Light Gray Iron Candelabra")
        builder.add(WitcheryBlocks.CYAN_IRON_CANDELABRA.get(), "Cyan Iron Candelabra")
        builder.add(WitcheryBlocks.PURPLE_IRON_CANDELABRA.get(), "Purple Iron Candelabra")
        builder.add(WitcheryBlocks.BLUE_IRON_CANDELABRA.get(), "Blue Iron Candelabra")
        builder.add(WitcheryBlocks.BROWN_IRON_CANDELABRA.get(), "Brown Iron Candelabra")
        builder.add(WitcheryBlocks.GREEN_IRON_CANDELABRA.get(), "Green Iron Candelabra")
        builder.add(WitcheryBlocks.RED_IRON_CANDELABRA.get(), "Red Iron Candelabra")
        builder.add(WitcheryBlocks.BLACK_IRON_CANDELABRA.get(), "Black Iron Candelabra")
        builder.add(WitcheryBlocks.ARTHANA.get(), "Arthana")
        builder.add(WitcheryItems.ARTHANA.get(), "Arthana")
        builder.add(WitcheryBlocks.CHALICE.get(), "Chalice")
        builder.add(WitcheryBlocks.PENTACLE.get(), "Pentacle")
        builder.add(WitcheryBlocks.BRAZIER.get(), "Brazier")
        builder.add(WitcheryItems.NECROMANTIC_STONE.get(), "Necromantic Stone")


        builder.add(WitcheryBlocks.GOLDEN_CHALK_BLOCK.get(), "Golden Chalk")
        builder.add(WitcheryBlocks.RITUAL_CHALK_BLOCK.get(), "Ritual Chalk")
        builder.add(WitcheryBlocks.INFERNAL_CHALK_BLOCK.get(), "Infernal Chalk")
        builder.add(WitcheryBlocks.OTHERWHERE_CHALK_BLOCK.get(), "Otherwhere Chalk")
        builder.add(WitcheryBlocks.DEMON_HEART.get(), "Demon Heart")
        builder.add(WitcheryBlocks.ALTAR_COMPONENT.get(), "Altar")
        builder.add(WitcheryBlocks.CAULDRON_COMPONENT.get(), "Cauldron")
        builder.add(WitcheryBlocks.DISTILLERY_COMPONENT.get(), "Distillery")
        builder.add(WitcheryBlocks.IRON_WITCHES_OVEN_FUME_EXTENSION_COMPONENT.get(), "Fume Filter")

        builder.add(WitcheryBlocks.SACRIFICIAL_CIRCLE_COMPONENT.get(), "Sacrificial Circle")
        builder.add(WitcheryBlocks.SACRIFICIAL_CIRCLE.get(), "Sacrificial Circle")
        builder.add(WitcheryItems.QUARTZ_SPHERE.get(), "Quartz Sphere")
        builder.add(WitcheryBlocks.SUNLIGHT_COLLECTOR.get(), "Sunlight Collector")
        builder.add("witchery.has_sun", "Sunlight")

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
        builder.add(WitcheryItems.PHANTOM_VAPOR.get(), "Phantom Vapor")

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

        builder.add(WitcheryItems.DREAM_WEAVER_OF_FLEET_FOOT.get(), "Dream Weaver of Fleet Foot")
        builder.add(WitcheryItems.DREAM_WEAVER_OF_NIGHTMARES.get(), "Dream Weaver of Nightmares")
        builder.add(WitcheryItems.DREAM_WEAVER_OF_FASTING.get(), "Dream Weaver of Fasting")
        builder.add(WitcheryItems.DREAM_WEAVER_OF_IRON_ARM.get(), "Dream Weaver of Iron Arm")
        builder.add(WitcheryItems.DREAM_WEAVER_OF_INTENSITY.get(), "Dream Weaver of Intensity")
        builder.add(WitcheryItems.DREAM_WEAVER.get(), "Dream Weaver")

        builder.add(WitcheryItems.BREW_OF_SLEEPING.get(), "Brew of Sleeping")
        builder.add(WitcheryItems.BREW_OF_LOVE.get(), "Brew of Love")
        builder.add(WitcheryItems.BREW_OF_INK.get(), "Brew of Ink")
        builder.add(WitcheryItems.BREW_OF_REVEALING.get(), "Brew of Revealing")
        builder.add(WitcheryItems.BREW_OF_EROSION.get(), "Brew of Erosion")
        builder.add(WitcheryItems.BREW_OF_WASTING.get(), "Brew of Wasting")
        builder.add(WitcheryItems.BREW_OF_WEBS.get(), "Brew of Webs")
        builder.add(WitcheryItems.BREW_OF_THE_DEPTHS.get(), "Brew of the Depths")
        builder.add(WitcheryItems.BREW_OF_RAISING.get(), "Brew of Raising")
        builder.add(WitcheryItems.BREW_OF_FROST.get(), "Brew of Frost")
        builder.add(WitcheryItems.DISTURBED_COTTON.get(), "Disturbed Cotton")
        builder.add(WitcheryItems.WISPY_COTTON.get(), "Wispy Cotton")
        builder.add(WitcheryItems.FANCIFUL_THREAD.get(), "Fanciful Thread")
        builder.add(WitcheryItems.WINE_GLASS.get(), "Wine Glass")
        builder.add(WitcheryItems.TORMENTED_TWINE.get(), "Tormented Twine")
        builder.add(WitcheryItems.FLOWING_SPIRIT_BUCKET.get(), "Flowing Spirit Bucket")
        builder.add(WitcheryItems.BREW_FLOWING_SPIRIT.get(), "Brew of Flowing Spirit")

        builder.add(WitcheryBlocks.ALDER_LOG.get(), "Alder Log")
        builder.add(WitcheryBlocks.ALDER_WOOD.get(), "Alder Wood")
        builder.add(WitcheryBlocks.STRIPPED_ALDER_LOG.get(), "Stripped Alder Log")
        builder.add(WitcheryBlocks.STRIPPED_ALDER_WOOD.get(), "Stripped Alder Wood")
        builder.add(WitcheryBlocks.ALDER_LEAVES.get(), "Alder Leaves")
        builder.add(WitcheryBlocks.ALDER_PLANKS.get(), "Alder Planks")
        builder.add(WitcheryBlocks.ALDER_STAIRS.get(), "Alder Stairs")
        builder.add(WitcheryBlocks.ALDER_SLAB.get(), "Alder Slab")
        builder.add(WitcheryBlocks.ALDER_FENCE.get(), "Alder Fence")
        builder.add(WitcheryBlocks.ALDER_FENCE_GATE.get(), "Alder Fence Gate")
        builder.add(WitcheryBlocks.ALDER_DOOR.get(), "Alder Door")
        builder.add(WitcheryBlocks.ALDER_TRAPDOOR.get(), "Alder Trapdoor")
        builder.add(WitcheryBlocks.ALDER_PRESSURE_PLATE.get(), "Alder Pressure Plate")
        builder.add(WitcheryBlocks.ALDER_BUTTON.get(), "Alder Button")
        builder.add(WitcheryBlocks.ALDER_SAPLING.get(), "Alder Sapling")
        builder.add(WitcheryBlocks.POTTED_ALDER_SAPLING.get(), "Potted Alder Sapling")
        builder.add(WitcheryBlocks.ALDER_SIGN.get(), "Alder Sign")
        builder.add(WitcheryBlocks.ALDER_HANGING_SIGN.get(), "Alder Hanging Sign")
        builder.add(WitcheryItems.ALDER_BOAT.get(), "Alder Boat")
        builder.add(WitcheryItems.ALDER_CHEST_BOAT.get(), "Alder Chest Boat")

        builder.add(WitcheryItems.GRAVESTONE.get(), "Gravestone")
        builder.add(WitcheryItems.TORN_PAGE.get(), "Torn Page")
        builder.add(WitcheryItems.SUSPICIOUS_GRAVEYARD_DIRT.get(), "Suspicious Graveyard Dirt")

        builder.add(WitcheryBlocks.SPIRIT_PORTAL.get(), "Spirit Portal")
        builder.add(WitcheryBlocks.SPIRIT_PORTAL_COMPONENT.get(), "Spirit Portal")

        builder.add(WitcheryBlocks.HAWTHORN_LOG.get(), "Hawthorn Log")
        builder.add(WitcheryBlocks.HAWTHORN_WOOD.get(), "Hawthorn Wood")
        builder.add(WitcheryBlocks.STRIPPED_HAWTHORN_LOG.get(), "Stripped Hawthorn Log")
        builder.add(WitcheryBlocks.STRIPPED_HAWTHORN_WOOD.get(), "Stripped Hawthorn Wood")
        builder.add(WitcheryBlocks.HAWTHORN_LEAVES.get(), "Hawthorn Leaves")
        builder.add(WitcheryBlocks.HAWTHORN_PLANKS.get(), "Hawthorn Planks")
        builder.add(WitcheryBlocks.HAWTHORN_STAIRS.get(), "Hawthorn Stairs")
        builder.add(WitcheryBlocks.HAWTHORN_SLAB.get(), "Hawthorn Slab")
        builder.add(WitcheryBlocks.HAWTHORN_FENCE.get(), "Hawthorn Fence")
        builder.add(WitcheryBlocks.HAWTHORN_FENCE_GATE.get(), "Hawthorn Fence Gate")
        builder.add(WitcheryBlocks.HAWTHORN_DOOR.get(), "Hawthorn Door")
        builder.add(WitcheryBlocks.HAWTHORN_TRAPDOOR.get(), "Hawthorn Trapdoor")
        builder.add(WitcheryBlocks.HAWTHORN_PRESSURE_PLATE.get(), "Hawthorn Pressure Plate")
        builder.add(WitcheryBlocks.HAWTHORN_BUTTON.get(), "Hawthorn Button")
        builder.add(WitcheryBlocks.HAWTHORN_SAPLING.get(), "Hawthorn Sapling")
        builder.add(WitcheryBlocks.POTTED_HAWTHORN_SAPLING.get(), "Potted Hawthorn Sapling")
        builder.add(WitcheryBlocks.HAWTHORN_SIGN.get(), "Hawthorn Sign")
        builder.add(WitcheryBlocks.HAWTHORN_HANGING_SIGN.get(), "Hawthorn Hanging Sign")
        builder.add(WitcheryItems.HAWTHORN_BOAT.get(), "Hawthorn Boat")
        builder.add(WitcheryItems.HAWTHORN_CHEST_BOAT.get(), "Hawthorn Chest Boat")

        builder.add(WitcheryBlocks.BLOOD_POPPY.get(), "Blood Poppy")
        builder.add(WitcheryItems.BATWING_PENDANT.get(), "Batwing Pendant")
        builder.add(WitcheryItems.SUNSTONE_PENDANT.get(), "Sunstone Pendant")
        builder.add(WitcheryItems.BLOODSTONE_PENDANT.get(), "Bloodstone Pendant")
        builder.add(WitcheryItems.DREAMWEAVER_CHARM.get(), "Dreamweaver Charm")
        builder.add(WitcheryItems.BITING_BELT.get(), "Biting Belt")
        builder.add(WitcheryItems.BARK_BELT.get(), "Bark Belt")

        builder.add(WitcheryEntityTypes.MANDRAKE.get(), "Mandrake")
        builder.add(WitcheryEntityTypes.SPECTRAL_PIG.get(), "Spectral Pig")

        builder.add("attribute.name.witchery.vampire_bat_form_duration", "Bat-form Duration")
        builder.add("attribute.name.witchery.vampire_drink_speed", "Blooding Drink Speed")
        builder.add("attribute.name.witchery.vampire_sun_resistance", "Sun Resistance")

        builder.add(WitcheryTags.ROWAN_LOG_ITEMS, "Rowan Logs")
        builder.add(WitcheryTags.ALDER_LOG_ITEMS, "Alder Logs")
        builder.add(WitcheryTags.HAWTHORN_LOG_ITEMS, "Hawthorn Logs")
        builder.add(WitcheryTags.LEAF_ITEMS, "Witchery Leaves")
        builder.add(WitcheryTags.CANDELABRA_ITEMS, "Candelabras")
        builder.add(WitcheryTags.PLACEABLE_POPPETS, "Placeable Poppets")
        builder.add(WitcheryTags.SPIRIT_WORLD_TRANSFERABLE, "Spirit World Transferable")

        builder.add("entity.witchery.rowan_boat", "Rowan Boat")
        builder.add("entity.witchery.rowan_chest_boat", "Rowan Chest Boat")
        builder.add("entity.witchery.alder_boat", "Alder Boat")
        builder.add("entity.witchery.alder_chest_boat", "Alder Chest Boat")
        builder.add("entity.witchery.hawthorn_boat", "Hawthorn Boat")
        builder.add("entity.witchery.hawthorn_chest_boat", "Hawthorn Chest Boat")


        builder.add("advancements.witchery.seeds.title", "The Start")
        builder.add("advancements.witchery.seeds.description", "Expensive on Etsy")
        builder.add("advancements.witchery.oven.title", "Smells nice")
        builder.add("advancements.witchery.oven.description", "In my gingerbread house")
        builder.add("advancements.witchery.cauldron.title", "A Stew is due")
        builder.add("advancements.witchery.cauldron.description", "Cookin'")
        builder.add("advancements.witchery.mutandis.title", "Lost plants")
        builder.add("advancements.witchery.mutandis.description", "CRISPR those flowers")
        builder.add("advancements.witchery.whiff_of_magic.title", "Wonder")
        builder.add("advancements.witchery.whiff_of_magic.description", "Unicorn farts")
        builder.add("advancements.witchery.chalk.title", "Rituals")
        builder.add("advancements.witchery.chalk.description", "And I started ritualing")
        builder.add("advancements.witchery.necromantic.title", "Necromantic")
        builder.add("advancements.witchery.necromantic.description", "No mind to break")

        builder.add("emi.category.witchery.brazier", "Brazier")
        builder.add("witchery:brazier_summoning/summon_banshee", "Summon Banshee")
        builder.add("witchery:brazier_summoning/summon_banshee.tooltip", "Summons a Banshee")

        builder.add("witchery:ritual/rite_of_charging_infusion", "Rite of Charging")
        builder.add("witchery:ritual/rite_of_charging_infusion.tooltip", "Charges the power of an Infused player")
        builder.add("witchery:ritual/infuse_light", "Infusion of Light")
        builder.add("witchery:ritual/infuse_light.tooltip", "Infuses the player with the Ghost of the Light")
        builder.add("witchery:ritual/infuse_otherwhere", "Infusion of Otherwhere")
        builder.add("witchery:ritual/infuse_otherwhere.tooltip", "Infuses the player with the Spirit of Otherwhere")

        builder.add("witchery:ritual/charge_attuned", "Rite of Charging")
        builder.add("witchery:ritual/charge_attuned.tooltip", "Charges an Attuned Stone with 2000 Altar Power")
        builder.add("witchery:ritual/summon_lightning_on_waystone", "Lightning Strike")
        builder.add(
            "witchery:ritual/summon_lightning_on_waystone.tooltip",
            "Summons a Lightning Strike at bound Waystone location"
        )
        builder.add("witchery:ritual/summon_lightning", "Lightning Strike")
        builder.add("witchery:ritual/summon_lightning.tooltip", "Summons a Lightning Strike at ritual center")
        builder.add("witchery:ritual/set_midnight", "Turn Night")
        builder.add("witchery:ritual/set_midnight.tooltip", "Sets the time to midnight")
        builder.add("witchery:ritual/push_mobs", "Rite of Sanctity")
        builder.add("witchery:ritual/push_mobs.tooltip", "Pushes hostile mobs away from ritual center")
        builder.add("witchery:ritual/teleport_owner_to_waystone", "Teleportation")
        builder.add(
            "witchery:ritual/teleport_owner_to_waystone.tooltip",
            "Teleports the user to bound Waystone location"
        )
        builder.add("witchery:ritual/teleport_taglock_to_waystone", "Teleportation")
        builder.add(
            "witchery:ritual/teleport_taglock_to_waystone.tooltip",
            "Teleports the bound Taglock entity to bound Waystone location"
        )
        builder.add("witchery:ritual/summon_spectral_pig", "Summon Spectral Pig")
        builder.add("witchery:ritual/summon_spectral_pig.tooltip", "Summon an Spectral Pig at ritual center")
        builder.add("witchery:ritual/summon_imp", "Summon Imp")
        builder.add("witchery:ritual/summon_imp.tooltip", "Summon an Imp at ritual center")
        builder.add("witchery:ritual/apply_ointment", "Imbue Flying Ointment")
        builder.add("witchery:ritual/apply_ointment.tooltip", "Allows flying with the Broom")


        builder.add("witchery:ritual/summon_witch", "Summon Witch")
        builder.add("witchery:ritual/summon_witch.tooltip", "Summon a Witch at ritual center")
        builder.add("witchery:ritual/summon_demon", "Summon Demon")
        builder.add("witchery:ritual/summon_demon.tooltip", "Summon a Demon at ritual center")
        builder.add("witchery:ritual/summon_wither", "Summon Wither")
        builder.add("witchery:ritual/summon_wither.tooltip", "Summon a Wither at ritual center")


        builder.add("witchery:ritual/necro_stone", "Necromantic Stone")
        builder.add("witchery:ritual/necro_stone.tooltip", "Created at night")

        //GUIDEBOOK GENERAL
        builder.add("book.witchery.guidebook.general.name", "General")
        builder.add("book.witchery.guidebook.brewing.name", "Brewing")
        builder.add("book.witchery.guidebook.ritual.name", "Rituals")


        builder.add("book.witchery.guidebook.general.beginning.name", "Beginning")
        builder.add("book.witchery.guidebook.general.beginning.description", "Essential Witchery resources")
        builder.add("general.beginning.title", "Essential Witchery Resources")
        builder.add("general.beginning.page.1", "Nature and seeds good, break grass to get cool seeds")


        builder.add("book.witchery.guidebook.general.cauldron.name", "Cauldron")
        builder.add("book.witchery.guidebook.general.cauldron.description", "A watched pot never boils")
        builder.add("general.cauldron.title", "Witches Cauldron")
        builder.add("general.cauldron.page.1", "Lets cook")

        builder.add("book.witchery.guidebook.general.oven.name", "Oven")
        builder.add("book.witchery.guidebook.general.oven.description", "Smells like home")
        builder.add("general.oven.title", "Witches Oven")
        builder.add("general.oven.page.1", "Lets cook")

        builder.add("book.witchery.guidebook.general.distillery.name", "Distillery")
        builder.add("book.witchery.guidebook.general.distillery.description", "Smells like home")
        builder.add("general.distillery.title", "Distillery")
        builder.add("general.distillery.page.1", "Lets cook")

        builder.add("general.mutandis.mutandis", "Mutandis")
        builder.add("general.oven.breath_of_the_goddess", "Breath of the Goddess")
        builder.add("general.oven.hint_of_rebirth", "Hint of Rebirth")
        builder.add("general.oven.exhale_of_the_horned_one", "Exhale of the Horned One")
        builder.add("general.oven.foul_fume_logs", "Foul Fume")
        builder.add("general.oven.whiff_of_magic", "Whiff of Magic")
        builder.add("general.oven.odor_of_purity", "Odor of Purity")
        builder.add("general.oven.reek_of_misfortune", "Reek of Misfortune")

        builder.add("general.distillery.oil_of_vitriol_gypsum", "Oil of Vitriol")
        builder.add("general.distillery.demons_blood", "Demons Blood")
        builder.add("general.distillery.ender_dew", "Ender Dew")
        builder.add("general.distillery.phantom_vapor", "Phantom Vapor")
        builder.add("general.distillery.reek_of_misfortune", "Reek of Misfortune")
        builder.add("general.distillery.refined_evil", "Refined Evil")
        builder.add("general.distillery.tear_and_whiff", "Tear of the Goddess")

        builder.add("book.witchery.guidebook.general.mutandis.name", "Mutandis")
        builder.add("book.witchery.guidebook.general.mutandis.description", "CRISPR that plant")
        builder.add("general.mutandis.title", "Mutandis")
        builder.add("general.mutandis.page.1", "Evil Scrambled Eggs")

        builder.add("book.witchery.guidebook.general.whiff_of_magic.name", "Whiff of Magic")
        builder.add("book.witchery.guidebook.general.whiff_of_magic.description", "Smells like potpourri")
        builder.add("general.whiff_of_magic.title", "Whiff of Magic")
        builder.add("general.whiff_of_magic.page.1", "Smells like potpourri")

        builder.add("book.witchery.guidebook.general.exhale_of_the_horned_one.name", "Exhale of the Horned One")
        builder.add("book.witchery.guidebook.general.exhale_of_the_horned_one.description", "Smells like mold")
        builder.add("general.exhale_of_the_horned_one.title", "Exhale of the Horned One")
        builder.add("general.exhale_of_the_horned_one.page.1", "Everything reminds me of him")

        builder.add("book.witchery.guidebook.general.hint_of_rebirth.name", "Hint of Rebirth")
        builder.add("book.witchery.guidebook.general.hint_of_rebirth.description", "Smells like lime")
        builder.add("general.hint_of_rebirth.title", "Hint of Rebirth")
        builder.add("general.hint_of_rebirth.page.1", "Wise words")

        builder.add("book.witchery.guidebook.general.breath_of_the_goddess.name", "Breath of the Goddess")
        builder.add("book.witchery.guidebook.general.breath_of_the_goddess.description", "Smells nice")
        builder.add("general.breath_of_the_goddess.title", "Breath of the Goddess")
        builder.add("general.breath_of_the_goddess.page.1", "Wise words")

        builder.add("book.witchery.guidebook.general.tear_of_the_goddess.name", "Tear of the Goddess")
        builder.add(
            "book.witchery.guidebook.general.tear_of_the_goddess.description",
            "Better than gamer girl bathwater"
        )
        builder.add("general.tear_of_the_goddess.title", "Tear of the Goddess")
        builder.add("general.tear_of_the_goddess.page.1", "Wise words")

        //GUIDEBOOK BREWING
        builder.add("book.witchery.guidebook.brewing.cauldron.name", "Cauldron")
        builder.add("book.witchery.guidebook.brewing.cauldron.description", "Swarley")
        builder.add("brewing.cauldron.title", "Cauldron")
        builder.add("brewing.cauldron.page.1", "Its not supposed to be brown, most of the time")

        builder.add("book.witchery.guidebook.brewing.ritual_chalk.name", "Ritual Chalk")
        builder.add("book.witchery.guidebook.brewing.ritual_chalk.description", "Better than crayons")
        builder.add("brewing.ritual_chalk.title", "Ritual Chalk")
        builder.add("brewing.ritual_chalk.page.1", "B")

        builder.add("book.witchery.guidebook.brewing.redstone_soup.name", "Redstone Soup")
        builder.add("book.witchery.guidebook.brewing.redstone_soup.description", "Got Soup?")
        builder.add("brewing.redstone_soup.title", "Redstone Soup")
        builder.add("brewing.redstone_soup.page.1", "Soup")

        builder.add("brewing.redstone_soup.redstone_soup", "Redstone Soup")
        builder.add("brewing.ritual_chalk.golden_chalk", "Golden Chalk")
        builder.add("brewing.ritual_chalk.infernal_chalk", "Infernal Chalk")
        builder.add("brewing.ritual_chalk.otherwhere_chalk", "Otherwhere Chalk")

        //GUIDEBOOK RITUALS
        builder.add("book.witchery.guidebook.ritual.ritual_chalk.name", "Ritual Chalk")
        builder.add("book.witchery.guidebook.ritual.ritual_chalk.description", "Better than crayons")
        builder.add("ritual.ritual_chalk.title", "Ritual Chalk")
        builder.add("ritual.ritual_chalk.page.1", "B")

        builder.add("book.witchery.guidebook.ritual.golden_chalk.name", "Golden Chalk")
        builder.add("book.witchery.guidebook.ritual.golden_chalk.description", "Better yellow than crayons")
        builder.add("ritual.golden_chalk.title", "Golden Chalk")
        builder.add("ritual.golden_chalk.page.1", "G")

        builder.add("book.witchery.guidebook.ritual.infernal_chalk.name", "Infernal Chalk")
        builder.add("book.witchery.guidebook.ritual.infernal_chalk.description", "Better red than crayons")
        builder.add("ritual.infernal_chalk.title", "Infernal Chalk")
        builder.add("ritual.infernal_chalk.page.1", "I")

        builder.add("book.witchery.guidebook.ritual.otherwhere_chalk.name", "Otherwhere Chalk")
        builder.add("book.witchery.guidebook.ritual.otherwhere_chalk.description", "Better purple than crayons")
        builder.add("ritual.otherwhere_chalk.title", "Otherwhere Chalk")
        builder.add("ritual.otherwhere_chalk.page.1", "I")


        builder.add("ritual.ritual_chalk.otherwhere_chalk", "Otherwhere Chalk")
        builder.add("ritual.ritual_chalk.infernal_chalk", "Infernal Chalk")
        builder.add("ritual.ritual_chalk.golden_chalk", "Golden Chalk")


        builder.add("book.witchery.guidebook.vampirism.vamp_1.name", "Observations of an Immortal")
        builder.add("book.witchery.guidebook.vampirism.vamp_2.name", "The Hunger")
        builder.add("book.witchery.guidebook.vampirism.vamp_3.name", "The Twisted")
        builder.add("book.witchery.guidebook.vampirism.vamp_4.name", "The Rush")
        builder.add("book.witchery.guidebook.vampirism.vamp_5.name", "The Enemy")
        builder.add("book.witchery.guidebook.vampirism.vamp_6.name", "The Burning Day")
        builder.add("book.witchery.guidebook.vampirism.vamp_7.name", "The Crimson Bloom")
        builder.add("book.witchery.guidebook.vampirism.vamp_8.name", "The Bloodborne Sky")
        builder.add("book.witchery.guidebook.vampirism.vamp_9.name", "The Eternal Covenant")
        builder.add("book.witchery.guidebook.vampirism.vamp_1.description", "First Stage")
        builder.add("book.witchery.guidebook.vampirism.vamp_2.description", "Second Stage")
        builder.add("book.witchery.guidebook.vampirism.vamp_3.description", "Third Stage")
        builder.add("book.witchery.guidebook.vampirism.vamp_4.description", "Forth Stage")
        builder.add("book.witchery.guidebook.vampirism.vamp_5.description", "Fifth Stage")
        builder.add("book.witchery.guidebook.vampirism.vamp_6.description", "Sixth Stage")
        builder.add("book.witchery.guidebook.vampirism.vamp_7.description", "Seventh Stage")
        builder.add("book.witchery.guidebook.vampirism.vamp_8.description", "Eighth Stage")
        builder.add("book.witchery.guidebook.vampirism.vamp_9.description", "Ninth Stage")
        builder.add("vampirism.vamp_1.title", "Observations of an Immortal")
        builder.add("vampirism.vamp_1.page.1", "\n" +
                "\\\n" +
                "\\\nIt is with some reluctance I commit these observations to paper, for what I have witnessed is not for the weak of mind. Instead, take my words as a warning.")
        builder.add("vampirism.vamp_1.page.2", "\n" +
                "\\\n" +
                "\\\n...he was reminiscing over dinner this evening about his \"birth\", a demonic pact of sorts...\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ..butchering a chicken over a skull with an Arthana and holding a glass goblet to collect the blood is barbaric,\" I told him...")
        builder.add("vampirism.vamp_1.page.3", "\n" +
                "\\\n" +
                "\\\n...apparently start of a long forgotten rite \n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ..night, open to the moon, chalk, candles and skull")
        builder.add("vampirism.vamp_1.page.4", "\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n...pouring the blood onto the skull... \n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ..mumbling about taking her to the underworld")
        builder.add("vampirism.vamp_1.page.5", "\n" +
                "\\\n" +
                "\\\n...proving his worth to her... \n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ..that glass goblet again, could someone really drink such a thing? ...")


        builder.add("vampirism.vamp_2.page.1", "\n" +
                "\\\n" +
                "\\\n...Today, or should I say this evening, he told me about his first kill...\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ...the thirst that first night, he said, was overwhelming, he had to fully sait hes hunger...")

        builder.add("vampirism.vamp_3.page.1", "\n" +
                "\\\n...he found he was able to transfix his victims...\n" +
                "\\\n" +
                "\\\n" +
                "\\\n... was now able to drink as he needed without others realizing, so long as he did not drain more than half..." +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ...did so, from five oblivious souls...")

        builder.add("vampirism.vamp_4.page.1", "\n" +
                "\\\n...strength was flowing into him, the more he drank, as the nights progressed, the stronger he became...\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ...it was on the forth night after his mastery of drinking that the world slowed down...")

        builder.add("vampirism.vamp_5.page.1", "\n" +
                "\\\n...his greatest foe, the sun, was ever present, tormenting and instantly deadly to him...\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ...became his obsession... ...found a way to collect sunlight and burnt himself with it ten times during the night...")

        builder.add("vampirism.vamp_6.page.1", "\n" +
                "\\\n" +
                "\\\n...first walk in the sun after his rebirth brought him to bloody tears, he felt his blood burning, but no longer instantly...\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ...he needed more strength, and extinguishing creatures of pure fire was his solution... ...twenty died.")

        builder.add("vampirism.vamp_7.page.1", "\n" +
                "\\\n...he could smash solid stone, but bound to the earth, however fast, he was still limited...\n" +
                "\\\n" +
                "\\\n ...he called in Her once more, repeating the rite of his rebirth..." +
                "\\\n" +
                "\\\n ...gifted Her a flower, the color of the blood She so craves...")

        builder.add("vampirism.vamp_8.page.1", "\n" +
                "\\\n" +
                "\\\n...he smiled, a rare event, when he told me of his first flight...\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ...he flew from village to village, until he knew the full extent of his domain, there was now nowhere he could not go...")

        builder.add("vampirism.vamp_9.page.1", "\n" +
                "\\\n...the weak minded would now not only let him drink his fill, but would also follow like faithful hounds...\n" +
                "\\\n" +
                "\\\n" +
                "\\\n ...horror of all horrors, he lured five of them to specially prepared iron cages, topped with wood and with a gap at the front. He sealed them inside...")

        builder.add("vampirism.vamp_9.page.2", "\n" +
                "\\\n" +
                "\\\n" +
                "\\\n...he began feeding from each of them; mesmerising them first, then carefully he drank al he could without damaging any...\n" +
                "\\\n")

        builder.add("vampirism.vamp_9.page.3", "\n" +
                "At last he knew his blood was strong enough to replicate what She had done for him... ...left me weak, close to oblivion but I watched him fill a glass goblet and hand it to me... ...we both sat next to a coffin, far from the sun's gaze, \"drink\" is all he said...")


        builder.add("book.witchery.guidebook.vampirism.armor.name", "Being Dapper")
        builder.add("book.witchery.guidebook.vampirism.armor.description", "Suit Up!")

        builder.add("vampirism.armor.title", "Vampiric Attire")
        builder.add("vampirism.armor.page.1", "\n" +
            "\\\n" +
            "Increased comfort will make all the difference. Use Bone Needle to stain wool"
        )

        builder.add("vampirism.armor.woven_cruor", "Woven Cruor")
        builder.add("vampirism.armor.top_hat", "Top Hat")
        builder.add("vampirism.armor.top_hat.text", "*description*")
        builder.add("vampirism.armor.dress_coat", "Dress Coat")
        builder.add("vampirism.armor.dress_coat.text", "*description*")
        builder.add("vampirism.armor.trousers", "Trousers")
        builder.add("vampirism.armor.trousers.text", "*description*")
        builder.add("vampirism.armor.oxford_boots", "Oxford Boots")
        builder.add("vampirism.armor.oxford_boots.text", "*description*")
    }
}