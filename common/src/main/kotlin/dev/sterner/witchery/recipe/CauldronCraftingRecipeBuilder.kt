package dev.sterner.witchery.recipe

import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient

class CauldronCraftingRecipeBuilder(
    val inputItems: List<ItemStackWithColor>,
    val outputStack: List<Ingredient>,
    val altarPower: Int,
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
        return outputStack[0].items[0].item
    }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val builder = recipeOutput.advancement()
            .addCriterion("has_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
        criteria.forEach { (name, criterion) -> builder.addCriterion(name, criterion) }
        val abstractCookingRecipe = CauldronCraftingRecipe(inputItems, outputStack, altarPower)
        recipeOutput.accept(id.withPrefix("cauldron_crafting/"), abstractCookingRecipe, builder.build(id.withPrefix("recipes/cauldron_crafting/")))
    }
}