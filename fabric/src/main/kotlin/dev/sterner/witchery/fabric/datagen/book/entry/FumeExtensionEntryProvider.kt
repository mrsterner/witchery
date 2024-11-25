package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.page.BookOvenFumingPageModel
import dev.sterner.witchery.registry.WitcheryItems

class FumeExtensionEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "fume_expansion"
    }

    override fun generatePages() {
        this.page(ID) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$ID.title")
                .withText("${parent.categoryId()}.$ID.page.1")
        }
        this.page("${parent.categoryId()}.${ID}") {
            BookCraftingRecipePageModel.create().withText("${parent.categoryId()}.${ID}.title")
                .withRecipeId1(Witchery.id("iron_witches_oven_fume_extension"))
                .withTitle1("${parent.categoryId()}.${ID}")
        }
        this.page("${parent.categoryId()}.${ID}.copper") {
            BookCraftingRecipePageModel.create().withText("${parent.categoryId()}.${ID}.title")
                .withRecipeId1(Witchery.id("copper_witches_oven_fume_extension"))
                .withTitle1("${parent.categoryId()}.${ID}")
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
        return BookIconModel.create(WitcheryItems.IRON_WITCHES_OVEN_FUME_EXTENSION.get())
    }

    override fun entryId(): String {
        return ID
    }
}