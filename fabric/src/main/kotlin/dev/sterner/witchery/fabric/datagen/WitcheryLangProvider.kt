package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.registry.*
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

class WitcheryLangProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricLanguageProvider(dataOutput, registryLookup) {

    fun formatId(id: ResourceLocation): String {
        val name = id.path.split('.').last()

        val exceptions = setOf("of", "the", "and", "in", "for", "on", "to")

        return name.split('_')
            .joinToString(" ") { word ->
                if (word in exceptions) {
                    word.lowercase()
                } else {
                    word.replaceFirstChar { it.uppercase() }
                }
            }
    }

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

        builder.add("trinkets.slot.chest.charm", "Charm")
        builder.add("trinkets.slot.legs.poppet", "Poppet")

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

        builder.add("witchery.captured.silverfish", "Silverfish")
        builder.add("witchery.captured.slime", "Slime")
        builder.add("witchery.captured.bat", "Bat")

        for (special in WitcherySpecialPotionEffects.SPECIALS.entrySet()) {
            builder.add("witchery:${special.value.id.path}", formatId(special.value.id))
        }

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
        builder.add(WitcheryItems.WITCHERY_POTION.get(), "Potion")
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
        builder.add(WitcheryItems.PARASITIC_LOUSE.get(), "Parasytic Louse")
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
        builder.add(WitcheryEntityTypes.SPECTRE.get(), "Spectre")
        builder.add(WitcheryEntityTypes.ELLE.get(), "Elle")
        builder.add(WitcheryEntityTypes.PARASITIC_LOUSE.get(), "Parasitic Louse")

        builder.add(WitcheryItems.CANE_SWORD.get(), "Cane Sword")
        builder.add(WitcheryBlocks.GLINTWEED.get(), "Glintweed")
        builder.add(WitcheryBlocks.GRASSPER.get(), "Grassper")
        builder.add(WitcheryBlocks.CRITTER_SNARE.get(), "Critter Snare")
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

        builder.add(WitcheryBlocks.TRENT_EFFIGY.get(), "Trent Effigy")
        builder.add(WitcheryBlocks.SCARECROW.get(), "Scarecrow")
        builder.add(WitcheryBlocks.WITCHS_LADDER.get(), "Witch's Ladder")

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
        builder.add(WitcheryItems.MOON_CHARM.get(), "Moon Charm")
        builder.add(WitcheryItems.WEREWOLF_ALTAR.get(), "Werewolf Altar")
        builder.add(WitcheryBlocks.WEREWOLF_ALTAR_COMPONENT.get(), "Werewolf Altar")
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


        builder.add("advancements.witchery.seeds.title.1", "The Start")
        builder.add("advancements.witchery.seeds.description", "Expensive on Etsy")
        builder.add("advancements.witchery.oven.title.1", "Smells nice")
        builder.add("advancements.witchery.oven.description", "In my gingerbread house")
        builder.add("advancements.witchery.cauldron.title.1", "A Stew is due")
        builder.add("advancements.witchery.cauldron.description", "Cookin'")
        builder.add("advancements.witchery.mutandis.title.1", "Lost plants")
        builder.add("advancements.witchery.mutandis.description", "CRISPR those flowers")
        builder.add("advancements.witchery.whiff_of_magic.title.1", "Wonder")
        builder.add("advancements.witchery.whiff_of_magic.description", "Unicorn farts")
        builder.add("advancements.witchery.chalk.title.1", "Rituals")
        builder.add("advancements.witchery.chalk.description", "And I started ritualing")
        builder.add("advancements.witchery.necromantic.title.1", "Necromantic")
        builder.add("advancements.witchery.necromantic.description", "No mind to break")
        builder.add("advancements.witchery.spirit_world.title.1", "The Spirit World")
        builder.add("advancements.witchery.spirit_world.description", "I have a dream")
        builder.add("advancements.witchery.disturbed.title.1", "The Disturbed")
        builder.add("advancements.witchery.disturbed.description", "Easy, just don't have a nightmare")

        builder.add("emi.category.witchery.brazier", "Brazier")
        builder.add("witchery:brazier_summoning/summon_banshee", "Summon Banshee")
        builder.add("witchery:brazier_summoning/summon_banshee.tooltip", "Summons a Banshee")
        builder.add("witchery:brazier_summoning/summon_spectre", "Summon Spectre")
        builder.add("witchery:brazier_summoning/summon_spectre.tooltip", "Summons a Spectre")

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
        builder.add("witchery:ritual/apply_ointment.tooltip", "When imbued into broom-wood, the charm awakens. The air no longer resists, and the winds know thy name")


        builder.add("witchery:ritual/summon_witch", "Summon Witch")
        builder.add("witchery:ritual/summon_witch.tooltip", "Summon a Witch at ritual center")
        builder.add("witchery:ritual/summon_demon", "Summon Demon")
        builder.add("witchery:ritual/summon_demon.tooltip", "Summon a Demon at ritual center")
        builder.add("witchery:ritual/summon_wither", "Summon Wither")
        builder.add("witchery:ritual/summon_wither.tooltip", "Summon a Wither at ritual center")


        builder.add("witchery:ritual/necro_stone", "Necromantic Stone")
        builder.add("witchery:ritual/necro_stone.tooltip", "A stone as cold as the grave and thrice as silent. It is not carved, but called—formed beneath moon-lit skies where the veil is thin. Death clings to it like moss to tombstone.")

