package dev.sterner.witchery.data_gen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_gen.book.page.BookCauldronInfusionPageModel
import net.minecraft.world.item.ItemStack

class BrewInfusionEntryProvider(val icon: ItemStack, val id: String, parent: CategoryProviderBase?) : EntryProvider(parent) {

    override fun generatePages() {
        this.page(id) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$id.title.1")
                .withText("${parent.categoryId()}.$id.page.1")
        }

        this.page("${parent.categoryId()}.${id}") {
            BookCauldronInfusionPageModel.create().withText("${parent.categoryId()}.$id.title.1")
                .withRecipeId1(Witchery.id("cauldron_infusion/$id"))
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
        return BookIconModel.create(icon)
    }

    override fun entryId(): String {
        return id
    }
}