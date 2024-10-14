package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.block.ritual.CommandContext
import dev.sterner.witchery.block.ritual.CommandType
import dev.sterner.witchery.block.ritual.RitualHelper
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipeBuilder
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipeBuilder
import dev.sterner.witchery.recipe.cauldron.ItemStackWithColor
import dev.sterner.witchery.recipe.oven.OvenCookingRecipeBuilder
import dev.sterner.witchery.recipe.ritual.RitualRecipeBuilder
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import java.util.concurrent.CompletableFuture

class WitcheryRecipeProvider(output: FabricDataOutput, val registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricRecipeProvider(output, registriesFuture) {

    override fun buildRecipes(exporter: RecipeOutput) {

        //TODO remove
        CauldronBrewingRecipeBuilder.create()
            .addInputWithColor(Items.APPLE.defaultInstance, 10257594)
            .addInputWithColor(Items.SUGAR.defaultInstance, 16755227)
            .setOutput(Items.HONEY_BOTTLE.defaultInstance)
            .setAltarPower(100)
            .save(exporter)


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.RITUAL_CHALK.get(), 2)
            .pattern("ATA")
            .pattern("AGA")
            .pattern("AGA")
            .define('A', WitcheryItems.WOOD_ASH.get())
            .define('T', WitcheryItems.TEAR_OF_THE_GODDESS.get())
            .define('G', WitcheryItems.GYPSUM.get())
            .unlockedBy("has_wood_ash", has(WitcheryItems.WOOD_ASH.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, 123456)
            .addInputWithColor(Items.GOLD_NUGGET.defaultInstance, 654321)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, 321654)
            .addOutput(Ingredient.of(WitcheryItems.GOLDEN_CHALK.get()))
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHER_WART.defaultInstance, 123456)
            .addInputWithColor(WitcheryItems.TEAR_OF_THE_GODDESS.get().defaultInstance, 654321)
            .addInputWithColor(Items.ENDER_PEARL.defaultInstance, 321654)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, 654321)
            .addOutput(WitcheryItems.OTHERWHERE_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHER_WART.defaultInstance, 123456)
            .addInputWithColor(Items.BLAZE_POWDER.defaultInstance, 654321)
            .addInputWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, 654321)
            .addOutput(WitcheryItems.INFERNAL_CHALK.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, 123456)
            .addInputWithColor(WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get().defaultInstance, 654321)
            .addInputWithColor(Items.EGG.defaultInstance, 654321)
            .addOutput(WitcheryItems.MUTANDIS.get())
            .addOutput(WitcheryItems.MUTANDIS.get())
            .addOutput(WitcheryItems.MUTANDIS.get())
            .addOutput(WitcheryItems.MUTANDIS.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.MANDRAKE_ROOT.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder.create()
            .addInputWithColor(Items.NETHER_WART.defaultInstance, 123456)
            .addInputWithColor(WitcheryItems.MUTANDIS.get().defaultInstance, 654321)
            .addOutput(WitcheryItems.MUTANDIS_EXTREMIS.get())
            .setAltarPower(100)
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.MANDRAKE_ROOT.get()))
            .save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.OAK_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.DARK_OAK_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.BIRCH_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.BREATH_OF_THE_GODDESS.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.CHERRY_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.BREATH_OF_THE_GODDESS.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.SPRUCE_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.HINT_OF_REBIRTH.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        OvenCookingRecipeBuilder(
            Ingredient.of(Items.JUNGLE_SAPLING),
            Ingredient.of(WitcheryItems.JAR.get()),
            WitcheryItems.WOOD_ASH.get().defaultInstance,
            WitcheryItems.HINT_OF_REBIRTH.get().defaultInstance,
            0.5f,
            0.5f,
            85
        ).save(exporter)

        //TODO remove
        RitualRecipeBuilder.create()
            .addInputItem(Items.STICK.defaultInstance)
            .addInputItem(WitcheryItems.WAYSTONE.get().defaultInstance)
            .addInputEntity(EntityType.SHEEP)
            .addOutputItem(Items.DIAMOND.defaultInstance)
            .addCommand(CommandType("kill {entity}", CommandType.END, CommandContext.ENTITY))
            .setTicks(20 * 5)
            .save(exporter)

        RitualRecipeBuilder.create()
            .addInputItem(WitcheryItems.WAYSTONE.get().defaultInstance)
            .addCommand(CommandType("tp {owner} {blockPos}", CommandType.END, CommandContext.BLOCKPOS))
            .setTicks(20)
            .save(exporter)

    }
}