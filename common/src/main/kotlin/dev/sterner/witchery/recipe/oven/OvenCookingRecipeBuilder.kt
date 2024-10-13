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
    val ingredient: Ingredient, val result: ItemStack, val extraOutput: ItemStack, val extraOutputChance: Float, val experience: Float, val cookingTime: Int
) : RecipeBuilder {

    private val criteria: MutableMap<String, Criterion<*>> = LinkedHashMap()

    override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder {
        this.criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): RecipeBuilder {
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
        criteria.forEach { (name, criterion) -> builder.addCriterion(name, criterion) }
        val abstractCookingRecipe = OvenCookingRecipe(ingredient, result, extraOutput, extraOutputChance, experience,  cookingTime)
        recipeOutput.accept(id.withPrefix("oven/"), abstractCookingRecipe, builder.build(id.withPrefix("recipes/oven/")))
    }
}