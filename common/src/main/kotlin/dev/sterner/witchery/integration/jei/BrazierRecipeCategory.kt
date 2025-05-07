package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.integration.jei.wrapper.BrazierSummoningJeiRecipe
import dev.sterner.witchery.registry.WitcheryItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IJeiHelpers
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component

class BrazierRecipeCategory(var guiHelper: IJeiHelpers) : IRecipeCategory<BrazierSummoningJeiRecipe> {

    override fun getRecipeType(): RecipeType<BrazierSummoningJeiRecipe> {
        return WitcheryJeiPlugin.BRAZIER
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.brazier.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.guiHelper.createBlankDrawable(18 * 6, 18 * 6)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.guiHelper.createDrawableItemStack(WitcheryItems.BRAZIER.get().defaultInstance)
    }

    override fun draw(
        recipe: BrazierSummoningJeiRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY)
        guiGraphics.drawCenteredString(
            Minecraft.getInstance().font,
            Component.translatable("${recipe.id}.tooltip"), (width / 2), 2, -1
        )
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: BrazierSummoningJeiRecipe, focuses: IFocusGroup) {
        for ((index, ingredient) in recipe.recipe.inputItems.withIndex()) {
            builder.addSlot(RecipeIngredientRole.INPUT, (width / 2) - 9, 20 * index + 18)
                .addItemStack(ingredient)
        }

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, (width / 2) - 9, height - 9 * 3)
            .addItemStack(WitcheryItems.BRAZIER.get().defaultInstance)

    }
}