package dev.sterner.witchery.data_gen.book.entry.vampire

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.core.registry.WitcheryItems

class VampireLevelTwoEntryProvider(val id: String, parent: CategoryProviderBase?) : EntryProvider(parent) {

    override fun generatePages() {
        this.page(id) {
            BookTextPageModel.create()
                .withText("${parent.categoryId()}.$id.page.1")
        }
    }

    override fun entryName(): String {
        return id.replaceFirstChar { it.uppercaseChar() }
    }

    override fun entryDescription(): String {
        return ""
    }

    override fun entryBackground(): Pair<Int, Int> {
        return EntryBackground.DEFAULT
    }

    override fun entryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.TORN_PAGE.get())
    }

    override fun entryId(): String {
        return id
    }
}