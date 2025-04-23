package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.oven.OvenCookingRecipe
import dev.sterner.witchery.registry.WitcheryItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IJeiHelpers
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.gui.GuiGraphics
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
        // Input: Main ingredient (bottom-left)
        builder.addSlot(RecipeIngredientRole.INPUT, 2 + 18 - 1, 10)
            .addIngredients(recipe.ingredient)

        // Input: Jar or Extra Ingredient (bottom-right)
        builder.addSlot(RecipeIngredientRole.INPUT, 2 + 2 + 18 + 36 + 36 + 12 + 1, 48)
            .addItemStack(recipe.extraIngredient.items.firstOrNull() ?: WitcheryItems.JAR.get().defaultInstance)

        // Output: Main result (middle-right)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 2 + 2 + 18 + 24 + 24 + 9, 50 - 18 - 4)
            .addItemStack(recipe.result)

        // Output: Byproduct (top-right)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 2 + 2 + 18 + 36 + 36 + 12 + 1, 9)
            .addItemStack(recipe.extraOutput ?: WitcheryItems.FOUL_FUME.get().defaultInstance)
    }

    override fun draw(
        recipe: OvenCookingRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val texture = Witchery.id("textures/gui/oven_emi.png")
        guiGraphics.blit(texture, 17, 8, 0, 0, 108, 57)
    }
}