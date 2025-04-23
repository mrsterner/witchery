package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.recipe.oven.OvenCookingRecipe
import dev.sterner.witchery.registry.WitcheryItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.helpers.IJeiHelpers
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.network.chat.Component

class OvenRecipeCategory(var guiHelper: IJeiHelpers) : IRecipeCategory<OvenCookingRecipe> {

    override fun getRecipeType(): RecipeType<OvenCookingRecipe> {
        return WitcheryJeiPlugin.OVEN
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.oven.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.guiHelper.createBlankDrawable(18 * 8, 18 * 6)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.guiHelper.createDrawableItemStack(WitcheryItems.IRON_WITCHES_OVEN.get().defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: OvenCookingRecipe, focuses: IFocusGroup) {

    }
}