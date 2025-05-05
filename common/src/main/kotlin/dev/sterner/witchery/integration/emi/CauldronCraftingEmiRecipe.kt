package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.util.RenderUtils.blitWithAlpha
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient

class CauldronCraftingEmiRecipe(val recipeId: ResourceLocation, val recipe: CauldronCraftingRecipe) : EmiRecipe {

    override fun getCategory(): EmiRecipeCategory {
        return WitcheryEmiPlugin.CAULDRON_CRAFTING_CATEGORY
    }

    override fun getId(): ResourceLocation {
        return recipeId
    }

    override fun getInputs(): MutableList<EmiIngredient> {
        val mutableList = mutableListOf<EmiIngredient>()
        for (ingredients in recipe.inputItems) {
            mutableList.add(EmiIngredient.of(Ingredient.of(ingredients.itemStack)))
        }
        return mutableList
    }

    override fun getOutputs(): MutableList<EmiStack> {
        val mutableList = mutableListOf<EmiStack>()
        for (itemStacks in recipe.outputItems) {
            mutableList.add(EmiStack.of(itemStacks))
        }
        return mutableList
    }

    override fun getDisplayWidth(): Int {
        return 18 * 8
    }

    override fun getDisplayHeight(): Int {
        return 18 * 6 + if (recipe.inputItems.size > 5) (recipe.inputItems.size - 5) * 18 else 0
    }

    override fun addWidgets(widgets: WidgetHolder) {
        for ((index, ingredient) in recipe.inputItems.withIndex()) {
            widgets.addDrawable(2, 20 * index, 48, 18) { ctx, _, _, _ ->
                ctx.blit(Witchery.id("textures/gui/order_widget.png"), 0, 0, 0f, 0f, 48, 18, 48, 18)
                blitWithAlpha(
                    ctx.pose(),
                    Witchery.id("textures/gui/index_${ingredient.order + 1}.png"),
                    2,
                    2,
                    0f,
                    0f,
                    13,
                    13,
                    13,
                    13
                )
            }

            widgets.add(
                WitcherySlotWidget(EmiStack.of(ingredient.itemStack), 2 + 2 + 18, 20 * index)
                    .drawBack(false)
            )
        }

        for ((index, itemStack) in recipe.outputItems.withIndex()) {
            widgets.add(
                WitcherySlotWidget(EmiStack.of(itemStack), 48 + 18 + 9 + 18 + 9 + 4 + (18 * index), 20 * 1 + 6)
                    .drawBack(false)
            ).recipeContext(this)
        }

        widgets.addDrawable(48 + 18 + 9, 20 * 1, 18, 18) { ctx, _, _, _ ->
            ctx.blit(Witchery.id("textures/gui/cauldron.png"), 0, 8, 0f, 0f, 35, 56, 35, 56)
        }
    }
}