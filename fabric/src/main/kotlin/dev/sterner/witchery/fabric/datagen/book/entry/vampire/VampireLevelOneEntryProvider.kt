package dev.sterner.witchery.fabric.datagen.book.entry.vampire

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems

class VampireLevelOneEntryProvider(val id: String, parent: CategoryProviderBase?) : EntryProvider(parent) {


    override fun generatePages() {
        this.page(id) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$id.title")
                .withText("${parent.categoryId()}.$id.page.1")
                .withUseMarkdownInTitle(true)
        }

        this.page(id) {
            BookTextPageModel.create()
                .withText("${parent.categoryId()}.$id.page.2")
        }

        this.page(id) {
            BookTextPageModel.create()
                .withText("${parent.categoryId()}.$id.page.3")
        }

        this.page("${id}_image") {

            BookImagePageModel.create()
                .withImages(Witchery.id("textures/gui/modonomicon/vampire/${id}.png"))
        }

        this.page(id) {
            BookTextPageModel.create()
                .withText("${parent.categoryId()}.$id.page.4")
        }

        this.page(id) {
            BookTextPageModel.create()
                .withText("${parent.categoryId()}.$id.page.5")
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