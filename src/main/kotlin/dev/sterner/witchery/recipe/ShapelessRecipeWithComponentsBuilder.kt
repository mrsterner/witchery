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
    category: RecipeCategory, output: ItemLike,
    private val components: DataComponentMap, count: Int
) :
    ShapelessRecipeBuilder(category, output, count) {

    fun offerTo(exporter: RecipeOutput, recipeId: ResourceLocation, list: NonNullList<Ingredient>) {

        val builder: Advancement.Builder = exporter.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(recipeId))
            .rewards(AdvancementRewards.Builder.recipe(recipeId))
            .requirements(AdvancementRequirements.Strategy.OR)

        (this as ShapelessRecipeAccessor).getCriteria().forEach(builder::addCriterion)

        val outputtedItemStack = ItemStack(
            (this as ShapelessRecipeAccessor).result,
            (this as ShapelessRecipeAccessor).getCount()
        )
        outputtedItemStack.applyComponents(this.components)

        val shapedRecipe = ShapelessRecipe(
            Objects.requireNonNullElse((this as ShapelessRecipeAccessor).getGroup(), ""),
            RecipeBuilder.determineBookCategory((this as ShapelessRecipeAccessor).getCategory()),
            outputtedItemStack,
            list
        )

        exporter.accept(
            recipeId, shapedRecipe, builder.build(
                recipeId.withPrefix(
                    ("recipes/" + (this as ShapelessRecipeAccessor).getCategory().name.lowercase()) + "/"
                )
            )
        )
    }

    companion object {
        fun create(
            category: RecipeCategory, output: ItemLike,
            components: DataComponentMap
        ): ShapelessRecipeWithComponentsBuilder {
            return create(category, output, components, 1)
        }

        fun create(
            category: RecipeCategory, output: ItemLike,
            components: DataComponentMap, count: Int
        ): ShapelessRecipeWithComponentsBuilder {
            return ShapelessRecipeWithComponentsBuilder(category, output, components, count)
        }
    }
}