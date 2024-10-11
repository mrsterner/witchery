package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.world.item.crafting.RecipeManager


@EmiEntrypoint
class WitcheryEmiPlugin : EmiPlugin {

    override fun register(registry: EmiRegistry) {
        registry.addCategory(CAULDRON_BREWING_CATEGORY)
        registry.addCategory(CAULDRON_CRAFTING_CATEGORY)
        val manager: RecipeManager = registry.recipeManager

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_BREWING_RECIPE_TYPE.get())) {
            registry.addRecipe(CauldronBrewingEmiRecipe(recipe.id, recipe.value))
        }

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_RECIPE_TYPE.get())) {
            registry.addRecipe(CauldronCraftingEmiRecipe(recipe.id, recipe.value))
        }
    }

    companion object {

        val ICON: EmiStack = EmiStack.of(WitcheryItems.CAULDRON.get())

        val CAULDRON_BREWING_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
            Witchery.id("cauldron_brewing"), ICON
        )

        val CAULDRON_CRAFTING_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
            Witchery.id("cauldron_crafting"), ICON
        )
    }
}