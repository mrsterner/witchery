package dev.sterner.witchery.datagen.book

import com.klikli_dev.modonomicon.api.datagen.SingleBookSubProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookModel
import com.klikli_dev.modonomicon.book.BookFrameOverlay
import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.witchery.Witchery
import java.util.function.BiConsumer

class WitcherySubBookProvider(defaultLang: BiConsumer<String, String>?) :
    SingleBookSubProvider("guidebook", Witchery.MODID, defaultLang) {

    override fun registerDefaultMacros() {
    }

    override fun generateCategories() {
        this.add(WitcheryGeneralCategoryProvider(this).generate())
        this.add(WitcheryBrewingCategoryProvider(this).generate())
        this.add(WitcheryRitualCategoryProvider(this).generate())
        this.add(WitcheryVampirismCategoryProvider(this).generate())
        this.add(WitcheryLycanthropyCategoryProvider(this).generate())
        this.add(WitcheryLichdomCategoryProvider(this).generate())
        this.add(WitcheryBrazierCategoryProvider(this).generate())
        this.add(WitcherySpiritWorldCategoryProvider(this).generate())
    }

    override fun bookName(): String {
        return "guidebook"
    }

    override fun bookTooltip(): String {
        return "${Witchery.MODID}.tooltip"
    }


    override fun additionalSetup(book: BookModel?): BookModel {
        return super.additionalSetup(book)
            .withGenerateBookItem(false)
            .withCustomBookItem(Witchery.id("guidebook"))
            .withBookContentTexture(BOOK_CONTENT)
            .withFrameTexture(BOOK_FRAME)
            .withLeftFrameOverride(LEFT_FRAME_OVERLAY)
            .withRightFrameOverride(RIGHT_FRAME_OVERLAY)
            .withTopFrameOverride(TOP_FRAME_OVERLAY)
            .withBottomFrameOverride(BOTTOM_FRAME_OVERLAY)
    }

    companion object {
        val BOOK_CONTENT = Witchery.id("textures/gui/modonomicon/book_content.png")
        val BOOK_FRAME = Witchery.id("textures/gui/modonomicon/book_frame.png")

        val TOP_FRAME_OVERLAY: BookFrameOverlay = BookFrameOverlay(
            Witchery.id("textures/gui/modonomicon/top.png"),
            256, 256, 72, 7, 0, 4
        )

        val BOTTOM_FRAME_OVERLAY: BookFrameOverlay = BookFrameOverlay(
            Witchery.id("textures/gui/modonomicon/bottom.png"),
            256, 256, 72, 8, 0, -4
        )

        val LEFT_FRAME_OVERLAY: BookFrameOverlay = BookFrameOverlay(
            Witchery.id("textures/gui/modonomicon/left.png"),
            256, 256, 7, 70, 3, 0
        )

        val RIGHT_FRAME_OVERLAY: BookFrameOverlay = BookFrameOverlay(
            Witchery.id("textures/gui/modonomicon/right.png"),
            256, 256, 8, 70, -4, 0
        )
    }
}