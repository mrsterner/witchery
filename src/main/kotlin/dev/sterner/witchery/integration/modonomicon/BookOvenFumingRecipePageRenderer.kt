package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.book.page.BookRecipePage
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.oven.OvenCookingRecipe
import dev.sterner.witchery.util.RenderUtils.blitWithAlpha
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Style
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder


abstract class BookOvenFumingRecipePageRenderer<T : Recipe<*>?>(page: BookOvenFumingRecipePage?) :
    BookRecipePageRenderer<OvenCookingRecipe, BookRecipePage<OvenCookingRecipe>?>(page) {


    override fun getRecipeHeight(): Int {
        return 45
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, ticks: Float) {
        val recipeX = X - 9
        val recipeY = Y + 18

        this.drawRecipe(
            guiGraphics,
            page!!.recipe1, recipeX, recipeY, mouseX, mouseY, false
        )

        val style: Style? = this.getClickedComponentStyleAt(mouseX.toDouble(), mouseY.toDouble())
        if (style != null) parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY)
    }

    override fun getClickedComponentStyleAt(pMouseX: Double, pMouseY: Double): Style? {
        val textStyle = super.getClickedComponentStyleAt(pMouseX, pMouseY)

        return textStyle
    }


    override fun drawRecipe(
        guiGraphics: GuiGraphics,
        recipeHolder: RecipeHolder<OvenCookingRecipe>,
        recipeX: Int,
        recipeY: Int,
        mouseX: Int,
        mouseY: Int,
        second: Boolean
    ) {
        val pose = guiGraphics.pose()

        /*
         widgets.addTexture(Witchery.id("textures/gui/oven_emi.png"), 18, 9, 108, 57, 0, 0)

            WitcherySlotWidget(EmiStack.of(recipe?.result ?: smokingRecipe!!.getResultItem(null)), 2 + 2 + 18 + 24 + 24 + 9, 50 - 18 - 4)
                .drawBack(false).recipeContext(this)

            WitcherySlotWidget(EmiStack.of(if(recipe != null) recipe.extraIngredient.items[0] else WitcheryItems.JAR.get().defaultInstance), 2 + 2 + 18 + 36 + 36 + 12 + 1, 48)
                .drawBack(false)

            WitcherySlotWidget(EmiStack.of(recipe?.extraOutput ?: WitcheryItems.FOUL_FUME.get().defaultInstance), 2 + 2 + 18 + 36 + 36 + 12 + 1, 9)
                .drawBack(false).recipeContext(this)

            WitcherySlotWidget(EmiStack.of(if (recipe != null) recipe.ingredient.items[0] else smokingRecipe!!.ingredients[0].items[0]), 2 + 18 - 1, 10)
         */
        pose.pushPose()



        if (!this.page!!.title1.isEmpty) {
            this.renderTitle(guiGraphics, this.page!!.title1, false, BookEntryScreen.PAGE_WIDTH / 2, 0)
        }

        blitWithAlpha(
            pose,
            Witchery.id("textures/gui/oven_modonomicon.png"),
            recipeX + 48 + 9 - 18 - 18 - 9, recipeY,
            0f, 0f,
            85, 57,
            85, 57,
        )

        this.parentScreen.renderItemStack(
            guiGraphics,
            recipeX + 56 + 2,
            recipeY + 18 + 2,
            mouseX,
            mouseY,
            recipeHolder.value.result
        )
        this.parentScreen.renderItemStack(
            guiGraphics,
            recipeX + 54 + 18 + 7,
            recipeY + 36 + 3,
            mouseX,
            mouseY,
            recipeHolder.value.extraIngredient.items[0]
        )
        this.parentScreen.renderItemStack(
            guiGraphics,
            recipeX + 54 + 18 + 7,
            recipeY + 1,
            mouseX,
            mouseY,
            recipeHolder.value.extraOutput
        )
        this.parentScreen.renderItemStack(
            guiGraphics,
            recipeX + 18 - 5,
            recipeY + 18 + 2,
            mouseX,
            mouseY,
            recipeHolder.value.ingredient.items[0]
        )

        // Pop the pose to restore state
        pose.popPose()
    }
}