        builder.add("witchery:ritual/bind_spectral_creatures", "Bind Spectral Creatures")
        builder.add("witchery:ritual/bind_spectral_creatures.tooltip", "Binds spectral being to Effigies. Multiple binds might yield unusual effects.")
        builder.add("witchery:ritual/bind_familiar", "Bind Familiar")
        builder.add("witchery:ritual/bind_familiar.tooltip", "Binds a familiar to the Player. A Familiar might aid in certain prospects of a witches life.")
        builder.add("witchery:ritual/manifestation", "Rite of Manifestation")
        builder.add("witchery:ritual/manifestation.tooltip", "Allows you to pass through spirit portals")
        builder.add("witchery:ritual/resurrect_familiar", "Resurrect Familiar")
        builder.add("witchery:ritual/resurrect_familiar.tooltip", "A bound familiar will be revived")

        //GUIDEBOOK GENERAL
        builder.add("book.witchery.guidebook.general.name", "General")
        builder.add("book.witchery.guidebook.brewing.name", "Brewing")
        builder.add("book.witchery.guidebook.ritual.name", "Rituals")
        builder.add("book.witchery.guidebook.potions.name", "Potions")


        builder.add("book.witchery.guidebook.general.beginning.name", "Beginning")
        builder.add("book.witchery.guidebook.general.beginning.description", "The roots of the Craft")
        builder.add("general.beginning.title.1", "The First Steps")
        builder.add("general.beginning.page.1", "All witchery begins with the green. Break thee the wild grass, and the earth shall gift thee seeds most curious and rare.")

        builder.add("book.witchery.guidebook.general.cauldron.name", "Cauldron")
        builder.add("book.witchery.guidebook.general.cauldron.description", "A watched pot never boils")
        builder.add("general.cauldron.title.1", "The Cauldron")
        builder.add("general.cauldron.page.1", "A vessel most vital for the crafting of Brews, Potions, and Witching Wares. Must be filled with Water and set Aflame. Often require an Altar nearby. A pinch of Wood Ash will cleanse its humours.")
        builder.add("general.cauldron.title.2", "")
        builder.add("general.cauldron.page.2", "")
        builder.add("brewing.cauldron.title.2", "")
        builder.add("brewing.cauldron.page.2", "")

        builder.add("book.witchery.guidebook.general.oven.name", "Witch's Oven")
        builder.add("book.witchery.guidebook.general.oven.description", "Smoke, soot, and secrets")
        builder.add("general.oven.title.1", "Witch's Oven")
        builder.add("general.oven.page.1", "By fire and fume, the Oven doth turn Saplings to Essences. With Jars in place, the Spirits are caught, else they vanish into the aether.")

        builder.add("book.witchery.guidebook.general.distillery.name", "Distillery")
        builder.add("book.witchery.guidebook.general.distillery.description", "Boil and bubble")
        builder.add("general.distillery.title.1", "The Distillery")
        builder.add("general.distillery.page.1", "Herein lies the art of separation. By fire’s kiss and alchemical glass, one may draw forth the true soul of a mixture, layer by layer.")

        builder.add("general.mutandis", "")
        builder.add("general.mutating_spring", "")

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
        builder.add("general.mutandis.title.1", "Mutandis")
        builder.add("general.mutandis.page.1", "Evil Scrambled Eggs. Used on Plants to mutate them into other plants, the mutations will be locked to the same type of plant for a few seconds on use, so for example saplings will turn into other saplings if used continually on the same sapling.")

        builder.add("book.witchery.guidebook.general.whiff_of_magic.name", "Whiff of Magic")
        builder.add("book.witchery.guidebook.general.whiff_of_magic.description", "Smells like potpourri")
        builder.add("general.whiff_of_magic.title.1", "Whiff of Magic")
        builder.add("general.whiff_of_magic.page.1", "Smells like potpourri")

        builder.add("book.witchery.guidebook.general.exhale_of_the_horned_one.name", "Exhale of the Horned One")
        builder.add("book.witchery.guidebook.general.exhale_of_the_horned_one.description", "Smells like mold")
        builder.add("general.exhale_of_the_horned_one.title.1", "Exhale of the Horned One")
        builder.add("general.exhale_of_the_horned_one.page.1", "This fume is born of the wild woods, where the Horned Lord treads. Burn the wood of oak or dark oak, and if the night be right, the smoke shall bear his mark. ")

        builder.add("book.witchery.guidebook.general.hint_of_rebirth.name", "Hint of Rebirth")
        builder.add("book.witchery.guidebook.general.hint_of_rebirth.description", "Smells like lime")
        builder.add("general.hint_of_rebirth.title.1", "Hint of Rebirth")
        builder.add("general.hint_of_rebirth.page.1", "The faintest whisper of life renewed. Found only in the last breath of dying spruce or the smoulder of young jungle green.")

        builder.add("book.witchery.guidebook.general.breath_of_the_goddess.name", "Breath of the Goddess")
        builder.add("book.witchery.guidebook.general.breath_of_the_goddess.description", "Smells nice")
        builder.add("general.breath_of_the_goddess.title.1", "Breath of the Goddess")
        builder.add("general.breath_of_the_goddess.page.1", "A sacred fume drawn from the boughs of the Birch or Cherry, when touched by fire most gentle. 'Tis said the very breath of the Goddess herself lingers in its vapour.")

        builder.add("book.witchery.guidebook.general.tear_of_the_goddess.name", "Tear of the Goddess")
        builder.add(
            "book.witchery.guidebook.general.tear_of_the_goddess.description",
            "Better than gamer girl bathwater"
        )
        builder.add("general.tear_of_the_goddess.title.1", "Tear of the Goddess")
        builder.add("general.tear_of_the_goddess.page.1", "Distilled sorrow and soft mercy. The Tear is not wept in vain, but drawn from sacred blooms under moonlight, when the air is thick with longing.")

