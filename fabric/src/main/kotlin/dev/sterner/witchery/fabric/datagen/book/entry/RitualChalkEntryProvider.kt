package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.page.BookCauldronCraftingPageModel
import dev.sterner.witchery.registry.WitcheryItems

class RitualChalkEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "ritual_chalk"
    }

    override fun generatePages() {
        this.page(ID) {
            BookSpotlightPageModel.create()
                .withItem(WitcheryItems.RITUAL_CHALK.get())
                .withTitle("${parent.categoryId()}.$ID.title.1")
                .withText("${parent.categoryId()}.$ID.page.1")
        }
        this.page("${parent.categoryId()}.${ID}.golden_chalk") {
            BookCauldronCraftingPageModel.create().withText("${parent.categoryId()}.${ID}.golden_chalk.title.1")
                .withRecipeId1(Witchery.id("cauldron_crafting/golden_chalk"))
                .withTitle1("${parent.categoryId()}.${ID}.golden_chalk")
        }
        this.page("${parent.categoryId()}.${ID}.infernal_chalk") {
            BookCauldronCraftingPageModel.create().withText("${parent.categoryId()}.${ID}.infernal_chalk.title.1")
                .withRecipeId1(Witchery.id("cauldron_crafting/infernal_chalk"))
                .withTitle1("${parent.categoryId()}.${ID}.infernal_chalk")
        }
        this.page("${parent.categoryId()}.${ID}.otherwhere_chalk") {
            BookCauldronCraftingPageModel.create().withText("${parent.categoryId()}.${ID}.otherwhere_chalk.title.1")
                .withRecipeId1(Witchery.id("cauldron_crafting/otherwhere_chalk"))
                .withTitle1("${parent.categoryId()}.${ID}.otherwhere_chalk")
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
        return BookIconModel.create(WitcheryItems.RITUAL_CHALK.get())
    }

    override fun entryId(): String {
        return ID
    }
}