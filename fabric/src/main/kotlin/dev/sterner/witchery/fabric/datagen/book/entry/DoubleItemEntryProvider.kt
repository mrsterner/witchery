package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.item.Item

class DoubleItemEntryProvider(parent: CategoryProviderBase?, var id: String, var item1: Item, var item2: Item) : EntryProvider(parent) {

    override fun generatePages() {
        this.page(id) {
            BookSpotlightPageModel.create()
                .withItem(item1)
                .withTitle("${parent.categoryId()}.$id.title.1")
                .withText("${parent.categoryId()}.$id.page.1")
        }

        this.page("${id}_2") {
            BookSpotlightPageModel.create()
                .withItem(item2)
                .withTitle("${parent.categoryId()}.$id.title.2")
                .withText("${parent.categoryId()}.$id.page.2")
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
        return BookIconModel.create(item1)
    }

    override fun entryId(): String {
        return id
    }
}