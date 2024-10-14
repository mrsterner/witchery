package dev.sterner.witchery.recipe.oven

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient

class OvenCookingRecipeBuilder(
    private val ingredient: Ingredient,
    private val extraIngredient: Ingredient = Ingredient.EMPTY,
    private val result: ItemStack,
    private val extraOutput: ItemStack = ItemStack.EMPTY,
    private val extraOutputChance: Float = 0.0f,
    private val experience: Float = 0.0f,
    private val cookingTime: Int = 200
) : RecipeBuilder {

    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()
    private var group: String? = null

    fun requires(ingredient: Ingredient): OvenCookingRecipeBuilder {
        return OvenCookingRecipeBuilder(ingredient, extraIngredient, result, extraOutput, extraOutputChance, experience, cookingTime)
    }

    fun extraIngredient(extraIngredient: Ingredient): OvenCookingRecipeBuilder {
        return OvenCookingRecipeBuilder(ingredient, extraIngredient, result, extraOutput, extraOutputChance, experience, cookingTime)
    }

    fun result(result: ItemStack): OvenCookingRecipeBuilder {
        return OvenCookingRecipeBuilder(ingredient, extraIngredient, result, extraOutput, extraOutputChance, experience, cookingTime)
    }

    fun extraOutput(extraOutput: ItemStack, chance: Float): OvenCookingRecipeBuilder {
        return OvenCookingRecipeBuilder(ingredient, extraIngredient, result, extraOutput, chance, experience, cookingTime)
    }

    fun experience(exp: Float): OvenCookingRecipeBuilder {
        return OvenCookingRecipeBuilder(ingredient, extraIngredient, result, extraOutput, extraOutputChance, exp, cookingTime)
    }

    fun cookingTime(time: Int): OvenCookingRecipeBuilder {
        return OvenCookingRecipeBuilder(ingredient, extraIngredient, result, extraOutput, extraOutputChance, experience, time)
    }

    override fun unlockedBy(name: String, criterion: Criterion<*>): OvenCookingRecipeBuilder {
        criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): OvenCookingRecipeBuilder {
        group = groupName
        return this
    }

    override fun getResult(): Item {
        return result.item
    }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val builder = recipeOutput.advancement()
            .addCriterion("has_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)

        criteria.forEach { (name, criterion) ->
            builder.addCriterion(name, criterion)
        }

        val ovenCookingRecipe = OvenCookingRecipe(ingredient, extraIngredient, result, extraOutput, extraOutputChance, experience, cookingTime)
        recipeOutput.accept(
            id.withPrefix("oven/").withSuffix("_from_${ingredient.items[0].item.`arch$registryName`()!!.path}"),
            ovenCookingRecipe,
            builder.build(id.withPrefix("recipes/oven/"))
        )
    }
}