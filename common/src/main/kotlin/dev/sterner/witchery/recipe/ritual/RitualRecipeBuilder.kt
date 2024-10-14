package dev.sterner.witchery.recipe.ritual

import dev.sterner.witchery.block.ritual.RitualManager.CommandType
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class RitualRecipeBuilder(
    val inputItems: List<ItemStack>,
    val inputEntities: List<EntityType<*>>,
    val outputItems: List<ItemStack>,
    val outputEntities: List<EntityType<*>>,
    val altarPower: Int,
    val commands: Set<CommandType>,
    val isInfinite: Boolean,
    val floatingItemOutput: Boolean,
    val ticks: Int
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
        return outputItems[0].item
    }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val builder = recipeOutput.advancement()
            .addCriterion("has_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
        criteria.forEach { (name, criterion) -> builder.addCriterion(name, criterion) }
        val abstractCookingRecipe = RitualRecipe(inputItems, inputEntities, outputItems, outputEntities, altarPower, commands, isInfinite, floatingItemOutput, ticks)
        recipeOutput.accept(id.withPrefix("ritual/").withSuffix("_from_${inputItems[0].item.`arch$registryName`()!!.path}"), abstractCookingRecipe, builder.build(id.withPrefix("recipes/ritual/")))
    }
}