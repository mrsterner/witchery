package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.spinning_wheel.SpinningWheelRecipe
import dev.sterner.witchery.core.registry.WitcheryItems
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

class SpinningRecipeCategory(var guiHelper: IJeiHelpers) : IRecipeCategory<SpinningWheelRecipe> {

    override fun getRecipeType(): RecipeType<SpinningWheelRecipe> {
        return WitcheryJeiPlugin.SPINNING
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.spinning.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.guiHelper.createBlankDrawable(18 * 8, 18 * 6)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.guiHelper.createDrawableItemStack(WitcheryItems.SPINNING_WHEEL.get().defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: SpinningWheelRecipe, focuses: IFocusGroup) {
        // Input slots
        builder.addSlot(RecipeIngredientRole.INPUT, 2 + 2 + 9 - 4, 50 - 18 - 4 - 18 - 2 + 1 + 18)
            .addItemStack(recipe.inputItems[0])

        if (recipe.inputItems.size > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, 2 + 2 + 9 + 18 - 4 + 18, 50 - 18 - 4 - 18 - 2 + 1)
                .addItemStack(recipe.inputItems[1])
        }

        if (recipe.inputItems.size > 2) {
            builder.addSlot(RecipeIngredientRole.INPUT, 2 + 2 + 9 + 18 - 4 + 18, 50 - 18 - 4 - 18 - 2 + 1 + 18)
                .addItemStack(recipe.inputItems[2])
        }

        if (recipe.inputItems.size > 3) {
            builder.addSlot(RecipeIngredientRole.INPUT, 2 + 2 + 9 + 18 - 4 + 18, 50 - 18 - 4 - 18 - 2 + 1 + 18 + 18)
                .addItemStack(recipe.inputItems[3])
        }

        // Output slot
        builder.addSlot(
            RecipeIngredientRole.OUTPUT,
            2 + 2 + 18 + 24 + 24 + 9 + 18 - 18 + 2,
            50 - 18 - 4 - 9 - 1 + 10 - 1
        )
            .addItemStack(recipe.outputItem)
    }

    override fun draw(
        recipe: SpinningWheelRecipe,
        recipeSlotsView: IRecipeSlotsView,
        graphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val texture = Witchery.id("textures/gui/spinning_wheel_emi.png")
        graphics.blit(texture, 8, 8, 0, 0, 123, 54)

        graphics.drawCenteredString(
            Minecraft.getInstance().font, Component.literal("Altar Power: ${recipe.altarPower}/s"),
            width / 2,
            height - 18,
            0xffffff
        )
    }

}