        //GUIDEBOOK BREWING
        builder.add("book.witchery.guidebook.brewing.cauldron.name", "Cauldron")
        builder.add("book.witchery.guidebook.brewing.cauldron.description", "A bubbling pot of wonders")
        builder.add("brewing.cauldron.title.1", "The Cauldron")
        builder.add("brewing.cauldron.page.1", "A vessel most vital for the crafting of Brews, Potions, and Witching Wares. Must be filled with Water and set o’er Flame. Oft’ it doth require an Altar nearby. A pinch of Wood Ash shall cleanse its humours. Should thy brew turn brown, beware—'tis likely spoiled, lest the hue be true to thine ingredients.")

        builder.add("book.witchery.guidebook.brewing.ritual_chalk.name", "Ritual Chalk")
        builder.add("book.witchery.guidebook.brewing.ritual_chalk.description", "Better than crayons")
        builder.add("brewing.ritual_chalk.title.1", "Ritual Chalk")
        builder.add("brewing.ritual_chalk.page.1", "The standard ritual chalk, no special alignment.")

        builder.add("book.witchery.guidebook.brewing.redstone_soup.name", "Redstone Soup")
        builder.add("book.witchery.guidebook.brewing.redstone_soup.description", "Got Soup?")
        builder.add("brewing.redstone_soup.title.1", "Redstone Soup")
        builder.add("brewing.redstone_soup.page.1", "Standard base for other soups.")

        builder.add("book.witchery.guidebook.brewing.ghost_of_the_light.name", "Ghost of the Light")
        builder.add("book.witchery.guidebook.brewing.ghost_of_the_light.description", "Ectoplasm goo")
        builder.add("brewing.ghost_of_the_light.title.1", "Ghost of the Light")
        builder.add("brewing.ghost_of_the_light", "")
        builder.add("brewing.ghost_of_the_light.ghost_of_the_light", "Ghost of the Light")
        builder.add("brewing.ghost_of_the_light.page.1", "A pale brew born of spirits lost to the light. Stir it well, for it whispers secrets from the shadows.")

        builder.add("book.witchery.guidebook.brewing.spirit_of_otherwhere.name", "Spirit of Otherwhere")
        builder.add("book.witchery.guidebook.brewing.spirit_of_otherwhere.description", "Enderman goo")
        builder.add("brewing.spirit_of_otherwhere.title.1", "Spirit of Otherwhere")
        builder.add("brewing.spirit_of_otherwhere.spirit_of_otherwhere", "Spirit of Otherwhere")
        builder.add("brewing.spirit_of_otherwhere.page.1", "A strange concoction that binds the essence of those who roam beyond. Drink deep, and feel the weight of distant worlds.")

        builder.add("book.witchery.guidebook.brewing.flying_ointment.name", "Flying Ointment")
        builder.add("book.witchery.guidebook.brewing.flying_ointment.description", "Flying goo")
        builder.add("brewing.flying_ointment.title.1", "Flying Ointment")
        builder.add("brewing.flying_ointment.flying_ointment", "Flying Ointment")
        builder.add("brewing.flying_ointment.page.1", "An oil of wind and wings. Imbue your broom and soar high.")

        builder.add("brewing.redstone_soup.redstone_soup", "Redstone Soup")
        builder.add("brewing.ritual_chalk.golden_chalk", "Golden Chalk")
        builder.add("brewing.ritual_chalk.infernal_chalk", "Infernal Chalk")
        builder.add("brewing.ritual_chalk.otherwhere_chalk", "Otherwhere Chalk")

        //GUIDEBOOK RITUALS
        builder.add("book.witchery.guidebook.ritual.ritual_chalk.name", "Ritual Chalk")
        builder.add("book.witchery.guidebook.ritual.ritual_chalk.description", "Better than crayons")
        builder.add("ritual.ritual_chalk.title.1", "Ritual Chalk")
        builder.add("ritual.ritual_chalk.page.1", "The standard ritual chalk, no special alignment.")

        builder.add("book.witchery.guidebook.ritual.golden_chalk.name", "Golden Chalk")
        builder.add("book.witchery.guidebook.ritual.golden_chalk.description", "Better yellow than crayons")
        builder.add("ritual.golden_chalk.title.1", "Golden Chalk")
        builder.add("ritual.golden_chalk.page.1", "The center block in a ritual circle, surround with items either on ground or on a Grassper. Rightclick to activate the ritual")

        builder.add("book.witchery.guidebook.ritual.infernal_chalk.name", "Infernal Chalk")
        builder.add("book.witchery.guidebook.ritual.infernal_chalk.description", "Better red than crayons")
        builder.add("ritual.infernal_chalk.title.1", "Infernal Chalk")
        builder.add("ritual.infernal_chalk.page.1", "Infernal Chalk channels the power of the underworld.")

        builder.add("book.witchery.guidebook.ritual.otherwhere_chalk.name", "Otherwhere Chalk")
        builder.add("book.witchery.guidebook.ritual.otherwhere_chalk.description", "Better purple than crayons")
        builder.add("ritual.otherwhere_chalk.title.1", "Otherwhere Chalk")
        builder.add("ritual.otherwhere_chalk.page.1", "Imbued with the essence of distant realms.")


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
        builder.add("vampirism.vamp_1.title.1", "Observations of an Immortal")
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

        builder.add("vampirism.armor.title.1", "Vampiric Attire")
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


        builder.add("book.witchery.guidebook.vampirism.sun_collector.name", "Sunlight Collector")
        builder.add("vampirism.sun_collector.title.1", "Sunlight Collector")
        builder.add("vampirism.sun_collector", "Sunlight Collector")
        builder.add("vampirism.sun_collector.sunlight_collector", "")
        builder.add("vampirism.sun_collector.page.1", "Place in the sun and put a Quartz Sphere on it to collect the sunlight")
        builder.add("book.witchery.guidebook.vampirism.sun_collector.description", "Can't slay it? Steal it!")

