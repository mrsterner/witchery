package dev.sterner.witchery.recipe

import dev.sterner.witchery.mixin.ShapelessRecipeAccessor
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.NonNullList
import net.minecraft.core.component.DataComponentMap
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.ShapelessRecipe
import net.minecraft.world.level.ItemLike
import java.util.*

class ShapelessRecipeWithComponentsBuilder(
    category: RecipeCategory,
    output: ItemLike,
    private val components: DataComponentMap,
    count: Int = 1
) : ShapelessRecipeBuilder(category, output, count) {

    fun offerTo(exporter: RecipeOutput, recipeId: ResourceLocation, list: NonNullList<Ingredient>) {
        val builder = exporter.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
            .rewards(AdvancementRewards.Builder.recipe(recipeId))
            .requirements(AdvancementRequirements.Strategy.OR)

        (this as ShapelessRecipeAccessor).criteria.forEach(builder::addCriterion)

        val outputStack = ItemStack(this.result, this.count)
        outputStack.applyComponents(this.components)

        val shapelessRecipe = ShapelessRecipe(
            this.group ?: "",
            RecipeBuilder.determineBookCategory(this.category),
            outputStack,
            list
        )

        exporter.accept(
            recipeId,
            shapelessRecipe,
            builder.build(recipeId.withPrefix("recipes/${this.category.name.lowercase()}/"))
        )
    }

    companion object {
        fun create(category: RecipeCategory, output: ItemLike, components: DataComponentMap, count: Int = 1) =
            ShapelessRecipeWithComponentsBuilder(category, output, components, count)
    }
}
