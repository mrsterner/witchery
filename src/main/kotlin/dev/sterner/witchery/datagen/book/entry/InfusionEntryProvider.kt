package dev.sterner.witchery.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.datagen.book.page.BookCauldronBrewingPageModel
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.item.Item

class InfusionEntryProvider(parent: CategoryProviderBase?, var id: String, var item: Item) : EntryProvider(parent) {

    override fun generatePages() {
        this.page(id) {
            BookSpotlightPageModel.create()
                .withItem(item)
                .withTitle("${parent.categoryId()}.$id.title.1")
                .withText("${parent.categoryId()}.$id.page.1")
        }

        this.page("${id}_2") {
            BookCauldronBrewingPageModel.create().withText("${parent.categoryId()}.$id.title.1")
                .withRecipeId1(Witchery.id("cauldron_brewing/$id"))
        }
    }

    override fun entryName(): String {
        return id
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
        return id
    }
}