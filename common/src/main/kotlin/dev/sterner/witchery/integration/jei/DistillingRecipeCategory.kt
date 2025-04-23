package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.recipe.brazier.BrazierSummoningRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.registry.WitcheryItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.network.chat.Component

class DistillingRecipeCategory(var guiHelper: IGuiHelper) : IRecipeCategory<DistilleryCraftingRecipe> {

    override fun getRecipeType(): RecipeType<DistilleryCraftingRecipe> {
        return WitcheryJeiPlugin.DISTILLING
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.distilling.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.createBlankDrawable(18 * 8, 18 * 5)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.createDrawableItemStack(WitcheryItems.DISTILLERY.get().defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: DistilleryCraftingRecipe, focuses: IFocusGroup) {

    }
}