package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.page.BookDistillingPageModel
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class DreamWeaverEntryProvider(val id: String, val itemStack: Item, parent: CategoryProviderBase?) : EntryProvider(parent) {


    override fun generatePages() {
        this.page(id) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$id.title")
                .withText("${parent.categoryId()}.$id.page.1")
        }

        //this.page("${parent.categoryId()}.${id}") {
        //    BookCraftingRecipePageModel.create().withText("${parent.categoryId()}.${id}.title")
        //        .withRecipeId1(Witchery.id("spinning_wheel/$id"))
        //        .withTitle1("${parent.categoryId()}.${id}.id")
        //}
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
        return BookIconModel.create(itemStack)
    }

    override fun entryId(): String {
        return id
    }
}