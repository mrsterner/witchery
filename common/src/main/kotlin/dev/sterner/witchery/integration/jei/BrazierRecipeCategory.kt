package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.recipe.brazier.BrazierSummoningRecipe
import dev.sterner.witchery.registry.WitcheryItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.network.chat.Component

class BrazierRecipeCategory(var guiHelper: IGuiHelper) : IRecipeCategory<BrazierSummoningRecipe> {

    override fun getRecipeType(): RecipeType<BrazierSummoningRecipe> {
        return WitcheryJeiPlugin.BRAZIER
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.brazier.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.createBlankDrawable(18 * 6, 18 * 6)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.createDrawableItemStack(WitcheryItems.BRAZIER.get().defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: BrazierSummoningRecipe, focuses: IFocusGroup) {

    }
}