package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.entry.OvenEntryProvider.Companion
import dev.sterner.witchery.fabric.datagen.book.page.BookDistillingPageModel
import dev.sterner.witchery.fabric.datagen.book.page.BookOvenFumingPageModel
import dev.sterner.witchery.integration.modonomicon.BookDistillingRecipePage
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.item.Items

class DistilleryEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "distillery"
    }

    override fun generatePages() {
        this.page(ID) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$ID.title")
                .withText("${parent.categoryId()}.$ID.page.1")
        }

        this.page("${parent.categoryId()}.${ID}.oil_of_vitriol_gypsum") {
            BookDistillingPageModel.create().withText("${parent.categoryId()}.${ID}oil_of_vitriol_gypsum.title")
                .withRecipeId1(Witchery.id("distillery_crafting/oil_of_vitriol_gypsum"))
                .withTitle1("${parent.categoryId()}.${ID}.oil_of_vitriol_gypsum")
        }

        this.page("${parent.categoryId()}.${ID}.demons_blood") {
            BookDistillingPageModel.create().withText("${parent.categoryId()}.${ID}demons_blood.title")
                .withRecipeId1(Witchery.id("distillery_crafting/demons_blood"))
                .withTitle1("${parent.categoryId()}.${ID}.demons_blood")
        }

        this.page("${parent.categoryId()}.${ID}.ender_dew") {
            BookDistillingPageModel.create().withText("${parent.categoryId()}.${ID}ender_dew.title")
                .withRecipeId1(Witchery.id("distillery_crafting/ender_dew"))
                .withTitle1("${parent.categoryId()}.${ID}.ender_dew")
        }

        this.page("${parent.categoryId()}.${ID}.phantom_vapor") {
            BookDistillingPageModel.create().withText("${parent.categoryId()}.${ID}phantom_vapor.title")
                .withRecipeId1(Witchery.id("distillery_crafting/phantom_vapor"))
                .withTitle1("${parent.categoryId()}.${ID}.phantom_vapor")
        }

        this.page("${parent.categoryId()}.${ID}.reek_of_misfortune") {
            BookDistillingPageModel.create().withText("${parent.categoryId()}.${ID}reek_of_misfortune.title")
                .withRecipeId1(Witchery.id("distillery_crafting/reek_of_misfortune_glowstone"))
                .withTitle1("${parent.categoryId()}.${ID}.reek_of_misfortune")
        }
        this.page("${parent.categoryId()}.${ID}.refined_evil") {
            BookDistillingPageModel.create().withText("${parent.categoryId()}.${ID}refined_evil.title")
                .withRecipeId1(Witchery.id("distillery_crafting/refined_evil"))
                .withTitle1("${parent.categoryId()}.${ID}.refined_evil")
        }
        this.page("${parent.categoryId()}.${ID}.tear_and_whiff") {
            BookDistillingPageModel.create().withText("${parent.categoryId()}.${ID}tear_and_whiff.title")
                .withRecipeId1(Witchery.id("distillery_crafting/tear_and_whiff"))
                .withTitle1("${parent.categoryId()}.${ID}.tear_and_whiff")
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
        return BookIconModel.create(WitcheryItems.DISTILLERY.get())
    }

    override fun entryId(): String {
        return ID
    }
}