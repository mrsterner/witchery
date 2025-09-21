package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery

class SoulCageEntryProvider(parent: CategoryProviderBase?, var id: String) : EntryProvider(parent) {

    override fun generatePages() {
        this.page(id) {
            BookSpotlightPageModel.create()
                .withTitle("${parent.categoryId()}.$id.title.1")
                .withText("${parent.categoryId()}.$id.page.1")
        }

        this.page("${id}_2") {
            BookImagePageModel.create()
                .withImages(Witchery.id("textures/gui/modonomicon/images/soul_cage.png"))
        }
    }

    override fun entryName(): String {
        return id
    }

    override fun entryDescription(): String {
        return ""
    }

    override fun entryBackground(): Pair<Int, Int> {
        return EntryBackground.DEFAULT
    }

    override fun entryIcon(): BookIconModel {
        return BookIconModel.create(Witchery.id("textures/item/soul_cage.png"))
    }

    override fun entryId(): String {
        return id
    }
}