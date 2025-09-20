package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.EmiEntrypoint
import dev.emi.emi.api.EmiPlugin
import dev.emi.emi.api.EmiRegistry
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.PendantDataComponentRecipe
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryRecipeTypes
import net.minecraft.world.item.crafting.*


@EmiEntrypoint
class WitcheryEmiPlugin : EmiPlugin {

    override fun register(registry: EmiRegistry) {
        registry.addWorkstation(
            BRAZIER_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.BRAZIER.get()))
        )
        registry.addWorkstation(
            CAULDRON_CRAFTING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_CRAFTING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_CRAFTING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.EXPOSED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_CRAFTING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WEATHERED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_CRAFTING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.OXIDIZED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_CRAFTING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_CRAFTING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_EXPOSED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_CRAFTING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_WEATHERED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_CRAFTING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_OXIDIZED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_BREWING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_BREWING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_BREWING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.EXPOSED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_BREWING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WEATHERED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_BREWING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.OXIDIZED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_BREWING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_BREWING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_EXPOSED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_BREWING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_WEATHERED_COPPER_CAULDRON.get()))
        )
        registry.addWorkstation(
            CAULDRON_BREWING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_OXIDIZED_COPPER_CAULDRON.get()))
        )

        registry.addWorkstation(
            OVEN_COOKING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.IRON_WITCHES_OVEN.get()))
        )
        registry.addWorkstation(
            OVEN_COOKING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.COPPER_WITCHES_OVEN.get()))
        )
        registry.addWorkstation(
            OVEN_COOKING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.EXPOSED_COPPER_WITCHES_OVEN.get()))
        )
        registry.addWorkstation(
            OVEN_COOKING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WEATHERED_COPPER_WITCHES_OVEN.get()))
        )
        registry.addWorkstation(
            OVEN_COOKING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.OXIDIZED_COPPER_WITCHES_OVEN.get()))
        )
        registry.addWorkstation(
            OVEN_COOKING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_COPPER_WITCHES_OVEN.get()))
        )
        registry.addWorkstation(
            OVEN_COOKING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get()))
        )
        registry.addWorkstation(
            OVEN_COOKING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get()))
        )
        registry.addWorkstation(
            OVEN_COOKING_CATEGORY,
            EmiIngredient.of(Ingredient.of(WitcheryItems.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get()))
        )

        registry.addWorkstation(RITUAL_CATEGORY, EmiIngredient.of(Ingredient.of(WitcheryItems.GOLDEN_CHALK.get())))
        registry.addWorkstation(DISTILLING_CATEGORY, EmiIngredient.of(Ingredient.of(WitcheryItems.DISTILLERY.get())))
        registry.addWorkstation(SPINNING_CATEGORY, EmiIngredient.of(Ingredient.of(WitcheryItems.SPINNING_WHEEL.get())))

        registry.addCategory(CAULDRON_BREWING_CATEGORY)
        registry.addCategory(CAULDRON_CRAFTING_CATEGORY)
        registry.addCategory(OVEN_COOKING_CATEGORY)
        registry.addCategory(RITUAL_CATEGORY)
        registry.addCategory(DISTILLING_CATEGORY)
        registry.addCategory(SPINNING_CATEGORY)
        registry.addCategory(BRAZIER_CATEGORY)

        val manager: RecipeManager = registry.recipeManager

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_BREWING_RECIPE_TYPE.get())) {
            registry.addRecipe(CauldronBrewingEmiRecipe(recipe.id, recipe.value))
        }

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_RECIPE_TYPE.get())) {
            registry.addRecipe(CauldronCraftingEmiRecipe(recipe.id, recipe.value))
        }

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.OVEN_RECIPE_TYPE.get())) {
            registry.addRecipe(OvenCookingEmiRecipe(recipe.id, recipe.value, null))
        }

        for (recipe in manager.getAllRecipesFor(RecipeType.SMOKING)) {
            registry.addRecipe(OvenCookingEmiRecipe(Witchery.id("/" + recipe.id.path.toString()), null, recipe.value))
        }

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.RITUAL_RECIPE_TYPE.get())) {
            registry.addRecipe(RitualEmiRecipe(recipe.id, recipe.value))
        }

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.DISTILLERY_RECIPE_TYPE.get())) {
            registry.addRecipe(DistillingEmiRecipe(recipe.id, recipe.value))
        }

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.SPINNING_WHEEL_RECIPE_TYPE.get())) {
            registry.addRecipe(SpinningEmiRecipe(recipe.id, recipe.value))
        }

        for (recipe in manager.getAllRecipesFor(WitcheryRecipeTypes.BRAZIER_SUMMONING_RECIPE_TYPE.get())) {
            registry.addRecipe(BrazierEmiRecipe(recipe.id, recipe.value))
        }

        for (recipe in getRecipes(registry, RecipeType.CRAFTING)) {
            if (recipe is PendantDataComponentRecipe) {
                registry.addRecipe(BloodPendantEmiRecipe(Witchery.id("/blood")))
                registry.addRecipe(SunPendantEmiRecipe(Witchery.id("/sun")))
            }
        }
    }

    private fun <C : RecipeInput?, T : Recipe<C>?> getRecipes(registry: EmiRegistry, type: RecipeType<T>): Iterable<T> {
        return Iterable {
            registry.recipeManager.getAllRecipesFor(type).stream().map { e: RecipeHolder<T> -> e.value() }
                .iterator()
        }
    }

    companion object {

        val ICON_CAULDRON: EmiStack = EmiStack.of(WitcheryItems.CAULDRON.get())
        val ICON_OVEN: EmiStack = EmiStack.of(WitcheryItems.IRON_WITCHES_OVEN.get())
        val ICON_RITUAL: EmiStack = EmiStack.of(WitcheryItems.RITUAL_CHALK.get())
        val ICON_DISTILLING: EmiStack = EmiStack.of(WitcheryItems.DISTILLERY.get())
        val ICON_SPINNING: EmiStack = EmiStack.of(WitcheryItems.SPINNING_WHEEL.get())
        val ICON_BRAZIER: EmiStack = EmiStack.of(WitcheryItems.BRAZIER.get())

        val BRAZIER_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
            Witchery.id("brazier"), ICON_BRAZIER
        )

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

        val DISTILLING_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
            Witchery.id("distilling"), ICON_DISTILLING
        )

        val SPINNING_CATEGORY: EmiRecipeCategory = EmiRecipeCategory(
            Witchery.id("spinning"), ICON_SPINNING
        )

    }
}