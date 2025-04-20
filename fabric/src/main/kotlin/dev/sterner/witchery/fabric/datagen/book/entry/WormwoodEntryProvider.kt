package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems

class WormwoodEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "wormwood"
    }

    override fun generatePages() {
        this.page(ID) {
            BookSpotlightPageModel.create()
                .withItem(WitcheryItems.WORMWOOD.get())
                .withTitle("${parent.categoryId()}.$ID.title.1")
                .withText("${parent.categoryId()}.$ID.page.1")
        }
        this.page("wormwood_image") {
            BookImagePageModel.create()
                .withImages(Witchery.id("textures/gui/modonomicon/images/wormwood_image.png"))
        }
    }

    override fun entryName(): String {
        return ID
    }

    override fun entryDescription(): String {
        return ""
    }

    override fun entryBackground(): Pair<Int, Int> {
        return EntryBackground.DEFAULT
    }

    override fun entryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.WORMWOOD.get())
    }

    override fun entryId(): String {
        return ID
    }
}