        builder.add("book.witchery.guidebook.vampirism.name", "Vampirism")
        builder.add("book.witchery.guidebook.vampirism.cane.name", "Cane Sword")
        builder.add("vampirism.cane.title.1", "Cane Sword")
        builder.add("vampirism.cane.cane", "")
        builder.add("vampirism.cane.page.1", "Can collect blood with each hit, stored in the cane to be drunk whenever. ")
        builder.add("book.witchery.guidebook.vampirism.cane.description", "Slash n' suck")

        builder.add("book.witchery.guidebook.general.fume_expansion.title.1", "Fume Extension Funnel")
        builder.add("general.fume_expansion.title.1", "Fume Extension Funnel")
        builder.add("general.fume_expansion.page.1", "With a maximum of two, one on each side of the Oven, this will increase the output chance of Fumes")
        builder.add("general.fume_expansion", "Fume Extension Funnel")

        builder.add("book.witchery.guidebook.brewing.brew_of_raising.name", "Brew of Raising")
        builder.add("brewing.brew_of_raising.title.1", "Brew of Raising")
        builder.add("brewing.brew_of_raising", "Brew of Raising")
        builder.add("brewing.brew_of_raising.page.1", "Raises one dead from the dirt")
        builder.add("book.witchery.guidebook.brewing.brew_of_raising.description", "Raise the dead")

        builder.add("book.witchery.guidebook.brewing.brew_of_love.name", "Brew of Love")
        builder.add("brewing.brew_of_love.title.1", "Brew of Love")
        builder.add("brewing.brew_of_love", "Brew of Love")
        builder.add("brewing.brew_of_love.page.1", "Makes Animals go into spontaneous labour")
        builder.add("book.witchery.guidebook.brewing.brew_of_love.description", "A little sus")

        builder.add("book.witchery.guidebook.brewing.brew_of_wasting.name", "Brew of Wasting")
        builder.add("brewing.brew_of_wasting.title.1", "Brew of Wasting")
        builder.add("brewing.brew_of_wasting", "Brew of Wasting")
        builder.add("brewing.brew_of_wasting.page.1", "Kills Plants")
        builder.add("book.witchery.guidebook.brewing.brew_of_wasting.description", "Forgot to water the plants")

        builder.add("book.witchery.guidebook.brewing.brew_of_the_depths.name", "Brew of the Depths")
        builder.add("brewing.brew_of_the_depths.title.1", "Brew of the Depths")
        builder.add("brewing.brew_of_the_depths", "Brew of the Depths")
        builder.add("brewing.brew_of_the_depths.page.1", "Not implemented yet")
        builder.add("book.witchery.guidebook.brewing.brew_of_the_depths.description", "Not implemented yet")

        builder.add("book.witchery.guidebook.brewing.brew_of_frost.name", "Brew of Frost")
        builder.add("brewing.brew_of_frost.title.1", "Brew of Frost")
        builder.add("brewing.brew_of_frost", "Brew of Frost")
        builder.add("brewing.brew_of_frost.page.1", "Freezes water and makes entities chilly")
        builder.add("book.witchery.guidebook.brewing.brew_of_frost.description", "Icy Ice Baby")

        builder.add("book.witchery.guidebook.brewing.brew_of_revealing.name", "Brew of Revealing")
        builder.add("brewing.brew_of_revealing.title.1", "Brew of Revealing")
        builder.add("brewing.brew_of_revealing", "Brew of Revealing")
        builder.add("brewing.brew_of_revealing.page.1", "Used to remove Invisibility effects on entities. Makes Spectral Creatures easier to see.")
        builder.add("book.witchery.guidebook.brewing.brew_of_revealing.description", "Illegally Blind")

        builder.add("book.witchery.guidebook.brewing.brew_of_ink.name", "Brew of Ink")
        builder.add("brewing.brew_of_ink.title.1", "Brew of Ink")
        builder.add("brewing.brew_of_ink", "Brew of Ink")
        builder.add("brewing.brew_of_ink.page.1", "Applies Blindness.")
        builder.add("book.witchery.guidebook.brewing.brew_of_ink.description", "Legally Blind")

        builder.add("book.witchery.guidebook.brewing.brew_of_sleeping.name", "Brew of Sleeping")
        builder.add("brewing.brew_of_sleeping.title.1", "Brew of Sleeping")
        builder.add("brewing.brew_of_sleeping", "Brew of Sleeping")
        builder.add("brewing.brew_of_sleeping.page.1", "Enter the Spirit world in your dreams, beware of your surroundings and to avoid a nightmare, use dream weavers, wispy cotton and flowing spirit nearby.")
        builder.add("book.witchery.guidebook.brewing.brew_of_sleeping.description", "Surely a good idea")

        builder.add("book.witchery.guidebook.brewing.brew_of_flowing_spirit.name", "Brew of Flowing Spirit")
        builder.add("brewing.brew_of_flowing_spirit.title.1", "Brew of Flowing Spirit")
        builder.add("brewing.brew_of_flowing_spirit", "Brew of Flowing Spirit")
        builder.add("brewing.brew_of_flowing_spirit.page.1", "Can only be made in the Spirit World. Throw to make a fluid which applies regeneration to anyone bathing in it. Throw it on double doors in the Spirit World to make a Spirit Portal. Rite of Manifestation required to enter it")
        builder.add("book.witchery.guidebook.brewing.brew_of_flowing_spirit.description", "Good for Hot Springs")

        builder.add("book.witchery.guidebook.spirit_world.brew_of_flowing_spirit.name", "Brew of Flowing Spirit")
        builder.add("spirit_world.brew_of_flowing_spirit.title.1", "Brew of Flowing Spirit")
        builder.add("spirit_world.brew_of_flowing_spirit", "Brew of Flowing Spirit")
        builder.add("spirit_world.brew_of_flowing_spirit.page.1", "Can only be made in the Spirit World. Throw to make a fluid which applies regeneration to anyone bathing in it. Throw it on double doors in the Spirit World to make a Spirit Portal. Rite of Manifestation required to enter it")
        builder.add("book.witchery.guidebook.spirit_world.brew_of_flowing_spirit.description", "Good for Hot Springs")



