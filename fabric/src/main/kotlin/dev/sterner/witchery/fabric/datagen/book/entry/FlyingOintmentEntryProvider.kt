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

class FlyingOintmentEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "flying_ointment"
    }

    override fun generatePages() {
        this.page(ID) {
            BookSpotlightPageModel.create()
                .withItem(WitcheryItems.FLYING_OINTMENT.get())
                .withTitle("${parent.categoryId()}.$ID.title")
                .withText("${parent.categoryId()}.$ID.page.1")
        }

        this.page("${parent.categoryId()}.${ID}.flying_ointment") {
            BookCauldronBrewingPageModel.create().withText("${parent.categoryId()}.$ID.title")
                .withRecipeId1(Witchery.id("cauldron_brewing/flying_ointment"))
                .withTitle1("${parent.categoryId()}.${ID}.flying_ointment")
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
        return BookIconModel.create(WitcheryItems.FLYING_OINTMENT.get())
    }

    override fun entryId(): String {
        return ID
    }
}