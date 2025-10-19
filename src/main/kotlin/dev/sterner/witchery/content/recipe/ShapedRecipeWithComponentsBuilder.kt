package dev.sterner.witchery.content.recipe

import dev.sterner.witchery.ShapedRecipeAccessor
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.component.DataComponentMap
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.ShapedRecipe
import net.minecraft.world.item.crafting.ShapedRecipePattern
import net.minecraft.world.level.ItemLike

class ShapedRecipeWithComponentsBuilder(
    category: RecipeCategory,
    output: ItemLike,
    private val components: DataComponentMap,
    count: Int = 1
) : ShapedRecipeBuilder(category, output, count) {

    override fun save(exporter: RecipeOutput, recipeId: ResourceLocation) {
        val shapedRecipePattern = this.ensureValid(recipeId)
        val builder = exporter.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
            .rewards(AdvancementRewards.Builder.recipe(recipeId))
            .requirements(AdvancementRequirements.Strategy.OR)

        (this as ShapedRecipeAccessor).criteria.forEach(builder::addCriterion)

        val outputStack = ItemStack(this.result, this.count)
        outputStack.applyComponents(this.components)

        val shapedRecipe = ShapedRecipe(
            this.group ?: "",
            RecipeBuilder.determineBookCategory(this.category),
            shapedRecipePattern,
            outputStack,
            this.showNotification
        )

        exporter.accept(
            recipeId,
            shapedRecipe,
            builder.build(recipeId.withPrefix("recipes/${this.category.name.lowercase()}/"))
        )
    }

    private fun ensureValid(location: ResourceLocation): ShapedRecipePattern {
        if ((this as ShapedRecipeAccessor).criteria.isEmpty()) {
            throw IllegalStateException("No way of obtaining recipe $location")
        }
        return ShapedRecipePattern.of((this as ShapedRecipeAccessor).key, (this as ShapedRecipeAccessor).rows)
    }

    companion object {
        fun create(category: RecipeCategory, output: ItemLike, components: DataComponentMap, count: Int = 1) =
            ShapedRecipeWithComponentsBuilder(category, output, components, count)
    }
}