        builder.add("book.witchery.guidebook.spirit_world.name", "The Spirit World")

        builder.add("book.witchery.guidebook.spirit_world.hunger.name", "Mellifluous Hunger")
        builder.add("book.witchery.guidebook.spirit_world.hunger.description", "Kill the Nightmare")
        builder.add("spirit_world.hunger.title.1", "Mellifluous Hunger")
        builder.add("spirit_world.hunger.page.1", "Kill your Nightmare to acquire, can be brought between dream and reality.")

        builder.add("book.witchery.guidebook.spirit_world.disturbed_cotton.name", "Disturbed Cotton")
        builder.add("book.witchery.guidebook.spirit_world.disturbed_cotton.description", "Something is off")
        builder.add("spirit_world.disturbed_cotton.title.1", "Disturbed Cotton")
        builder.add("spirit_world.disturbed_cotton.page.1", "This nightmare warps and twists the Wispy Cotton to become... Disturbed..")

        builder.add("book.witchery.guidebook.spirit_world.wispy_cotton.name", "Wispy Cotton")
        builder.add("book.witchery.guidebook.spirit_world.wispy_cotton.description", "This is better")
        builder.add("spirit_world.wispy_cotton.title.1", "Wispy Cotton")
        builder.add("spirit_world.wispy_cotton.page.1", "A delicate weave from the fabric of dreams. To hold it is to hold the softness of slumber itself")

        builder.add("book.witchery.guidebook.spirit_world.dream_weaver_of_nightmares.name", "Dreamweaver of Nightmares")
        builder.add("book.witchery.guidebook.spirit_world.dream_weaver_of_nightmares.description", "Catch the bad")
        builder.add("spirit_world.dream_weaver_of_nightmares.title.1", "Nightmares")
        builder.add("spirit_world.dream_weaver_of_nightmares.page.1", "Born of shadow and fear, this weaver binds nightmares into the night. May it grant you peace when the dark calls.")

        builder.add("book.witchery.guidebook.spirit_world.dream_weaver_of_fleet_foot.name", "Dreamweaver of Fleet Foot")
        builder.add("book.witchery.guidebook.spirit_world.dream_weaver_of_fleet_foot.description", "Catch my Wind")
        builder.add("spirit_world.dream_weaver_of_fleet_foot.title.1", "Fleet Foot")
        builder.add("spirit_world.dream_weaver_of_fleet_foot.page.1", "Swift as the wind, this spirit will carry you swiftly through the waking world, like a whisper in the breeze.")

        builder.add("book.witchery.guidebook.spirit_world.dream_weaver_of_iron_arm.name", "Dreamweaver of Iron Arm")
        builder.add("book.witchery.guidebook.spirit_world.dream_weaver_of_iron_arm.description", "Sleep like a stone")
        builder.add("spirit_world.dream_weaver_of_iron_arm.title.1", "Iron Arm")
        builder.add("spirit_world.dream_weaver_of_iron_arm.page.1", "The iron arm of slumber. Rest as if your body were bound by steel, and awaken with unyielding strength.")

        builder.add("book.witchery.guidebook.spirit_world.dream_weaver_of_fasting.name", "Dreamweaver of Fasting")
        builder.add("book.witchery.guidebook.spirit_world.dream_weaver_of_fasting.description", "Hangry cinderella")
        builder.add("spirit_world.dream_weaver_of_fasting.title.1", "Fasting")
        builder.add("spirit_world.dream_weaver_of_fasting.page.1", "A strange brew for the soul, fasting in sleep will still the hunger within, leaving you calm when the dawn breaks")




        builder.add("book.witchery.guidebook.ritual.summon_spectral_pig.name", "Summon Spectral Familiar")
        builder.add("book.witchery.guidebook.ritual.summon_spectral_pig.description", "One little piggy")
        builder.add("ritual.summon_spectral_pig.title.1", "Summon Spectral Familiar")
        builder.add("ritual.summon_spectral_pig.page.1", "Summons a Spectral Pig which will drop Spectral Dust, a valuable resource")

        builder.add("book.witchery.guidebook.ritual.summon_imp.name", "Summon Imp")
        builder.add("book.witchery.guidebook.ritual.summon_imp.description", "Not implemented")
        builder.add("ritual.summon_imp.title.1", "Summon Imp")
        builder.add("ritual.summon_imp.page.1", "Not implemented")

        builder.add("book.witchery.guidebook.ritual.summon_demon.name", "Summon Demon")
        builder.add("book.witchery.guidebook.ritual.summon_demon.description", "Felt cute, summoned Satan")
        builder.add("ritual.summon_demon.title.1", "Summon Demon")
        builder.add("ritual.summon_demon.page.1", "Summons a fierce Demon which will drop a Demon Heart, an incredibly valuable resource")

        builder.add("book.witchery.guidebook.ritual.summon_wither.name", "Summon Wither")
        builder.add("book.witchery.guidebook.ritual.summon_wither.description", "Oh boy")
        builder.add("ritual.summon_wither.title.1", "Summon Wither")
        builder.add("ritual.summon_wither.page.1", "Its the wither what can i say")

        builder.add("book.witchery.guidebook.ritual.summon_witch.name", "Summon Witch")
        builder.add("book.witchery.guidebook.ritual.summon_witch.description", "Hey it's me!")
        builder.add("ritual.summon_witch.title.1", "Summon Witch")
        builder.add("ritual.summon_witch.page.1", "It's a witch")

        builder.add("book.witchery.guidebook.ritual.manifestation.name", "Rite of Manifestation")
        builder.add("book.witchery.guidebook.ritual.manifestation.description", "Look mom i'm a ghost")
        builder.add("ritual.manifestation.title.1", "Manifestation")
        builder.add("ritual.manifestation.page.1", "Enables a player to walk through a spirit portal")

