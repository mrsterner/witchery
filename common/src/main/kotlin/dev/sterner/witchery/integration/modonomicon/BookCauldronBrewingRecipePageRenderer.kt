package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.book.page.BookRecipePage
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.util.RenderUtils.blitWithAlpha
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Style
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder


abstract class BookCauldronBrewingRecipePageRenderer<T : Recipe<*>?>(page: BookCauldronBrewingRecipePage?) :
    BookRecipePageRenderer<CauldronBrewingRecipe?, BookRecipePage<CauldronBrewingRecipe?>?>(page) {


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
        recipeHolder: RecipeHolder<CauldronBrewingRecipe?>,
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
        // Render input items
        for ((index, ingredient) in recipeHolder.value.inputItems.withIndex()) {
            // Draw background texture for each ingredient


            guiGraphics.blit(
                Witchery.id("textures/gui/order_widget.png"),
                recipeX + 2, recipeY + 20 * index,
                0f, 0f,
                48, 18,
                48, 18
            )

            blitWithAlpha(
                pose,
                Witchery.id("textures/gui/index_${ingredient.order + 1}.png"),
                recipeX + 2 + 2, recipeY + 20 * index + 2,
                0f, 0f,
                13, 13,
                13, 13
            )

            this.parentScreen.renderItemStack(
                guiGraphics,
                recipeX + 2 + 2 + 18,
                recipeY + 20 * index,
                mouseX,
                mouseY,
                ingredient.itemStack
            )
        }

        this.parentScreen.renderItemStack(
            guiGraphics,
            recipeX + 48 + 9 + 4 + 6 + (18),
            recipeY + 20 + 6 - 4 + 18,
            mouseX,
            mouseY,
            recipeHolder.value.outputItem
        )
        this.parentScreen.renderItemStack(
            guiGraphics,
            recipeX + 48 + 9 + 4 + 6 - (18) + 9 + 9,
            recipeY + 20 + 6 - 4 + 18,
            mouseX,
            mouseY,
            Items.GLASS_BOTTLE.defaultInstance
        )


        // Render the cauldron icon
        guiGraphics.blit(
            Witchery.id("textures/gui/cauldron_brewing_modonomicon.png"),
            recipeX + 48 + 9 + 9, recipeY + 20 + 18 + 18,
            0f, 0f,
            35, 56,
            35, 56
        )

        // Pop the pose to restore state
        pose.popPose()
    }
}