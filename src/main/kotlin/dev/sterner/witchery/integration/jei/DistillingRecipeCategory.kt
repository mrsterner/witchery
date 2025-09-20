package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
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
import net.minecraft.world.item.ItemStack

class DistillingRecipeCategory(var guiHelper: IJeiHelpers) : IRecipeCategory<DistilleryCraftingRecipe> {

    override fun getRecipeType(): RecipeType<DistilleryCraftingRecipe> {
        return WitcheryJeiPlugin.DISTILLING
    }

    override fun getTitle(): Component {
        return Component.translatable("witchery.distilling.category")
    }

    override fun getBackground(): IDrawable {
        return guiHelper.guiHelper.createBlankDrawable(18 * 8, 18 * 5)
    }

    override fun getIcon(): IDrawable? {
        return guiHelper.guiHelper.createDrawableItemStack(WitcheryItems.DISTILLERY.get().defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: DistilleryCraftingRecipe, focuses: IFocusGroup) {
        val baseX = 2 + 2
        val baseY = 50 - 18 - 4

        // Input 1
        builder.addSlot(RecipeIngredientRole.INPUT, baseX + 9 - 4, baseY - 18 - 2 + 1)
            .addItemStack(recipe.inputItems[0])

        // Input 2 (if exists)
        if (recipe.inputItems.size > 1) {
            builder.addSlot(RecipeIngredientRole.INPUT, baseX + 9 + 18 - 4, baseY - 18 - 2 + 1)
                .addItemStack(recipe.inputItems[1])
        }

        // Jar input
        builder.addSlot(RecipeIngredientRole.INPUT, baseX + 9 + 9 - 4, baseY + 36 - 20 + 1)
            .addItemStack(ItemStack(WitcheryItems.JAR.get(), recipe.jarConsumption))

        // Output 1
        if (recipe.outputItems.isNotEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, baseX + 18 + 24 + 24 + 9 + 18 - 1, baseY - 9 - 1)
                .addItemStack(recipe.outputItems[0])
        }

        // Output 2
        if (recipe.outputItems.size > 1) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, baseX + 18 + 24 + 24 + 9 + 18 + 18 - 1, baseY - 9 - 1)
                .addItemStack(recipe.outputItems[1])
        }

        // Output 3
        if (recipe.outputItems.size > 2) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, baseX + 18 + 24 + 24 + 9 + 18 - 1, baseY + 18 - 9 - 1)
                .addItemStack(recipe.outputItems[2])
        }

        // Output 4
        if (recipe.outputItems.size > 3) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, baseX + 18 + 24 + 24 + 9 + 18 + 18 - 1, baseY + 18 - 9 - 1)
                .addItemStack(recipe.outputItems[3])
        }
    }

    override fun draw(
        recipe: DistilleryCraftingRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val texture = Witchery.id("textures/gui/distillery_emi.png")

        guiGraphics.blit(texture, 8, 8, 0, 0, 123, 54)

        val text = Component.literal("Altar Power: ${recipe.altarPower}/s")
        val font = Minecraft.getInstance().font
        val textX = width / 2
        val textY = height - 24
        guiGraphics.drawCenteredString(font, text, textX, textY, 0xFFFFFF)
    }

}