        builder.add("book.witchery.guidebook.ritual.teleport_owner_to_waystone.name", "Rite of Teleportation")
        builder.add("book.witchery.guidebook.ritual.teleport_owner_to_waystone.description", "Time is money")
        builder.add("ritual.teleport_owner_to_waystone.title.1", "Teleportation")
        builder.add("ritual.teleport_owner_to_waystone.page.1", "Sends the user to the location of the waystone")

        builder.add("book.witchery.guidebook.ritual.infuse_otherwhere.name", "Otherwhere Infusion")
        builder.add("book.witchery.guidebook.ritual.infuse_otherwhere.description", "Tap into the enderman")
        builder.add("ritual.infuse_otherwhere.title.1", "Spirit of Otherwhere")
        builder.add("ritual.infuse_otherwhere.page.1", "User can teleport with a witches hand")

        builder.add("book.witchery.guidebook.ritual.infuse_light.name", "Light Infusion")
        builder.add("book.witchery.guidebook.ritual.infuse_light.description", "Now you see me-n't")
        builder.add("ritual.infuse_light.title.1", "Ghost of the Light")
        builder.add("ritual.infuse_light.page.1", "User can become invisible")

        builder.add("book.witchery.guidebook.ritual.apply_ointment.name", "Apply Ointment")
        builder.add("book.witchery.guidebook.ritual.apply_ointment.description", "Broom to ride")
        builder.add("ritual.apply_ointment.title.1", "Apply Ointment")
        builder.add("ritual.apply_ointment.page.1", "Infuse the Broom with flying powers")

        builder.add("book.witchery.guidebook.ritual.push_mobs.name", "Rite of Push Mobs")
        builder.add("book.witchery.guidebook.ritual.push_mobs.description", "Sashay Away")
        builder.add("ritual.push_mobs.title.1", "Rite of Push Mobs")
        builder.add("ritual.push_mobs.page.1", "Mobs will be pushed away from the ritual center")

        builder.add("book.witchery.guidebook.ritual.set_midnight.name", "Rite of Turn Midnight")
        builder.add("book.witchery.guidebook.ritual.set_midnight.description", "Sun't")
        builder.add("ritual.set_midnight.title.1", "Turn Midnight")
        builder.add("ritual.set_midnight.page.1", "Manipulate the celestial bodies to make day into night.")

        builder.add("book.witchery.guidebook.ritual.charge_attuned.name", "Rite of Charging")
        builder.add("book.witchery.guidebook.ritual.charge_attuned.description", "It's over 90..., exactly 3000")
        builder.add("ritual.charge_attuned.title.1", "Rite of Charging")
        builder.add("ritual.charge_attuned.page.1", "Charges an attuned stone, this charge is required by some recipes and rituals, can also replace Altar Power up to 3000.")

        builder.add("book.witchery.guidebook.ritual.necro_stone.name", "Necromatic Stone")
        builder.add("book.witchery.guidebook.ritual.necro_stone.description", "One step closer to the grave")
        builder.add("ritual.necro_stone.title.1", "Necromatic Stone")
        builder.add("ritual.necro_stone.page.1", "Stone but dead cool")

        builder.add("book.witchery.guidebook.ritual.rite_of_charging_infusion.name", "Rite of Charge Infusion")
        builder.add("book.witchery.guidebook.ritual.rite_of_charging_infusion.description", "Need more juice")
        builder.add("ritual.rite_of_charging_infusion.title.1", "Charge Infusion")
        builder.add("ritual.rite_of_charging_infusion.page.1", "Replenishes the infused players power during the duration of the rite.")


        builder.add("book.witchery.guidebook.ritual.bind_familiar.name", "Rite of Binding")
        builder.add("book.witchery.guidebook.ritual.bind_familiar.description", "Team")
        builder.add("ritual.bind_familiar.title.1", "Rite of Binding")
        builder.add("ritual.bind_familiar.page.1", "Cats, Owls and Frogs. All with unique powers while bound. Cats amplify rites and curses. Owls increases Broom manoeuvrability. Frogs increase chance of extra brewed potions.")

        builder.add("book.witchery.guidebook.ritual.bind_spectral_creatures.name", "Rite of Binding")
        builder.add("book.witchery.guidebook.ritual.bind_spectral_creatures.description", "Curse you")
        builder.add("ritual.bind_spectral_creatures.title.1", "Rite of Binding")
        builder.add("ritual.bind_spectral_creatures.page.1", "Binds spectral creatures like Spirit, Banshee, Spectre and Poltergeist to an Effigy, like a Scarecrow, Trent Effigy or Witch's Ladder.")

        builder.add("general.mutandis_extremis", "")
        builder.add("book.witchery.guidebook.general.mutandis_extremis.name", "Mutandis Extremis")
        builder.add("book.witchery.guidebook.general.mutandis_extremis.description", "Mutandis Extremis")
        builder.add("general.mutandis_extremis.title.1", "Mutandis Extremis")
        builder.add("general.mutandis_extremis.page.1", "Mutandis Extremis")

        builder.add("book.witchery.guidebook.general.mutating_spring.name", "Mutating Spring")
        builder.add("book.witchery.guidebook.general.mutating_spring.description", "Flesh and Plant")
        builder.add("general.mutating_spring.title.1", "Mutating Spring")
        builder.add("general.mutating_spring.page.1", "Can create Grasspers, Critter Snares, Wormwood, Parasytioc Lose and Owls. And convert some blocks into other.")


