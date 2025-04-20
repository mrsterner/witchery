package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.registry.WitcheryItems

class HungerEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {


    override fun generatePages() {
        this.page("hunger") {
            BookSpotlightPageModel.create()
                .withItem(WitcheryItems.MELLIFLUOUS_HUNGER.get())
                .withTitle("${parent.categoryId()}.hunger.title")
                .withText("${parent.categoryId()}.hunger.page.1")
        }
    }

    override fun entryName(): String {
        return "hunger"
    }

    override fun entryDescription(): String {
        return ""
    }

    override fun entryBackground(): Pair<Int, Int> {
        return EntryBackground.DEFAULT
    }

    override fun entryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.MELLIFLUOUS_HUNGER.get())
    }

    override fun entryId(): String {
        return "hunger"
    }
}