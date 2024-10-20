package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.recipe.spinning_wheel.SpinningWheelRecipe
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient

class SpinningEmiRecipe(val recipeId: ResourceLocation, val recipe: SpinningWheelRecipe) : EmiRecipe {

    override fun getCategory(): EmiRecipeCategory {
        return WitcheryEmiPlugin.SPINNING_CATEGORY
    }

    override fun getId(): ResourceLocation {
        return recipeId
    }

    override fun getInputs(): MutableList<EmiIngredient> {
        val mutableList = mutableListOf<EmiIngredient>()
        for (item in recipe.inputItems) {
            mutableList.add(EmiIngredient.of(Ingredient.of(item)))
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
        return 18 * 5
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.addTexture(Witchery.id("textures/gui/spinning_wheel_emi.png"), 9, 9, 123, 54, 0, 0)

        widgets.add(
            WitcherySlotWidget(EmiStack.of(recipe.inputItems[0]), 2 + 2 + 9 - 4, 50 - 18 - 4 - 18 - 2 + 1 + 18)
                .drawBack(false)
        )
        if (recipe.inputItems.size > 1) {
            widgets.add(
                WitcherySlotWidget(EmiStack.of(recipe.inputItems[1]), 2 + 2 + 9 + 18 - 4 + 18, 50 - 18 - 4 - 18 - 2 + 1)
                    .drawBack(false)
            )
        }

        if (recipe.inputItems.size > 2) {
            widgets.add(
                WitcherySlotWidget(EmiStack.of(recipe.inputItems[2]), 2 + 2 + 9 + 18 - 4+ 18, 50 - 18 - 4 - 18 - 2 + 1 + 22)
                    .drawBack(false)
            )
        }

        if (recipe.inputItems.size > 3) {
            widgets.add(
                WitcherySlotWidget(EmiStack.of(recipe.inputItems[2]), 2 + 2 + 9 + 18 - 4 + 18, 50 - 18 - 4 - 18 - 2 + 1 + 22 + 22)
                    .drawBack(false)
            )
        }

        widgets.add(
            WitcherySlotWidget(
                EmiStack.of(recipe.outputItem),
                2 + 2 + 18 + 24 + 24 + 9 + 18 - 1 - 18,
                50 - 18 - 4 - 9 - 1 + 10
            )
                .drawBack(false).recipeContext(this)
        )


        widgets.addText(
            Component.literal("Altar Power: ${recipe.altarPower}/s"),
            displayWidth / 4,
            displayHeight - 18,
            0xffffff,
            true
        )
    }
}