        builder.add("book.witchery.guidebook.general.wormwood.name", "Wormwood")
        builder.add("book.witchery.guidebook.general.wormwood.description", "Ghastly")
        builder.add("general.wormwood.title.1", "Wormwood")
        builder.add("general.wormwood.page.1", "Used mostly in necromancy and brazier.\n" +
                "\\\n" +
                "\\\n" +
                "Require Wheat, four Wispy Cotton and Four Water."
        )

        builder.add("book.witchery.guidebook.general.owl.name", "Owl")
        builder.add("book.witchery.guidebook.general.owl.description", "Ho-ho")
        builder.add("general.owl.title.1", "Owl")
        builder.add("general.owl.page.1", "\n" +
                "Familiar, quite stupid but grants better control over broom when bound.\n" +
                "\\\n" +
                "\\\n" +
                "Require three Mutandis Extremis, one Charges Attuned Stone, Water, Wolf, Cobweb and one Critter snare with a bat for each Owl."
        )

        builder.add("book.witchery.guidebook.general.parasitic_louse.name", "Parasytic Louse")
        builder.add("book.witchery.guidebook.general.parasitic_louse.description", "Ho-ho")
        builder.add("general.parasitic_louse.title.1", "Parasytic Louse")
        builder.add("general.parasitic_louse.page.1", "Can be picked up, has the ability to store and apply a Potion. Use potion on louse to apply it. has a chance to consume the potion on attacking a creature.\n" +
                "\\\n" +
                "\\\n" +
                "Require Two Mutandis, One Tongue of Dog, one Charged Attuned Stone and one Critter Snare with a Silverfish. Water and Lily pads."
        )

        builder.add("book.witchery.guidebook.general.grassper.name", "Grassper")
        builder.add("book.witchery.guidebook.general.grassper.description", "Organic item pedestal")
        builder.add("general.grassper.title.1", "Grassper")
        builder.add("general.grassper.page.1", "Can be used to hold items for rituals.\n" +
                "\\\n" +
                "\\\n" +
                "Require four grass, one chest and one water."
        )

        builder.add("book.witchery.guidebook.general.critter_snare.name", "Critter Snare")
        builder.add("book.witchery.guidebook.general.critter_snare.description", "Organic critter holder")
        builder.add("general.critter_snare.title.1", "Critter Snare")
        builder.add("general.critter_snare.page.1", "Small Slimes, Bats and Silverfish will be trapped in this plant.\n" +
                "\\\n" +
                "\\\n" +
                "Require four Alder Saplings, Water, Cobweb and a Zombie."
        )

        //Potion Book

        fun genPotionCapacity(name: String){
            builder.add("witchery.potion_crafting.$name", "Capacity: +%1\$d\nAltar Power: -%2\$d")
        }
        genPotionCapacity("mandrake")
        genPotionCapacity("tear_of_the_goddess")
        genPotionCapacity("amethyst")
        genPotionCapacity("heart_of_the_sea")
        genPotionCapacity("phantom_vapour")
        genPotionCapacity("nether_star")
        genPotionCapacity("pentacle")

        builder.add("book.witchery.guidebook.brewing.beginning_potions.name", "Custom Potions")
        builder.add("book.witchery.guidebook.brewing.beginning_potions.description", "For when you want more effects")
        builder.add("brewing.beginning_potions.title.1", "Custom Potions")
        builder.add("brewing.beginning_potions.title.2", "")
        builder.add("brewing.beginning_potions.page.1", "Custom potions are made in a lit Cauldron access to an Altar with sufficient power depending on the resources used. The first ingredient must be a Nether Wart. Other than Altar Power, Potions require Capacity to increase the amount of ingredients the potion can hold.")
        builder.add("brewing.beginning_potions.page.2", "Stronger ingredients require more altar power to successfully mix.")

        builder.add("book.witchery.guidebook.brewing.potion_capacity.name", "Potion Capacity")
        builder.add("book.witchery.guidebook.brewing.potion_capacity.description", "More room for more fun")
        builder.add("brewing.potion_capacity.title.1", "Potion Capacity")
        builder.add("brewing.potion_capacity.page.1", "After the Nether Wart has been added the potion starts without any capacity. And since most ingredients cost capacity, capacity modifiers should be added first, but not limited to first. Potent ingredients might require more than one capacity.")

        builder.add("book.witchery.guidebook.brewing.potion_effect.name", "Potion Effect")
        builder.add("book.witchery.guidebook.brewing.potion_effect.description", "More room for more fun")
        builder.add("brewing.potion_effect.title.1", "Potion Effect")
        builder.add("brewing.potion_effect.page.1", "Once the brew holds capacity, ingredients may bestow their magick. Each carries a tale, each a tincture. Choose wisely, for not all gifts are kind. Most potions needs to be thrown")

        builder.add("book.witchery.guidebook.brewing.potion_effect_type.title.1", "Effect Type")
        builder.add("brewing.potion_effect_type.title.1", "Effect Type")
        builder.add("brewing.potion_effect_type.page.1", "Modifies the behaviour of potions")

        builder.add("potion_effect.blaze_powder", "Brings strength like fire, fierce and fleeting.")
        builder.add("potion_effect.blaze_powder.title.1", "Strength")

        builder.add("potion_effect.glistering_melon_slice", "Restores the flesh with golden sweetness.")
        builder.add("potion_effect.glistering_melon_slice.title.1", "Healing")

        builder.add("potion_effect.spider_eye", "A cursed touch, brings poison with a glance.")
        builder.add("potion_effect.spider_eye.title.1", "Poison")

        builder.add("potion_effect.ghast_tear", "Tear of the damned")
        builder.add("potion_effect.ghast_tear.title.1", "Regeneration")

        builder.add("potion_effect.sugar", "Glucose induced hyperactivity")
        builder.add("potion_effect.sugar.title.1", "Speed")

        builder.add("potion_effect.golden_carrot", "See")
        builder.add("potion_effect.golden_carrot.title.1", "Night Vision")

