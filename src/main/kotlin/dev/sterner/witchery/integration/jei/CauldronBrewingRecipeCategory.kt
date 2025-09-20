package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.util.RenderUtils
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

class CauldronBrewingRecipeCategory(var guiHelper: IJeiHelpers) : IRecipeCategory<CauldronBrewingRecipe> {

    override fun getRecipeType(): RecipeType<CauldronBrewingRecipe> {
        return WitcheryJeiPlugin.CAULDRON_BREWING
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.cauldron_brewing.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.guiHelper.createBlankDrawable(18 * 8, 18 * 9)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.guiHelper.createDrawableItemStack(WitcheryItems.CAULDRON.get().defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: CauldronBrewingRecipe, focuses: IFocusGroup) {
        // Input ingredients
        var drawableBuilder =
            guiHelper.guiHelper.drawableBuilder(Witchery.id("textures/gui/order_widget.png"), 0, 0, 48, 18)
        drawableBuilder.setTextureSize(48, 18)
        for ((index, ingredient) in recipe.inputItems.withIndex()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 2 + 2 + 18, 20 * index)
                .addItemStack(ingredient.itemStack)
                .setBackground(drawableBuilder.build(), -18, -1)

        }

        // Glass Bottle
        builder.addSlot(RecipeIngredientRole.INPUT, 48 + 18 + 9 - 12, 20 * 1 + 6 - 18)
            .addItemStack(Items.GLASS_BOTTLE.defaultInstance)

        // Output
        builder.addSlot(RecipeIngredientRole.OUTPUT, 48 + 18 + 9 + 18 + 9 + 4, 20 * 1 + 6 - 18)
            .addItemStack(recipe.outputItem)
    }

    override fun draw(
        recipe: CauldronBrewingRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {

        guiGraphics.blit(Witchery.id("textures/gui/cauldron.png"), 48 + 18 + 9, 20 * 1 - 18 + 8, 0f, 0f, 35, 56, 35, 56)

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

        var bl = false
        if (recipe.dimensionKey.isNotEmpty()) {
            for ((index, key) in recipe.dimensionKey.withIndex()) {
                if (key.isNotEmpty()) {
                    bl = true
                }
                val text = Component.translatable(key)
                guiGraphics.drawString(
                    Minecraft.getInstance().font,
                    text,
                    width / 4 + 18 + 18,
                    36 + 36 + 14 * index,
                    0xffffff,
                    true
                )
            }
        }
        if (!bl) {
            val text = Component.translatable("witchery:all_worlds")
            guiGraphics.drawString(
                Minecraft.getInstance().font,
                text,
                width / 4 + 18 + 18,
                36 + 36,
                0xffffff,
                true
            )
        }
    }

}