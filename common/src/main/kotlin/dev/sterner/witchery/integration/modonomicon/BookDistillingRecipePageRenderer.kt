package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.book.page.BookRecipePage
import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen
import com.klikli_dev.modonomicon.client.render.page.BookRecipePageRenderer
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.RenderUtils.blitWithAlpha
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder


abstract class BookDistillingRecipePageRenderer<T : Recipe<*>?>(page: BookDistillingRecipePage?) :
    BookRecipePageRenderer<DistilleryCraftingRecipe?, BookRecipePage<DistilleryCraftingRecipe?>?>(page) {


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
        recipeHolder: RecipeHolder<DistilleryCraftingRecipe?>,
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

        blitWithAlpha(
            pose,
            Witchery.id("textures/gui/distillery_modonomicon.png"),
            recipeX + 48 + 9 - 18 - 18 - 9, recipeY,
            0f, 0f,
            91, 45,
            91, 45,
        )


        if (recipeHolder.value.inputItems.isNotEmpty()) {
            this.parentScreen.renderItemStack(
                guiGraphics,
                recipeX + 2 + 2 + 9 - 4 + 5 - 1,
                recipeY + 50 - 18 - 4 - 18 - 2 + 1 - 7 - 1,
                mouseX,
                mouseY,
                recipeHolder.value.inputItems[0]
            )
        }

        if (recipeHolder.value.inputItems.size > 1) {
            this.parentScreen.renderItemStack(
                guiGraphics,
                recipeX + 2 + 2 + 9 + 18 - 4 + 5,
                recipeY + 50 - 18 - 4 - 18 - 2 + 1 - 7 - 1,
                mouseX,
                mouseY,
                recipeHolder.value.inputItems[1]
            )
        }


        this.parentScreen.renderItemStack(
            guiGraphics,
            recipeX + 2 + 2 + 9 + 9 - 4 + 4,
            recipeY + 50 - 18 - 4 + 36 - 20 + 1 - 22 + 3,
            mouseX,
            mouseY,
            ItemStack(
                WitcheryItems.JAR.get(), recipeHolder.value.jarConsumption
            )
        )


        if (recipeHolder.value.outputItems.isNotEmpty()) {
            this.parentScreen.renderItemStack(
                guiGraphics,
                recipeX + 2 + 2 + 18 + 24 + 24 + 9 + 18 - 1 - 22 - 9 + 1,
                recipeY + 50 - 18 - 4 - 9 - 1 - 9 - 1,
                mouseX,
                mouseY,
                recipeHolder.value.outputItems[0]
            )
        }

        if (recipeHolder.value.outputItems.size > 1) {
            this.parentScreen.renderItemStack(
                guiGraphics,
                recipeX + 2 + 2 + 18 + 24 + 24 + 9 + 18 + 18 - 1 - 22 - 9 + 2,
                recipeY + 50 - 18 - 4 - 9 - 1 - 9 - 1,
                mouseX,
                mouseY,
                recipeHolder.value.outputItems[1]
            )
        }

        if (recipeHolder.value.outputItems.size > 2) {
            this.parentScreen.renderItemStack(
                guiGraphics,
                recipeX + 2 + 2 + 18 + 24 + 24 + 9 + 18 - 1 - 22 - 9 + 1,
                recipeY + 50 - 18 - 4 + 18 - 9 - 1 - 9,
                mouseX,
                mouseY,
                recipeHolder.value.outputItems[2]
            )
        }

        if (recipeHolder.value.outputItems.size > 3) {
            this.parentScreen.renderItemStack(
                guiGraphics,
                recipeX + 2 + 2 + 18 + 24 + 24 + 9 + 18 + 18 - 1 - 22 - 9 + 2,
                recipeY + 50 - 18 - 4 + 18 - 9 - 1 - 9,
                mouseX,
                mouseY,
                recipeHolder.value.outputItems[3]
            )
        }

        blitWithAlpha(
            pose,
            Witchery.id("textures/gui/altar_power_modonomicon.png"),
            recipeX + 9, recipeY + 18 * 6 - 9,
            0f, 0f,
            96, 23,
            96, 23,
        )

        val c = Component.literal("Altar Power: ${recipeHolder.value.altarPower}/s")
        val i: Int = Minecraft.getInstance().font.width(c)
        guiGraphics.drawStringWithBackdrop(
            Minecraft.getInstance().font,
            c,
            recipeX + (c.toString().length) - 9,
            recipeY + 18 * 6 - 2,
            i,
            0xffffff
        )


        // Pop the pose to restore state
        pose.popPose()
    }
}