        builder.add("potion_effect.gold_nugget", "I fancy a sneaky potion")
        builder.add("potion_effect.gold_nugget.title.1", "Hide Particles")

        builder.add("potion_effect.fermented_spider_eye", "Good to bad and bad to good")
        builder.add("potion_effect.fermented_spider_eye.title.1", "Inverse Effect")

        builder.add("potion_effect.rowan_berries", "Taste too good!")
        builder.add("potion_effect.rowan_berries.title.1", "Fast Drinking")

        builder.add("potion_effect.exhale_of_the_horned_one", "Taste too good!")
        builder.add("potion_effect.exhale_of_the_horned_one.title.1", "Fast Drinking")

        builder.add("potion_effect.spanish_moss", "Taste too good!")
        builder.add("potion_effect.spanish_moss.title.1", "Fast Drinking")

        builder.add("potion_effect.glowstone_dust", "Extra Omph!")
        builder.add("potion_effect.glowstone_dust.title.1", "Amplify +1")

        builder.add("potion_effect.blaze_rod", "Extra Omph!")
        builder.add("potion_effect.blaze_rod.title.1", "Amplify +2")

        builder.add("potion_effect.attuned_stone", "Extra Omph!")
        builder.add("potion_effect.attuned_stone.title.1", "Amplify +4")

        builder.add("potion_effect.redstone", "Extra Long!")
        builder.add("potion_effect.redstone.title.1", "Mult 2")

        builder.add("potion_effect.obsidian", "Extra Long!")
        builder.add("potion_effect.obsidian.title.1", "Mult 4")

        builder.add("potion_effect.gunpowder", "")
        builder.add("potion_effect.gunpowder.title.1", "Splash")

        builder.add("potion_effect.cocoa_beans", "")
        builder.add("potion_effect.cocoa_beans.title.1", "Range Mod *2")

        builder.add("potion_effect.wispy_cotton", "")
        builder.add("potion_effect.wispy_cotton.title.1", "Range Mod *4")

        builder.add("potion_effect.belladonna_flower", "")
        builder.add("potion_effect.belladonna_flower.title.1", "Lingering Mod *2")

        builder.add("potion_effect.lapis_lazuli", "")
        builder.add("potion_effect.lapis_lazuli.title.1", "Lingering Mod *2")

        builder.add("potion_effect.end_stone", "")
        builder.add("potion_effect.end_stone.title.1", "Lingering Mod *4")

        builder.add("potion_effect.dragon_breath", "")
        builder.add("potion_effect.dragon_breath.title.1", "Lingering")

        builder.add("potion_effect.apple", "Harvests crops on Splash")
        builder.add("potion_effect.apple.title.1", "Harvest")

        builder.add("potion_effect.bone_meal", "Bone Meal effect on Splash")
        builder.add("potion_effect.bone_meal.title.1", "Fertile")

        builder.add("potion_effect.coal", "Puts out fire")
        builder.add("potion_effect.coal.title.1", "Extinguish")

        builder.add("potion_effect.dandelion", "Grow random flowers")
        builder.add("potion_effect.dandelion.title.1", "Grow Flowers")

        builder.add("potion_effect.dirt", "Tills all dirt")
        builder.add("potion_effect.dirt.title.1", "Till Land")

        builder.add("potion_effect.ender_dew", "Disables Endermen teleportation")
        builder.add("potion_effect.ender_dew.title.1", "Ender Inhibition")

        builder.add("potion_effect.lily_pad", "Grows Lilies on water")
        builder.add("potion_effect.lily_pad.title.1", "Grow Lily")

        builder.add("potion_effect.dead_bush", "Destroys all leaves")
        builder.add("potion_effect.dead_bush.title.1", "Prune Leaves")

        builder.add("potion_effect.sand", "Temporarily removes water")
        builder.add("potion_effect.sand.title.1", "Part Water")

        builder.add("potion_effect.wheat_seeds", "Plants seed items on the ground on tilled land")
        builder.add("potion_effect.wheat_seeds.title.1", "Plant Seeds")

        builder.add("potion_effect.wolfsbane", "Inhibits werewolf transformation")
        builder.add("potion_effect.wolfsbane.title.1", "Werewolf Lock")

        builder.add("potion_effect.string", "Harvests whole trees")
        builder.add("potion_effect.string.title.1", "Fell Tree")

        builder.add("potion_effect.cobblestone", "Temporarily removes lava")
        builder.add("potion_effect.cobblestone.title.1", "Part Lava")

        builder.add("potion_effect.ent_twig", "Grows a tree. Can pick sapling from inventory")
        builder.add("potion_effect.ent_twig.title.1", "Sprouting")

        builder.add("potion_effect.slime_ball", "Pulls mobs to the center")
        builder.add("potion_effect.slime_ball.title.1", "Pull")

        builder.add("potion_effect.stick", "Push mobs from the center")
        builder.add("potion_effect.stick.title.1", "Push")

        builder.add("potion_effect.ender_pearl", "Random Teleportation")
        builder.add("potion_effect.ender_pearl.title.1", "Teleport")

        builder.add("potion_effect.rose_bush", "Tames or Un-tames animals")
        builder.add("potion_effect.rose_bush.title.1", "Tame Animals")

        builder.add("potion_effect.poppy", "Puts animals into spontaneous labour")
        builder.add("potion_effect.poppy.title.1", "Love")

        builder.add("potion_effect.brown_mushroom", "Grow the entity in size")
        builder.add("potion_effect.brown_mushroom.title.1", "Grow")

        builder.add("potion_effect.red_mushroom", "Shrinks the entity in size")
        builder.add("potion_effect.red_mushroom.title.1", "Shrink")

        builder.add("potion_effect.witches_hat", "...")
        builder.add("potion_effect.witches_hat.title.1", "Summon Leonard")
    }
}