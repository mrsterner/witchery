package dev.sterner.witchery.integration.modonomicon

import com.klikli_dev.modonomicon.client.gui.book.entry.BookEntryScreen
import com.klikli_dev.modonomicon.client.render.page.BookPageRenderer
import com.klikli_dev.modonomicon.client.render.page.PageWithTextRenderer
import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style


open class BookPotionEffectPageRenderer(page: BookPotionEffectPage) :
    BookPageRenderer<BookPotionEffectPage>(page),
    PageWithTextRenderer {

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, ticks: Float) {
        var currentY = this.textY - 40

        val itemHeight = 18
        val offsetX = 0
        val font = Minecraft.getInstance().font

        for (item in page!!.items) {
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

            val capacity = item.second.first
            val capacityText = capacity.toString()
            val textWidth = font.width(capacityText)

            val capacityX = offsetX + 24 - textWidth + 1
            val capacityY = currentY - 1

            guiGraphics.drawString(
                font,
                capacityText,
                capacityX,
                capacityY,
                0x000000,
                false
            )
            val width = font.width(capacityText)
            val height = font.lineHeight

            val hovered = mouseX >= capacityX && mouseX <= capacityX + width &&
                    mouseY >= capacityY && mouseY <= capacityY + height

            if (hovered) {
                guiGraphics.renderTooltip(
                    font,
                    Component.literal("Capacity: $capacity"),
                    mouseX, mouseY
                )
            }

            renderBookTextHolder(
                guiGraphics,
                item.second.third,
                font,
                offsetX + 18 + 11,
                currentY + 8,
                BookEntryScreen.PAGE_WIDTH - 5,
                BookEntryScreen.PAGE_HEIGHT - currentY
            )

            renderBookTextHolder(
                guiGraphics,
                item.second.second,
                font,
                offsetX + 18 + 11 - 24,
                currentY + 4 + 18 + 4,
                BookEntryScreen.PAGE_WIDTH - 5,
                BookEntryScreen.PAGE_HEIGHT - currentY
            )

            currentY += itemHeight + 7 + 28
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
                BookEntryScreen.PAGE_WIDTH + parentScreen.book.bookTextOffsetWidth - x
            val height =
                BookEntryScreen.PAGE_HEIGHT + parentScreen.book.bookTextOffsetHeight - y

            val textStyle = this.getClickedComponentStyleAtForTextHolder(
                page!!.text, x, y, width, height, pMouseX, pMouseY
            )
            if (textStyle != null) {
                return textStyle
            }

        }
        return super.getClickedComponentStyleAt(pMouseX, pMouseY)
    }

    override fun getTextY(): Int {
        return 40
    }
}