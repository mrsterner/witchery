package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import net.minecraft.world.item.Items

class BeginningEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "beginning"
    }

    override fun generatePages() {
        this.page("beginning.1") {
            BookTextPageModel.create()
                .withTitle("beginning.title")
                .withText("beginning.page.1")
        }
        this.page("beginning.2") {
            BookTextPageModel.create()
                .withText("beginning.page.2")
        }
        this.page("beginning.3") {
            BookTextPageModel.create()
                .withText("beginning.page.3")
        }
    }

    override fun entryName(): String {
        return "Beginning"
    }

    override fun entryDescription(): String {
        return "The microwaved banana of this mod"
    }

    override fun entryBackground(): Pair<Int, Int> {
        return EntryBackground.DEFAULT
    }

    override fun entryIcon(): BookIconModel {
        return BookIconModel.create(Items.HONEYCOMB)
    }

    override fun entryId(): String {
        return ID
    }
}