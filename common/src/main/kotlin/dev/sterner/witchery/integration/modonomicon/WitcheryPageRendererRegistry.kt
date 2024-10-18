package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.book.page.BookPage
import com.klikli_dev.modonomicon.client.render.page.PageRendererRegistry
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import net.minecraft.resources.ResourceLocation

object WitcheryPageRendererRegistry {

    val CAULDRON_RECIPE: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "cauldron_crafting_recipe")

    val CAULDRON_BREWING_RECIPE: ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(Witchery.MODID, "cauldron_brewing_recipe")


    fun register(){
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
    }
}