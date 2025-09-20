package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.TextWidget
import dev.emi.emi.api.widget.WidgetHolder
import dev.sterner.witchery.recipe.brazier.BrazierSummoningRecipe
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient

class BrazierEmiRecipe(val recipeId: ResourceLocation, val recipe: BrazierSummoningRecipe) : EmiRecipe {

    override fun getCategory(): EmiRecipeCategory {
        return WitcheryEmiPlugin.BRAZIER_CATEGORY
    }

    override fun getId(): ResourceLocation {
        return recipeId
    }

    override fun getInputs(): MutableList<EmiIngredient> {
        return mutableListOf<EmiIngredient>().apply {
            addAll(recipe.inputItems.map { EmiIngredient.of(Ingredient.of(it)) })
        }
    }

    override fun getOutputs(): MutableList<EmiStack> {
        return mutableListOf<EmiStack>().apply {

        }
    }

    override fun getDisplayWidth(): Int {
        return 18 * 6
    }

    override fun getDisplayHeight(): Int {
        return 18 * 6
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addText(Component.translatable(id.toString()), displayWidth / 2, 2, 0xffffff, true)
            .horizontalAlign(TextWidget.Alignment.CENTER)
        widgets.addTooltipText(listOf(Component.translatable("$id.tooltip")), 9, 2, 18 * 7, 18)


        for ((index, ingredient) in recipe.inputItems.withIndex()) {
            widgets.add(
                WitcherySlotWidget(EmiStack.of(ingredient), displayWidth / 2 - 9, 20 * index + 18)
                    .drawBack(false)
            )
        }

        widgets.add(
            WitcherySlotWidget(EmiStack.of(WitcheryItems.BRAZIER.get()), displayWidth / 2 - 9, displayHeight - 9 * 3)
                .drawBack(false)
        )
    }
}