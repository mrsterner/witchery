package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.book.page.BookRecipePage
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.brazier.BrazierSummoningRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.registry.WitcheryItems
import dev.sterner.witchery.util.RenderUtils.blitWithAlpha
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Style
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder


abstract class BookBrazierSummoningRecipePageRenderer<T : Recipe<*>?>(page: BookBrazierSummoningRecipePage?) :
    BookRecipePageRenderer<BrazierSummoningRecipe, BookRecipePage<BrazierSummoningRecipe>?>(page) {


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
        recipeHolder: RecipeHolder<BrazierSummoningRecipe>,
        recipeX: Int,
        recipeY: Int,
        mouseX: Int,
        mouseY: Int,
        second: Boolean
    ) {
        val pose = guiGraphics.pose()

        pose.pushPose()

        if (!this.page!!.title1.isEmpty) {
            this.renderTitle(guiGraphics, this.page!!.title1, false, BookEntryScreen.PAGE_WIDTH / 2, 0)
        }

        for ((index, ingredient) in recipeHolder.value.inputItems.withIndex()) {

            this.parentScreen.renderItemStack(
                guiGraphics,
                recipeX + 48 + 9 + 4 + 6 - (18),
                recipeY + 20 * index,
                mouseX,
                mouseY,
                ingredient
            )
        }

        this.parentScreen.renderItemStack(
            guiGraphics,
            recipeX + 48 + 9 + 4 + 6 - (18),
            recipeY + 20 + 6 - 4 + 18 + 32,
            mouseX,
            mouseY,
            WitcheryItems.BRAZIER.get().defaultInstance
        )

        pose.popPose()
    }
}