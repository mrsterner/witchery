package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.core.util.RenderUtils
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

class CauldronCraftingRecipeCategory(var guiHelper: IJeiHelpers) : IRecipeCategory<CauldronCraftingRecipe> {

    override fun getRecipeType(): RecipeType<CauldronCraftingRecipe> {
        return WitcheryJeiPlugin.CAULDRON_CRAFTING
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.cauldron_crafting.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.guiHelper.createBlankDrawable(18 * 8, 18 * 9)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.guiHelper.createDrawableItemStack(WitcheryItems.CAULDRON.get().defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: CauldronCraftingRecipe, focuses: IFocusGroup) {
        val orderWidgetDrawable =
            guiHelper.guiHelper.drawableBuilder(Witchery.id("textures/gui/order_widget.png"), 0, 0, 48, 18)
                .setTextureSize(48, 18)
                .build()

        for ((index, ingredient) in recipe.inputItems.withIndex()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 2 + 2 + 18, 20 * index)
                .addItemStack(ingredient.itemStack)
                .setBackground(orderWidgetDrawable, -18, -1)
        }

        // Output items
        for ((index, outputStack) in recipe.outputItems.withIndex()) {
            builder.addSlot(
                RecipeIngredientRole.OUTPUT,
                48 + 18 + 9 + 18 + 9 + 4 + (18 * index),
                20 * 1 + 6 - 18
            ).addItemStack(outputStack)
        }
    }

    override fun draw(
        recipe: CauldronCraftingRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        guiGraphics.blit(
            Witchery.id("textures/gui/cauldron.png"),
            48 + 18 + 9,
            20 * 1,
            0f,
            0f,
            35,
            56,
            35,
            56
        )

        for ((index, ingredient) in recipe.inputItems.withIndex()) {
            val order = ingredient.order
            if (order in 0..8) {
                RenderUtils.blitWithAlpha(
                    guiGraphics.pose(),
                    Witchery.id("textures/gui/index_${order + 1}.png"),
                    2 + 2 + 18 + 2 - 18,
                    20 * index + 2,
                    0f,
                    0f,
                    13,
                    13,
                    13,
                    13
                )
            }
        }
    }

}