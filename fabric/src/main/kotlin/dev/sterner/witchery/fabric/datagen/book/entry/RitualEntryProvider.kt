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

class RitualEntryProvider(parent: CategoryProviderBase?, val id: String, val icon: Item) : EntryProvider(parent) {

    override fun generatePages() {
        this.page(id) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$id.title.1")
                .withText("${parent.categoryId()}.$id.page.1")
        }
        this.page("${id}_2") {
            BookRitualPageModel.create()
                .withRecipeId1(Witchery.id("ritual/$id"))
                .withText("${parent.categoryId()}.$id.page.2")
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
        return BookIconModel.create(icon)
    }

    override fun entryId(): String {
        return id
    }
}