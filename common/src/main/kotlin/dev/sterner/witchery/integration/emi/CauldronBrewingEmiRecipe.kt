package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.RenderUtils.blitWithAlpha
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient

class CauldronBrewingEmiRecipe(val recipe: CauldronBrewingRecipe) : EmiRecipe {

    override fun getCategory(): EmiRecipeCategory {
        return WitcheryEmiPlugin.CAULDRON_BREWING_CATEGORY
    }

    override fun getId(): ResourceLocation {
        return Witchery.id(CauldronBrewingRecipe.NAME)
    }

    override fun getInputs(): MutableList<EmiIngredient> {
        val mutableList = mutableListOf<EmiIngredient>()
        for (ingredients in recipe.ingredients) {
            mutableList.add(EmiIngredient.of(ingredients))
        }
        return mutableList
    }

    override fun getOutputs(): MutableList<EmiStack> {
        val mutableList = mutableListOf<EmiStack>()
        mutableList.add(EmiStack.of(recipe.outputItem))
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
                WitcherySlotWidget(EmiIngredient.of(Ingredient.of(ingredient.itemStack)), 2 + 2 + 18, 20 * index)
                    .drawBack(false)
            )
        }

        widgets.addDrawable(48 + 18 + 9, 20 * 1, 18, 18) { ctx, _, _, _ ->
            ctx.blit(Witchery.id("textures/gui/cauldron.png"), 0, 8, 0f, 0f, 35, 56, 35, 56)
        }

        widgets.add(
            WitcherySlotWidget(
                EmiIngredient.of(Ingredient.of(Items.GLASS_BOTTLE.defaultInstance)),
                48 + 18 + 9 - 12,
                20 * 1 + 6
            )
                .drawBack(false)
        )

        widgets.add(
            WitcherySlotWidget(EmiIngredient.of(Ingredient.of(recipe.outputItem)), 48 + 18 + 9 + 18 + 9 + 4, 20 * 1 + 6)
                .drawBack(false)
        )
    }
}