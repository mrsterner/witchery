package dev.sterner.witchery.fabric.datagen.book.entry.vampire

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems

class VampireCaneEntryProvider(val id: String, parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "cane"
    }

    override fun generatePages() {
        this.page(id) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$id.title.1")
                .withText("${parent.categoryId()}.$id.page.1")
        }

        this.page("${parent.categoryId()}.$ID.cane") {
            BookCraftingRecipePageModel.create()
                .withRecipeId1(Witchery.id("cane_sword"))
                .withTitle1("${parent.categoryId()}.$ID.cane")
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
        return BookIconModel.create(WitcheryItems.CANE_SWORD.get())
    }

    override fun entryId(): String {
        return id
    }
}