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
        registry.addCategory(OVEN_COOKING_CATEGORY)
        registry.addCategory(RITUAL_CATEGORY)
        val manager: RecipeManager = registry.recipeManager

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_BREWING_RECIPE_TYPE.get())) {
            registry.addRecipe(CauldronBrewingEmiRecipe(recipe.id, recipe.value))
        }

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_RECIPE_TYPE.get())) {
            registry.addRecipe(CauldronCraftingEmiRecipe(recipe.id, recipe.value))
        }

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.OVEN_RECIPE_TYPE.get())) {
            registry.addRecipe(OvenCookingEmiRecipe(recipe.id, recipe.value))
        }

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.RITUAL_RECIPE_TYPE.get())) {
            registry.addRecipe(RitualEmiRecipe(recipe.id, recipe.value))
        }
    }

    companion object {

        val ICON_CAULDRON: EmiStack = EmiStack.of(WitcheryItems.CAULDRON.get())
        val ICON_OVEN: EmiStack = EmiStack.of(WitcheryItems.IRON_WITCHES_OVEN.get())
        val ICON_RITUAL: EmiStack = EmiStack.of(WitcheryItems.RITUAL_CHALK.get())

        val CAULDRON_BREWING_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
            Witchery.id("cauldron_brewing"), ICON_CAULDRON
        )

        val CAULDRON_CRAFTING_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
            Witchery.id("cauldron_crafting"), ICON_CAULDRON
        )

        val OVEN_COOKING_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
            Witchery.id("oven_cooking"), ICON_OVEN
        )

        val RITUAL_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
            Witchery.id("ritual"), ICON_RITUAL
        )
    }
}