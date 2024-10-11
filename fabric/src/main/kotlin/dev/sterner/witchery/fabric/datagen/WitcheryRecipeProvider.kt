package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.recipe.CauldronBrewingRecipeBuilder
import dev.sterner.witchery.recipe.CauldronCraftingRecipeBuilder
import dev.sterner.witchery.recipe.ItemStackWithColor
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import java.util.concurrent.CompletableFuture

class WitcheryRecipeProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricRecipeProvider(output, registriesFuture) {

    override fun buildRecipes(exporter: RecipeOutput) {

        CauldronBrewingRecipeBuilder(
            listOf(
                ItemStackWithColor(
                    Items.APPLE.defaultInstance,
                    10257594,
                    0
                ),
                ItemStackWithColor(
                    Items.SUGAR.defaultInstance,
                    16755227,
                    1
                )
            ),
            Items.HONEY_BOTTLE.defaultInstance,
            100
        ).save(exporter)

        CauldronCraftingRecipeBuilder(
            listOf(
                ItemStackWithColor(
                    Items.RAW_IRON.defaultInstance,
                    123456,
                    0
                ),
                ItemStackWithColor(
                    Items.RAW_COPPER.defaultInstance,
                    654321,
                    1
                ),
                ItemStackWithColor(
                    Items.RAW_GOLD.defaultInstance,
                    321654,
                    2
                )
            ),
            listOf(Ingredient.of(Items.STICK)),
            100
        ).save(exporter)
    }
}