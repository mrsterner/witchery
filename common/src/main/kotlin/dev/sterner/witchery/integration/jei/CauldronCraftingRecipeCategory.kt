package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.recipe.brazier.BrazierSummoningRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.registry.WitcheryItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.network.chat.Component

class CauldronCraftingRecipeCategory(var guiHelper: IGuiHelper) : IRecipeCategory<CauldronCraftingRecipe> {

    override fun getRecipeType(): RecipeType<CauldronCraftingRecipe> {
        return WitcheryJeiPlugin.CAULDRON_CRAFTING
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.cauldron_crafting.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.createBlankDrawable(18 * 8, 18 * 8)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.createDrawableItemStack(WitcheryItems.CAULDRON.get().defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: CauldronCraftingRecipe, focuses: IFocusGroup) {

    }
}