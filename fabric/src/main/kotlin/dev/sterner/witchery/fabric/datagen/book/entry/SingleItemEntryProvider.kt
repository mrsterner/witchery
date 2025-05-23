package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSpotlightPageModel
import com.mojang.datafixers.util.Pair
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class SingleItemEntryProvider(parent: CategoryProviderBase?, var id: String, var itemStack: ItemStack) : EntryProvider(parent) {

    constructor(parent: CategoryProviderBase?, id: String, item: Item): this(parent, id, item.defaultInstance)

    override fun generatePages() {
        this.page(id) {
            BookSpotlightPageModel.create()
                .withItem(itemStack)
                .withTitle("${parent.categoryId()}.$id.title.1")
                .withText("${parent.categoryId()}.$id.page.1")
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
        return BookIconModel.create(itemStack)
    }

    override fun entryId(): String {
        return id
    }
}