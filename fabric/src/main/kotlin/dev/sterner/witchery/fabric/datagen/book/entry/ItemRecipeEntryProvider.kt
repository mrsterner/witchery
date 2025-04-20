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
import net.minecraft.world.item.Item

class ItemRecipeEntryProvider(parent: CategoryProviderBase?, var id: String, var recipePath: String, var item: Item) : EntryProvider(parent) {


    override fun generatePages() {
        this.page(id) {
            BookSpotlightPageModel.create()
                .withItem(item)
                .withTitle("${parent.categoryId()}.$id.title.1")
                .withText("${parent.categoryId()}.$id.page.1")
        }
        this.page("${id}_2") {
            BookCauldronCraftingPageModel.create()
                .withText("${parent.categoryId()}.${id}.title.2")
                .withRecipeId1(Witchery.id("$recipePath/$id"))
                .withTitle1("${parent.categoryId()}.${id}")
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
        return BookIconModel.create(item)
    }

    override fun entryId(): String {
        return id
    }
}