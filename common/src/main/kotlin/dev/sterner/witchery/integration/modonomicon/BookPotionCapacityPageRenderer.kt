package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer
import com.klikli_dev.modonomicon.client.render.page.PageWithTextRenderer
import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Style


open class BookPotionCapacityPageRenderer(page: BookPotionCapacityPage) :
    BookPageRenderer<BookPotionCapacityPage>(page),
    PageWithTextRenderer {

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, ticks: Float) {
        if (page!!.hasTitle()) {
            this.renderTitle(
                guiGraphics,
                page!!.title, false, BookEntryScreen.PAGE_WIDTH / 2, 0
            )
        }

        var currentY = this.textY - 18

        val itemHeight = 18
        val offsetX = 0

        for (item in page!!.items) {
            // Render the item to the left
            RenderSystem.enableBlend()
            guiGraphics.blit(
                Witchery.id("textures/gui/book_item_entry.png"),
                offsetX,
                currentY,
                0f,
                0f,
                24,
                24,
                24,
                24
            )

            parentScreen.renderItemStack(
                guiGraphics,
                offsetX + 4, currentY + 4,
                mouseX,
                mouseY,
                item.first
            )

            renderBookTextHolder(
                guiGraphics,
                item.second,
                Minecraft.getInstance().font,
                offsetX + 18 + 11,
                currentY + 4,
                BookEntryScreen.PAGE_WIDTH - 5,
                BookEntryScreen.PAGE_HEIGHT - currentY
            )

            currentY += itemHeight + 7
        }

        val style = this.getClickedComponentStyleAt(mouseX.toDouble(), mouseY.toDouble())
        if (style != null) parentScreen.renderComponentHoverEffect(guiGraphics, style, mouseX, mouseY)
    }

    override fun getClickedComponentStyleAt(pMouseX: Double, pMouseY: Double): Style? {
        if (pMouseX > 0 && pMouseY > 0) {
            if (page!!.hasTitle()) {
                val titleStyle = this.getClickedComponentStyleAtForTitle(
                    page!!.title, BookEntryScreen.PAGE_WIDTH / 2, 0, pMouseX, pMouseY
                )
                if (titleStyle != null) {
                    return titleStyle
                }
            }

            val x = parentScreen.book.bookTextOffsetX
            val y = this.textY + parentScreen.book.bookTextOffsetY
            val width =
                BookEntryScreen.PAGE_WIDTH + parentScreen.book.bookTextOffsetWidth - x //always remove the offset x from the width to avoid overflow
            val height =
                BookEntryScreen.PAGE_HEIGHT + parentScreen.book.bookTextOffsetHeight - y //always remove the offset y from the height to avoid overflow

            val textStyle = this.getClickedComponentStyleAtForTextHolder(
                page!!.text, x, y, width, height, pMouseX, pMouseY
            )
            if (textStyle != null) {
                return textStyle
            }

            //should not do item hover - that is handled by render ingredient, which also makes sure the tooltip does not go beyond the screen.
        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY)
    }

    override fun getTextY(): Int {
        return 40
    }
}
