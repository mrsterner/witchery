package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.entry.MutandisEntryProvider.Companion
import dev.sterner.witchery.fabric.datagen.book.page.BookCauldronCraftingPageModel
import dev.sterner.witchery.fabric.datagen.book.page.BookOvenFumingPageModel
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.item.Items

class OvenEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "oven"
    }

    override fun generatePages() {
        this.page(ID) {
            BookTextPageModel.create()
                .withTitle("$ID.title")
                .withText("$ID.page1")
        }
        this.page("${ID}breath_of_the_goddess") {
            BookOvenFumingPageModel.create().withText("${ID}breath_of_the_goddess.title")
                .withRecipeId1(Witchery.id("oven/breath_of_the_goddess"))
                .withTitle1("${ID}breath_of_the_goddess")
        }
        this.page("${ID}breath_of_the_goddess") {
            BookOvenFumingPageModel.create().withText("${ID}breath_of_the_goddess.title")
                .withRecipeId1(Witchery.id("oven/breath_of_the_goddess2"))
                .withTitle1("${ID}breath_of_the_goddess")
        }

        this.page("${ID}hint_of_rebirth") {
            BookOvenFumingPageModel.create().withText("${ID}hint_of_rebirth.title")
                .withRecipeId1(Witchery.id("oven/hint_of_rebirth"))
                .withTitle1("${ID}hint_of_rebirth")
        }
        this.page("${ID}hint_of_rebirth") {
            BookOvenFumingPageModel.create().withText("${ID}hint_of_rebirth.title")
                .withRecipeId1(Witchery.id("oven/hint_of_rebirth2"))
                .withTitle1("${ID}hint_of_rebirth")
        }

        this.page("${ID}exhale_of_the_horned_one") {
            BookOvenFumingPageModel.create().withText("${ID}exhale_of_the_horned_one.title")
                .withRecipeId1(Witchery.id("oven/exhale_of_the_horned_one"))
                .withTitle1("${ID}exhale_of_the_horned_one")
        }
        this.page("${ID}exhale_of_the_horned_one") {
            BookOvenFumingPageModel.create().withText("${ID}exhale_of_the_horned_one.title")
                .withRecipeId1(Witchery.id("oven/exhale_of_the_horned_one2"))
                .withTitle1("${ID}exhale_of_the_horned_one")
        }
        this.page("${ID}foul_fume_logs") {
            BookOvenFumingPageModel.create().withText("${ID}foul_fume_logs.title")
                .withRecipeId1(Witchery.id("oven/foul_fume_logs"))
                .withTitle1("${ID}foul_fume_logs")
        }
        this.page("${ID}whiff_of_magic") {
            BookOvenFumingPageModel.create().withText("${ID}whiff_of_magic.title")
                .withRecipeId1(Witchery.id("oven/whiff_of_magic"))
                .withTitle1("${ID}whiff_of_magic")
        }
        this.page("${ID}odor_of_purity") {
            BookOvenFumingPageModel.create().withText("${ID}odor_of_purity.title")
                .withRecipeId1(Witchery.id("oven/odor_of_purity"))
                .withTitle1("${ID}odor_of_purity")
        }
        this.page("${ID}reek_of_misfortune") {
            BookOvenFumingPageModel.create().withText("${ID}reek_of_misfortune.title")
                .withRecipeId1(Witchery.id("oven/reek_of_misfortune"))
                .withTitle1("${ID}reek_of_misfortune")
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
        return BookIconModel.create(WitcheryItems.IRON_WITCHES_OVEN.get())
    }

    override fun entryId(): String {
        return ID
    }
}