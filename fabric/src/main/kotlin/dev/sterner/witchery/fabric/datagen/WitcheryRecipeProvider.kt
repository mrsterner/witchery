package dev.sterner.witchery.fabric.datagen

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
        CauldronBrewingRecipeBuilder(
            listOf(
                ItemStackWithColor(Items.APPLE.defaultInstance, 10257594, 0),
                ItemStackWithColor(Items.SUGAR.defaultInstance, 16755227, 1)
            ),
            Items.HONEY_BOTTLE.defaultInstance,
            100
        ).save(exporter)

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, WitcheryItems.RITUAL_CHALK.get(), 2)
            .pattern("ATA")
            .pattern("AGA")
            .pattern("AGA")
            .define('A', WitcheryItems.WOOD_ASH.get())
            .define('T', WitcheryItems.TEAR_OF_THE_GODDESS.get())
            .define('G', WitcheryItems.GYPSUM.get())
            .unlockedBy("has_wood_ash", has(WitcheryItems.WOOD_ASH.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder(
            listOf(
                ItemStackWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, 123456, 0),
                ItemStackWithColor(Items.GOLD_NUGGET.defaultInstance, 654321, 1),
                ItemStackWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, 321654, 2)
            ),
            listOf(Ingredient.of(WitcheryItems.GOLDEN_CHALK.get())),
            100
        )
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder(
            listOf(
                ItemStackWithColor(Items.NETHER_WART.defaultInstance, 123456, 0),
                ItemStackWithColor(WitcheryItems.TEAR_OF_THE_GODDESS.get().defaultInstance, 654321, 1),
                ItemStackWithColor(Items.ENDER_PEARL.defaultInstance, 321654, 2),
                ItemStackWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, 654321, 3)
            ),
            listOf(Ingredient.of(WitcheryItems.OTHERWHERE_CHALK.get())),
            100
        )
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder(
            listOf(
                ItemStackWithColor(Items.NETHER_WART.defaultInstance, 123456, 0),
                ItemStackWithColor(Items.BLAZE_POWDER.defaultInstance, 654321, 1),
                ItemStackWithColor(WitcheryItems.RITUAL_CHALK.get().defaultInstance, 654321, 2)
            ),
            listOf(Ingredient.of(WitcheryItems.INFERNAL_CHALK.get())),
            100
        )
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.RITUAL_CHALK.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder(
            listOf(
                ItemStackWithColor(WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, 123456, 0),
                ItemStackWithColor(WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get().defaultInstance, 654321, 1),
                ItemStackWithColor(Items.EGG.defaultInstance, 654321, 2)
            ),
            listOf(
                Ingredient.of(WitcheryItems.MUTANDIS.get()),
                Ingredient.of(WitcheryItems.MUTANDIS.get()),
                Ingredient.of(WitcheryItems.MUTANDIS.get()),
                Ingredient.of(WitcheryItems.MUTANDIS.get())),
            100
        )
            .unlockedBy("has_ritual_chalk", has(WitcheryItems.MANDRAKE_ROOT.get()))
            .save(exporter)

        CauldronCraftingRecipeBuilder(
            listOf(
                ItemStackWithColor(Items.NETHER_WART.defaultInstance, 123456, 0),
                ItemStackWithColor(WitcheryItems.MUTANDIS.get().defaultInstance, 654321, 1)
            ),
            listOf(
                Ingredient.of(WitcheryItems.MUTANDIS_EXTREMIS.get())),
            100
        )
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
        RitualRecipeBuilder(
            listOf(Items.STICK.defaultInstance),
            listOf(EntityType.SHEEP),

            listOf(Items.DIAMOND.defaultInstance),

            listOf(EntityType.COW),
            100,
            setOf(RitualHelper.CommandType("", RitualHelper.CommandType.TICK)),
            false,
            false,
            20 * 10

        ).save(exporter)
    }
}