package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.CauldronBrewingRecipeBuilder
import dev.sterner.witchery.recipe.CauldronCraftingRecipeBuilder
import dev.sterner.witchery.recipe.ItemStackWithColor
import dev.sterner.witchery.registry.WitcheryItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import java.util.concurrent.CompletableFuture

class WitcheryRecipeProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) :
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

        //TODO remove
        CauldronCraftingRecipeBuilder(
            listOf(
                ItemStackWithColor(Items.RAW_IRON.defaultInstance, 123456, 0),
                ItemStackWithColor(Items.RAW_COPPER.defaultInstance, 654321, 1),
                ItemStackWithColor(Items.RAW_GOLD.defaultInstance, 321654, 2)
            ),
            listOf(Ingredient.of(Items.STICK)),
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
    }
}