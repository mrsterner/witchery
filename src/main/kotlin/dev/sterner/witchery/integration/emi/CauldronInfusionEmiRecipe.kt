package dev.sterner.witchery.integration.emi

import dev.emi.emi.api.recipe.EmiRecipe
import dev.emi.emi.api.recipe.EmiRecipeCategory
import dev.emi.emi.api.stack.EmiIngredient
import dev.emi.emi.api.stack.EmiStack
import dev.emi.emi.api.widget.WidgetHolder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.content.recipe.cauldron.CauldronInfusionRecipe
import dev.sterner.witchery.core.util.RenderUtils.blitWithAlpha
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.crafting.Ingredient

class CauldronInfusionEmiRecipe(val recipeId: ResourceLocation, val recipe: CauldronInfusionRecipe) : EmiRecipe {

    override fun getCategory(): EmiRecipeCategory {
        return WitcheryEmiPlugin.CAULDRON_INFUSION_CATEGORY
    }

    override fun getId(): ResourceLocation {
        return recipeId
    }

    override fun getInputs(): MutableList<EmiIngredient> {
        val mutableList = mutableListOf<EmiIngredient>()
        mutableList.add(EmiIngredient.of(Ingredient.of(recipe.infusionItem)))
        mutableList.add(EmiIngredient.of(Ingredient.of(recipe.brewInput)))
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
        return 18 * 6
    }

    override fun addWidgets(widgets: WidgetHolder) {
        widgets.add(
            WitcherySlotWidget(EmiStack.of(recipe.infusionItem), 28, 50)
                .drawBack(false)
        )

        widgets.add(
            WitcherySlotWidget(EmiStack.of(recipe.outputItem), 64, 22)
                .drawBack(false)
        ).recipeContext(this)

        widgets.addDrawable(48 + 8, 20 + 16, 18, 18) { ctx, _, _, _ ->
            ctx.blit(Witchery.id("textures/gui/cauldron_modonomicon.png"), 0, 8, 0f, 0f, 35, 56, 35, 56)
        }

        widgets.add(
            WitcherySlotWidget(EmiStack.of(recipe.brewInput), 64, 26 + 18 + 24 + 5)
                .drawBack(false)
        ).recipeContext(this)
    }
}