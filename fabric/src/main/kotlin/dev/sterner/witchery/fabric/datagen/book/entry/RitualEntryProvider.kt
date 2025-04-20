package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.page.BookRitualPageModel
import dev.sterner.witchery.integration.modonomicon.BookRitualRecipePage
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item

class RitualEntryProvider(val id: String, val item: Item, parent: CategoryProviderBase?) : EntryProvider(parent) {


    override fun generatePages() {
        this.page(id) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$id.title")
                .withText("${parent.categoryId()}.$id.page.1")
        }
        this.page(id) {
            BookRitualPageModel.create()
                .withText("${parent.categoryId()}.$id.page.2")
                .withRecipeId1(Witchery.id("ritual/$id"))
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