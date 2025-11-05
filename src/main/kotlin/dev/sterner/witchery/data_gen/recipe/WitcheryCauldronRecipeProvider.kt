package dev.sterner.witchery.data_gen.recipe

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.cauldron.CauldronBrewingRecipeBuilder
import dev.sterner.witchery.content.recipe.cauldron.CauldronCraftingRecipeBuilder
import dev.sterner.witchery.content.recipe.cauldron.CauldronInfusionRecipeBuilder
import dev.sterner.witchery.core.registry.WitcheryDataComponents
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.advancements.critereon.ItemPredicate
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.alchemy.Potions
import java.awt.Color
import java.util.*

object WitcheryCauldronRecipeProvider {

    fun cauldron(exporter: RecipeOutput) {

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MUTANDIS_EXTREMIS.get(), Color(255, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(95, 75, 10).rgb)
            .addInputWithColor(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), Color(80, 130, 250).rgb)
            .addInputWithColor(Items.GOLDEN_APPLE, Color(250, 250, 50).rgb)
            .addInputWithColor(WitcheryItems.TONGUE_OF_DOG.get(), Color(255, 80, 50).rgb)
            .addInputWithColor(Items.POISONOUS_POTATO, Color(255, 50, 100).rgb)
            .setOutput(WitcheryItems.BREW_OF_THE_GROTESQUE.get())
            .setAltarPower(500)
            .save(exporter, Witchery.id("brew_of_the_grotesque"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.POPPY, Color(255, 50, 50).rgb)
            .addInputWithColor(Items.GOLDEN_CARROT, Color(250, 250, 50).rgb)
            .addInputWithColor(Items.LILY_PAD, Color(50, 250, 50).rgb)
            .addInputWithColor(Items.COCOA_BEANS, Color(95, 75, 10).rgb)
            .addInputWithColor(WitcheryItems.WHIFF_OF_MAGIC.get(), Color(255, 150, 170).rgb)
            .addInputWithColor(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), Color(255, 110, 250).rgb)
            .setOutput(WitcheryItems.BREW_OF_LOVE.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("brew_of_love"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.COOKIE, Color(255, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.WHIFF_OF_MAGIC.get(), Color(255, 200, 210).rgb)
            .addInputWithColor(WitcheryItems.ICY_NEEDLE.get(), Color(80, 130, 210).rgb)
            .addInputWithColor(WitcheryItems.BREW_OF_LOVE.get(), Color(255, 150, 170).rgb)
            .addInputWithColor(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), Color(255, 110, 190).rgb)
            .setOutput(WitcheryItems.BREW_OF_SLEEPING.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("brew_of_sleeping"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.INK_SAC, Color(40, 40, 50).rgb)
            .addInputWithColor(Items.SLIME_BALL, Color(50, 200, 50).rgb)
            .addInputWithColor(WitcheryItems.BELLADONNA_FLOWER.get(), Color(195, 75, 110).rgb)
            .addInputWithColor(WitcheryItems.ROWAN_BERRIES.get(), Color(255, 50, 70).rgb)
            .addInputWithColor(WitcheryItems.OIL_OF_VITRIOL.get(), Color(50, 50, 50).rgb)
            .setOutput(WitcheryItems.BREW_OF_INK.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("brew_of_ink"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.CARROT, Color(250, 150, 50).rgb)
            .addInputWithColor(
                PotionContents.createItemStack(Items.POTION, Potions.NIGHT_VISION),
                Color(50, 20, 150).rgb
            )
            .addInputWithColor(Items.FERMENTED_SPIDER_EYE, Color(255, 50, 70).rgb)
            .addInputWithColor(WitcheryItems.ODOR_OF_PURITY.get(), Color(150, 50, 150).rgb)
            .setOutput(WitcheryItems.BREW_OF_REVEALING.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("brew_of_revealing"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.OIL_OF_VITRIOL.get(), Color(50, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.OIL_OF_VITRIOL.get(), Color(50, 20, 50).rgb)
            .addInputWithColor(WitcheryItems.GYPSUM.get(), Color(100, 100, 30).rgb)
            .addInputWithColor(Items.MAGMA_CREAM, Color(205, 110, 70).rgb)
            .addInputWithColor(WitcheryItems.BELLADONNA_FLOWER.get(), Color(255, 50, 170).rgb)
            .addInputWithColor(Items.DANDELION, Color(90, 110, 50).rgb)
            .setOutput(WitcheryItems.BREW_OF_EROSION.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("brew_of_erosion"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.WOOL_OF_BAT.get(), Color(150, 100, 20).rgb)
            .addInputWithColor(WitcheryItems.OIL_OF_VITRIOL.get(), Color(50, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.MUTANDIS.get(), Color(30, 110, 70).rgb)
            .addInputWithColor(Items.BONE, Color(255, 255, 220).rgb)
            .addInputWithColor(Items.REDSTONE, Color(220, 50, 50).rgb)
            .addInputWithColor(Items.ROTTEN_FLESH, Color(150, 30, 30).rgb)
            .setOutput(WitcheryItems.BREW_OF_RAISING.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("brew_of_raising"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.GLISTERING_MELON_SLICE, Color(155, 190, 190).rgb)
            .addInputWithColor(Items.SNOWBALL, Color(115, 160, 190).rgb)
            .addInputWithColor(Items.RED_MUSHROOM, Color(160, 150, 250).rgb)
            .addInputWithColor(WitcheryItems.ICY_NEEDLE.get(), Color(115, 160, 190).rgb)
            .addInputWithColor(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), Color(55, 220, 250).rgb)
            .addInputWithColor(WitcheryItems.ODOR_OF_PURITY.get(), Color(90, 50, 250).rgb)
            .setOutput(WitcheryItems.BREW_OF_FROST.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("brew_of_frost"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.LILY_PAD, Color(60, 230, 60).rgb)
            .addInputWithColor(Items.INK_SAC, Color(40, 40, 80).rgb)
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(160, 70, 30).rgb)
            .addInputWithColor(WitcheryItems.TEAR_OF_THE_GODDESS.get(), Color(115, 160, 190).rgb)
            .addInputWithColor(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), Color(55, 220, 250).rgb)
            .addInputWithColor(WitcheryItems.ODOR_OF_PURITY.get(), Color(90, 50, 250).rgb)
            .setOutput(WitcheryItems.BREW_OF_THE_DEPTHS.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("brew_of_the_depths"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.SUGAR, Color(230, 230, 230).rgb)
            .addInputWithColor(WitcheryItems.BREATH_OF_THE_GODDESS.get(), Color(200, 220, 255).rgb)
            .addInputWithColor(Items.GLOW_INK_SAC, Color(80, 180, 170).rgb)
            .addInputWithColor(WitcheryItems.ENDER_DEW.get(), Color(20, 120, 120).rgb)
            .addInputWithColor(WitcheryItems.WHIFF_OF_MAGIC.get(), Color(255, 188, 168).rgb)
            .setOutput(WitcheryItems.BREW_OF_OBLIVION.get())
            .setDimensionKey("minecraft:the_end")
            .setAltarPower(250)
            .save(exporter, Witchery.id("brew_of_oblivion"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.SPECTRAL_DUST.get(), Color(90, 160, 150).rgb)
            .addInputWithColor(Items.SOUL_SAND, Color(85, 65, 50).rgb)
            .addInputWithColor(WitcheryItems.PHANTOM_VAPOR.get(), Color(140, 120, 180).rgb)
            .addInputWithColor(WitcheryItems.ENDER_DEW.get(), Color(20, 120, 120).rgb)
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(160, 70, 30).rgb)
            .setOutput(WitcheryItems.BREW_OF_SOUL_SEVERANCE.get())
            .setAltarPower(200)
            .save(exporter, Witchery.id("brew_of_soul_severance"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MELLIFLUOUS_HUNGER.get(), Color(220, 70, 60).rgb)
            .addInputWithColor(WitcheryItems.EMBER_MOSS.get(), Color(220, 40, 80).rgb)
            .addInputWithColor(WitcheryItems.BELLADONNA_FLOWER.get(), Color(160, 70, 230).rgb)
            .addInputWithColor(Items.ROTTEN_FLESH, Color(215, 120, 90).rgb)
            .addInputWithColor(Items.POISONOUS_POTATO, Color(255, 220, 100).rgb)
            .addInputWithColor(Items.SPIDER_EYE, Color(220, 50, 50).rgb)
            .setOutput(WitcheryItems.BREW_OF_WASTING.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("brew_of_wasting"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.COBWEB, Color(220, 220, 220).rgb)
            .addInputWithColor(Items.DANDELION, Color(220, 220, 80).rgb)
            .addInputWithColor(Items.RED_MUSHROOM, Color(210, 70, 130).rgb)
            .addInputWithColor(WitcheryItems.WHIFF_OF_MAGIC.get(), Color(215, 220, 220).rgb)
            .addInputWithColor(WitcheryItems.WOOL_OF_BAT.get(), Color(205, 160, 100).rgb)
            .addInputWithColor(WitcheryItems.BELLADONNA_FLOWER.get(), Color(220, 220, 220).rgb)
            .setOutput(WitcheryItems.BREW_OF_WEBS.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("brew_of_webs"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.REDSTONE, Color(255, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.DROP_OF_LUCK.get(), Color(50, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.WOOL_OF_BAT.get(), Color(150, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.TONGUE_OF_DOG.get(), Color(255, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.BELLADONNA_FLOWER.get(), Color(255, 50, 170).rgb)
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(255, 50, 50).rgb)
            .setOutput(WitcheryItems.REDSTONE_SOUP.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("redstone_soup"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get(), Color(255, 50, 50).rgb)
            .addInputWithColor(Items.FEATHER, Color(250, 250, 250).rgb)
            .addInputWithColor(
                PotionContents.createItemStack(Items.POTION, Potions.SWIFTNESS),
                Color(125, 165, 250).rgb
            )
            .addInputWithColor(WitcheryItems.WOOL_OF_BAT.get(), Color(150, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.BELLADONNA_FLOWER.get(), Color(255, 180, 10).rgb)
            .setOutput(WitcheryItems.FLYING_OINTMENT.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("flying_ointment"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get(), Color(255, 50, 50).rgb)
            .addInputWithColor(Items.GOLDEN_CARROT, Color(250, 250, 250).rgb)
            .addInputWithColor(
                PotionContents.createItemStack(Items.POTION, Potions.NIGHT_VISION),
                Color(125, 165, 250).rgb
            )
            .addInputWithColor(Items.SPIDER_EYE, Color(150, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(255, 180, 10).rgb)
            .setOutput(WitcheryItems.HAPPENSTANCE_OIL.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("happenstance"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get(), Color(255, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.ATTUNED_STONE.get(), Color(255, 50, 250).rgb)
            .addInputWithColor(
                PotionContents.createItemStack(Items.POTION, Potions.REGENERATION),
                Color(125, 165, 250).rgb
            )
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(150, 50, 50).rgb)
            .addInputWithColor(Items.GOLDEN_APPLE, Color(180, 180, 0).rgb)
            .addInputWithColor(WitcheryItems.ROWAN_SAPLING.get(), Color(55, 250, 10).rgb)
            .setOutput(WitcheryItems.SOUL_OF_THE_WORLD.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("soul_of_the_world"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get(), Color(255, 50, 50).rgb)
            .addInputWithColor(
                PotionContents.createItemStack(Items.POTION, Potions.STRONG_HARMING),
                Color(225, 165, 50).rgb
            )
            .addInputWithColor(WitcheryItems.DEMON_HEART.get(), Color(255, 50, 20).rgb)
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(150, 50, 50).rgb)
            .addInputWithColor(Items.FERMENTED_SPIDER_EYE, Color(155, 150, 10).rgb)
            .addInputWithColor(WitcheryItems.REFINED_EVIL.get(), Color(255, 100, 10).rgb)
            .addInputWithColor(Items.BLAZE_ROD, Color(255, 50, 10).rgb)
            .setOutput(WitcheryItems.INFERNAL_ANIMUS.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("infernal_animus"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get(), Color(255, 50, 50).rgb)
            .addInputWithColor(
                PotionContents.createItemStack(Items.POTION, Potions.LONG_SWIFTNESS),
                Color(60, 165, 250).rgb
            )
            .addInputWithColor(Items.ENDER_EYE, Color(4, 250, 130).rgb)
            .addInputWithColor(Items.ENDER_EYE, Color(10, 250, 90).rgb)
            .addInputWithColor(WitcheryItems.DROP_OF_LUCK.get(), Color(50, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.WOOL_OF_BAT.get(), Color(255, 100, 255).rgb)
            .setOutput(WitcheryItems.SPIRIT_OF_OTHERWHERE.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("spirit_of_otherwhere"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get(), Color(255, 50, 50).rgb)
            .addInputWithColor(Items.TOTEM_OF_UNDYING, Color(230, 180, 30).rgb)
            .addInputWithColor(Items.BONE, Color(200, 200, 200).rgb)
            .addInputWithColor(WitcheryItems.MELLIFLUOUS_HUNGER.get(), Color(150, 50, 250).rgb)
            .addInputWithColor(WitcheryItems.NECROMANTIC_STONE.get(), Color(20, 255, 160).rgb)
            .setOutput(WitcheryItems.NECROMANTIC_SOULBIND.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("necromantic_soulbind"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.REDSTONE_SOUP.get(), Color(255, 50, 50).rgb)
            .addInputWithColor(
                PotionContents.createItemStack(Items.POTION, Potions.LONG_INVISIBILITY),
                Color(60, 165, 250).rgb
            )
            .addInputWithColor(
                PotionContents.createItemStack(Items.POTION, Potions.LONG_FIRE_RESISTANCE),
                Color(255, 160, 60).rgb
            )
            .addInputWithColor(WitcheryItems.EMBER_MOSS.get(), Color(255, 180, 90).rgb)
            .addInputWithColor(Items.TORCH, Color(250, 250, 50).rgb)
            .addInputWithColor(WitcheryItems.TONGUE_OF_DOG.get(), Color(200, 200, 255).rgb)
            .setOutput(WitcheryItems.GHOST_OF_THE_LIGHT.get())
            .setAltarPower(100)
            .save(exporter, Witchery.id("ghost_of_the_light"))

        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.FANCIFUL_THREAD.get(), Color(200, 150, 100).rgb)
            .addInputWithColor(WitcheryItems.SPANISH_MOSS.get(), Color(100, 200, 100).rgb)
            .addInputWithColor(WitcheryItems.WATER_ARTICHOKE_GLOBE.get(), Color(100, 100, 200).rgb)
            .addInputWithColor(WitcheryItems.GLINTWEED.get(), Color(250, 250, 50).rgb)
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(150, 100, 5).rgb)
            .addInputWithColor(WitcheryItems.WOOL_OF_BAT.get(), Color(80, 150, 255).rgb)
            .setOutput(WitcheryItems.BREW_FLOWING_SPIRIT.get())
            .setAltarPower(100)
            .setDimensionKey(setOf("witchery:dream_world", "witchery:nightmare_world"))
            .save(exporter, Witchery.id("brew_of_flowing_spirit"))


        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHERITE_SCRAP, Color(205, 125, 50).rgb)
            .addInputWithColor(WitcheryItems.OIL_OF_VITRIOL.get(), Color(50, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.DEMONS_BLOOD.get(), Color(255, 1, 1).rgb)
            .addOutput(WitcheryItems.PENTACLE.get())
            .setAltarPower(100)
            .unlockedBy("has_oil", has(WitcheryItems.OIL_OF_VITRIOL.get()))
            .save(exporter, Witchery.id("pentacle"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.CRIMSON_FUNGUS, Color(255, 55, 50).rgb)
            .addInputWithColor(WitcheryItems.ENT_TWIG.get(), Color(255, 100, 1).rgb)
            .addInputWithColor(WitcheryItems.MUTANDIS_EXTREMIS.get(), Color(255, 1, 1).rgb)
            .addOutput(WitcheryItems.MUTATING_SPRING.get())
            .setAltarPower(100)
            .unlockedBy("has_ent_twig", has(WitcheryItems.ENT_TWIG.get()))
            .save(exporter, Witchery.id("mutating_spring"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(100, 50, 50).rgb)
            .addInputWithColor(Items.GOLD_NUGGET, Color(255, 255, 50).rgb)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get(), Color(255, 255, 255).rgb)
            .addOutput(WitcheryItems.GOLDEN_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter, Witchery.id("golden_chalk"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.WARPED_FUNGUS, Color(255, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.TEAR_OF_THE_GODDESS.get(), Color(105, 50, 250).rgb)
            .addInputWithColor(Items.ENDER_PEARL, Color(50, 150, 150).rgb)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get(), Color(255, 255, 255).rgb)
            .addOutput(WitcheryItems.OTHERWHERE_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter, Witchery.id("otherwhere_chalk"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.CRIMSON_FUNGUS, Color(255, 50, 50).rgb)
            .addInputWithColor(Items.BLAZE_POWDER, Color(205, 200, 10).rgb)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get(), Color(200, 200, 200).rgb)
            .addOutput(WitcheryItems.INFERNAL_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter, Witchery.id("infernal_chalk"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(100, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get(), Color(100, 150, 50).rgb)
            .addInputWithColor(Items.EGG, Color(150, 150, 50).rgb)
            .addOutput(WitcheryItems.MUTANDIS.get(), 4)
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.MANDRAKE_ROOT.get()))
            .save(exporter, Witchery.id("mutandis"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.CRIMSON_FUNGUS, Color(255, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.MUTANDIS.get(), Color(155, 150, 50).rgb)
            .addOutput(WitcheryItems.MUTANDIS_EXTREMIS.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter, Witchery.id("mutandis_extremis"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(100, 50, 50).rgb)
            .addInputWithColor(Items.NETHER_WART, Color(255, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.TEAR_OF_THE_GODDESS.get(), Color(55, 50, 250).rgb)
            .addInputWithColor(WitcheryItems.REFINED_EVIL.get(), Color(20, 20, 20).rgb)
            .addInputWithColor(WitcheryItems.MUTANDIS_EXTREMIS.get(), Color(50, 20, 20).rgb)
            .addOutput(WitcheryItems.DROP_OF_LUCK.get())
            .setAltarPower(100)
            .unlockedBy("has_refined_evil", has(WitcheryItems.REFINED_EVIL.get()))
            .save(exporter, Witchery.id("drop_of_luck"))

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get(), Color(100, 50, 50).rgb)
            .addInputWithColor(WitcheryItems.TEAR_OF_THE_GODDESS.get(), Color(100, 150, 150).rgb)
            .addInputWithColor(WitcheryItems.PHANTOM_VAPOR.get(), Color(150, 150, 150).rgb)
            .addInputWithColor(Items.ENDER_PEARL, Color(50, 150, 150).rgb)
            .addInputWithColor(Items.WHEAT, Color(150, 150, 50).rgb)
            .addInputWithColor(WitcheryItems.MUTANDIS.get(), Color(150, 190, 50).rgb)
            .addOutput(Items.NETHER_WART, 1)
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.MANDRAKE_ROOT.get()))
            .save(exporter, Witchery.id("nether_wart"))

        val hagMiner = WitcheryItems.HAGS_RING.get().defaultInstance
        hagMiner.set(WitcheryDataComponents.HAG_RING_TYPE.get(), WitcheryDataComponents.HagType.MINER)

        CauldronInfusionRecipeBuilder.create()
            .setBrewInput(WitcheryItems.BREW_OF_EROSION.get())
            .setInfusionItem(WitcheryItems.GOLD_RING.get())
            .setOutput(hagMiner)
            .setAltarPower(5000)
            .unlockedBy("has_gold_ring", has(WitcheryItems.GOLD_RING.get()))
            .save(exporter, Witchery.id("hags_ring_infusion"))


        val hagLumber = WitcheryItems.HAGS_RING.get().defaultInstance
        hagLumber.set(WitcheryDataComponents.HAG_RING_TYPE.get(), WitcheryDataComponents.HagType.LUMBER)

        CauldronInfusionRecipeBuilder.create()
            .setBrewInput(WitcheryItems.BREW_OF_WASTING .get())
            .setInfusionItem(hagMiner)
            .setOutput(hagLumber)
            .setAltarPower(5000)
            .unlockedBy("has_hags_ring", has(WitcheryItems.HAGS_RING.get()))
            .save(exporter, Witchery.id("hags_ring_infusion_lumber"))
    }

    fun has(tag: Item): Criterion<InventoryChangeTrigger.TriggerInstance> {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tag))
    }

    fun has(tag: TagKey<Item>): Criterion<InventoryChangeTrigger.TriggerInstance> {
        return inventoryTrigger(ItemPredicate.Builder.item().of(tag))
    }

    fun inventoryTrigger(vararg builders: ItemPredicate.Builder): Criterion<InventoryChangeTrigger.TriggerInstance> {
        val predicates = builders.map { it.build() }.toTypedArray()
        return inventoryTrigger(*predicates)
    }

    fun inventoryTrigger(vararg predicates: ItemPredicate): Criterion<InventoryChangeTrigger.TriggerInstance> {
        return CriteriaTriggers.INVENTORY_CHANGED.createCriterion(
            InventoryChangeTrigger.TriggerInstance(
                Optional.empty(),
                InventoryChangeTrigger.TriggerInstance.Slots.ANY,
                predicates.toList()
            )
        )
    }


}