package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.page.BookCauldronBrewingPageModel
import dev.sterner.witchery.registry.WitcheryItems

class GhostOfTheLightEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "ghost_of_the_light"
    }

    override fun generatePages() {
        this.page(ID) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$ID.title")
                .withText("${parent.categoryId()}.$ID.page.1")
        }

        this.page("${parent.categoryId()}.${ID}.ghost_of_the_light") {
            BookCauldronBrewingPageModel.create().withText("${parent.categoryId()}.$ID.title")
                .withRecipeId1(Witchery.id("cauldron_brewing/ghost_of_the_light"))
                .withTitle1("${parent.categoryId()}.${ID}.ghost_of_the_light")
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
        return BookIconModel.create(WitcheryItems.GHOST_OF_THE_LIGHT.get())
    }

    override fun entryId(): String {
        return ID
    }
}