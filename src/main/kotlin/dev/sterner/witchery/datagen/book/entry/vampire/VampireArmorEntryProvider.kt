package dev.sterner.witchery.datagen.book.entry.vampire

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookCraftingRecipePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookSmeltingRecipePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryItems

class VampireArmorEntryProvider(val id: String, parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "armor"
    }

    override fun generatePages() {
        this.page(id) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$id.title.1")
                .withText("${parent.categoryId()}.$id.page.1")
        }

        this.page("${parent.categoryId()}.$ID.woven_cruor") {
            BookSmeltingRecipePageModel.create()
                .withRecipeId1(Witchery.id("woven_cruor"))
                .withTitle1("${parent.categoryId()}.$ID.woven_cruor")
        }

        this.page("${parent.categoryId()}.$ID.top_hat") {
            BookCraftingRecipePageModel.create().withText("${parent.categoryId()}.${ID}.top_hat.text")
                .withRecipeId1(Witchery.id("top_hat"))
                .withTitle1("${parent.categoryId()}.$ID.top_hat")
        }

        this.page("${parent.categoryId()}.$ID.dress_coat") {
            BookCraftingRecipePageModel.create().withText("${parent.categoryId()}.${ID}.dress_coat.text")
                .withRecipeId1(Witchery.id("dress_coat"))
                .withTitle1("${parent.categoryId()}.$ID.dress_coat")
        }

        this.page("${parent.categoryId()}.$ID.trousers") {
            BookCraftingRecipePageModel.create().withText("${parent.categoryId()}.${ID}.trousers.text")
                .withRecipeId1(Witchery.id("trousers"))
                .withTitle1("${parent.categoryId()}.$ID.trousers")
        }

        this.page("${parent.categoryId()}.$ID.oxford_boots") {
            BookCraftingRecipePageModel.create().withText("${parent.categoryId()}.${ID}.oxford_boots.text")
                .withRecipeId1(Witchery.id("oxford_boots"))
                .withTitle1("${parent.categoryId()}.$ID.oxford_boots")
        }
    }

    override fun entryName(): String {
        return id.replaceFirstChar { it.uppercaseChar() }
    }

    override fun entryDescription(): String {
        return ""
    }

    override fun entryBackground(): Pair<Int, Int> {
        return EntryBackground.DEFAULT
    }

    override fun entryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.WOVEN_CRUOR.get())
    }

    override fun entryId(): String {
        return id
    }
}