package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.book.page.BookRecipePage
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.cauldron.CauldronInfusionRecipe
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import java.awt.Color

open class BookCauldronInfusionRecipePageRenderer<T : Recipe<*>?>(page: BookCauldronInfusionRecipePage?) :
    BookRecipePageRenderer<CauldronInfusionRecipe, BookRecipePage<CauldronInfusionRecipe>?>(page) {


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
        recipeHolder: RecipeHolder<CauldronInfusionRecipe>,
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

        this.parentScreen.renderItemStack(guiGraphics,
            recipeX + 48 - 24,
            recipeY + 20 + 6 - 4 + 18 + 18, mouseX, mouseY, recipeHolder.value().infusionItem
        )

        this.parentScreen.renderItemStack(guiGraphics,
            recipeX + 48,
            recipeY + 20 + 9, mouseX, mouseY,recipeHolder.value().outputItem
        )

        this.parentScreen.renderItemStack(guiGraphics,
            recipeX + 48,
            recipeY + 20 + 18 + 18 + 18 + 3, mouseX, mouseY,recipeHolder.value().brewInput,
        )

        val c = Component.literal("Altar Power: ${recipeHolder.value.altarPower}/s")
        val i: Int = Minecraft.getInstance().font.width(c)
        guiGraphics.drawStringWithBackdrop(
            Minecraft.getInstance().font,
            c,
            recipeX + (c.toString().length) - 18,
            recipeY + 18 * 6 - 2,
            i,
            Color(200,200,200).rgb
        )
        guiGraphics.blit(
            Witchery.id("textures/gui/cauldron_modonomicon.png"),
            recipeX + 48 - 9, recipeY + 20 + 18 + 9,
            0f, 0f,
            35, 56,
            35, 56
        )

        pose.popPose()
    }
}