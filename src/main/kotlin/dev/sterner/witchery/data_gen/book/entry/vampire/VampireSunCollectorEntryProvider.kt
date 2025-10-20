package dev.sterner.witchery.data_gen.book.entry.vampire

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryItems

class VampireSunCollectorEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "sun_collector"
    }

    override fun generatePages() {
        this.page(ID) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$ID.title.1")
                .withText("${parent.categoryId()}.$ID.page.1")
        }

        this.page("${parent.categoryId()}.$ID.sunlight_collector") {
            BookCraftingRecipePageModel.create()
                .withRecipeId1(Witchery.id("sunlight_collector"))
                .withTitle1("${parent.categoryId()}.$ID.sunlight_collector")
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
        return BookIconModel.create(WitcheryItems.SUN_COLLECTOR.get())
    }

    override fun entryId(): String {
        return ID
    }
}