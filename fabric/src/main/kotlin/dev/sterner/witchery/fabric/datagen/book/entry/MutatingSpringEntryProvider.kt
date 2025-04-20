package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.page.BookCauldronCraftingPageModel
import dev.sterner.witchery.registry.WitcheryItems

class MutatingSpringEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "mutating_spring"
    }

    override fun generatePages() {
        this.page(ID) {
            BookSpotlightPageModel.create()
                .withItem(WitcheryItems.MUTATING_SPRING.get())
                .withTitle("${parent.categoryId()}.$ID.title")
                .withText("${parent.categoryId()}.$ID.page.1")
        }
        this.page("${parent.categoryId()}.${ID}") {
            BookCauldronCraftingPageModel.create().withText("${parent.categoryId()}.${ID}.title")
                .withRecipeId1(Witchery.id("cauldron_crafting/mutating_spring"))
                .withTitle1("${parent.categoryId()}.${ID}")
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
        return BookIconModel.create(WitcheryItems.MUTATING_SPRING.get())
    }

    override fun entryId(): String {
        return ID
    }
}