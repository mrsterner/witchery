package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.page.BookCauldronBrewingPageModel
import dev.sterner.witchery.registry.WitcheryItems

class SpiritOfOtherwhereEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "spirit_of_otherwhere"
    }

    override fun generatePages() {
        this.page(ID) {
            BookSpotlightPageModel.create()
                .withItem(WitcheryItems.SPIRIT_OF_OTHERWHERE.get())
                .withTitle("${parent.categoryId()}.$ID.title")
                .withText("${parent.categoryId()}.$ID.page.1")
        }

        this.page("${parent.categoryId()}.${ID}.spirit_of_otherwhere") {
            BookCauldronBrewingPageModel.create().withText("${parent.categoryId()}.$ID.title")
                .withRecipeId1(Witchery.id("cauldron_brewing/spirit_of_otherwhere"))
                .withTitle1("${parent.categoryId()}.${ID}.spirit_of_otherwhere")
        }


    }

    override fun entryName(): String {
        return ID.replaceFirstChar { it.uppercaseChar() }
    }

    override fun entryDescription(): String {
        return ""
    }

    override fun entryBackground(): Pair<Int, Int> {
        return EntryBackground.DEFAULT
    }

    override fun entryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.SPIRIT_OF_OTHERWHERE.get())
    }

    override fun entryId(): String {
        return ID
    }
}