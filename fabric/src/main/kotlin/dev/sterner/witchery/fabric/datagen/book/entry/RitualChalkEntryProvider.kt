package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookRecipePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.page.BookCauldronCraftingPageModel
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.item.Items

class RitualChalkEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "ritual_chalk"
    }

    override fun generatePages() {
        this.page(ID) {
            BookTextPageModel.create()
                .withTitle("$ID.title")
                .withText("$ID.page1")
        }
        this.page("${ID}golden_chalk") {
            BookCauldronCraftingPageModel.create().withText("${ID}golden_chalk.title")
                .withRecipeId1(Witchery.id("cauldron_crafting/golden_chalk"))
                .withTitle1("${ID}golden_chalk")
        }
        this.page("${ID}infernal_chalk") {
            BookCauldronCraftingPageModel.create().withText("${ID}infernal_chalk.title")
                .withRecipeId1(Witchery.id("cauldron_crafting/infernal_chalk"))
                .withTitle1("${ID}infernal_chalk")
        }
        this.page("${ID}otherwhere_chalk") {
            BookCauldronCraftingPageModel.create().withText("${ID}otherwhere_chalk.title")
                .withRecipeId1(Witchery.id("cauldron_crafting/otherwhere_chalk"))
                .withTitle1("${ID}otherwhere_chalk")
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