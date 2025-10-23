package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.content.recipe.cauldron.CauldronInfusionRecipe
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.util.RenderUtils
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
import net.minecraft.world.item.Items

class CauldronInfusionRecipeCategory(var guiHelper: IJeiHelpers) : IRecipeCategory<CauldronInfusionRecipe> {

    override fun getRecipeType(): RecipeType<CauldronInfusionRecipe> {
        return WitcheryJeiPlugin.CAULDRON_INFUSION
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.cauldron_infusion.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.guiHelper.createBlankDrawable(18 * 6, 18 * 5)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.guiHelper.createDrawableItemStack(WitcheryItems.CAULDRON.get().defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: CauldronInfusionRecipe, focuses: IFocusGroup) {

        builder.addSlot(RecipeIngredientRole.INPUT, 2 + 2 + 18 + 9, 28)
            .addItemStack(recipe.infusionItem)

        builder.addSlot(RecipeIngredientRole.INPUT, 48 + 18 + 9 - 12, 20 * 1 + 24 + 8)
            .addItemStack(recipe.brewInput)

        builder.addSlot(RecipeIngredientRole.OUTPUT, 48 + 18 + 9 - 12, 4)
            .addItemStack(recipe.outputItem)
    }

    override fun draw(
        recipe: CauldronInfusionRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {

        guiGraphics.blit(Witchery.id("textures/gui/cauldron_modonomicon.png"), 48 + 6, 20 * 1, 0f, 0f, 35, 56, 35, 56)

    }

}