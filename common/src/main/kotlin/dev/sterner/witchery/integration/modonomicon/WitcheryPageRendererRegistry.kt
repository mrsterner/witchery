package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.api.ModonomiconConstants
import com.klikli_dev.modonomicon.book.page.BookPage
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.recipe.oven.OvenCookingRecipe
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import net.minecraft.resources.ResourceLocation

object WitcheryPageRendererRegistry {

    val CAULDRON_RECIPE: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "cauldron_crafting_recipe")

    val CAULDRON_BREWING_RECIPE: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "cauldron_brewing_recipe")

    val OVEN_FUMING_RECIPE: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "oven_fuming_recipe")

    val DISTILLING_RECIPE: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "distilling_recipe")

    val RITUAL_RECIPE: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "ritual_recipe")

    fun register() {
        PageRendererRegistry.registerPageRenderer(
            CAULDRON_RECIPE
        ) { p: BookPage ->
            object :
                BookCauldronCraftingRecipePageRenderer<CauldronCraftingRecipe>(
                    p as BookCauldronCraftingRecipePage
                ) {
            }
        }
        PageRendererRegistry.registerPageRenderer(
            CAULDRON_BREWING_RECIPE
        ) { p: BookPage ->
            object :
                BookCauldronBrewingRecipePageRenderer<CauldronBrewingRecipe>(
                    p as BookCauldronBrewingRecipePage
                ) {
            }
        }
        PageRendererRegistry.registerPageRenderer(
            OVEN_FUMING_RECIPE
        ) { p: BookPage ->
            object :
                BookOvenFumingRecipePageRenderer<OvenCookingRecipe>(
                    p as BookOvenFumingRecipePage
                ) {
            }
        }

        PageRendererRegistry.registerPageRenderer(
            DISTILLING_RECIPE
        ) { p: BookPage ->
            object :
                BookDistillingRecipePageRenderer<DistilleryCraftingRecipe>(
                    p as BookDistillingRecipePage
                ) {
            }
        }

        PageRendererRegistry.registerPageRenderer(
            RITUAL_RECIPE
        ) { p: BookPage ->
            object :
                BookRitualRecipePageRenderer<RitualRecipe>(
                    p as BookRitualRecipePage
                ) {
            }
        }

        PageRendererRegistry.registerPageRenderer(
            Witchery.id("potion_model")
        ) { p: BookPage ->
            object :
                BookPotionPageRenderer(
                    p as BookPotionPage
                ) {
            }
        }
    }
}