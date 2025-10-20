package dev.sterner.witchery.data_gen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.data_gen.book.page.BookPotionEffectPageModel
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.world.item.Items

class PotionEffectTypeEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "potion_effect_type"
    }

    override fun generatePages() {
        this.page(ID) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.${ID}.title.1")
                .withText("${parent.categoryId()}.${ID}.page.1")
        }

        this.page("${ID}_2") {
            BookPotionEffectPageModel.create()
                .addItem(Items.GOLD_NUGGET.defaultInstance)
                .addItem(Items.FERMENTED_SPIDER_EYE.defaultInstance)
                .addItem(WitcheryItems.SPANISH_MOSS.get().defaultInstance)
        }

        this.page("${ID}_3") {
            BookPotionEffectPageModel.create()
                .addItem(Items.GLOWSTONE_DUST.defaultInstance)
                .addItem(Items.BLAZE_ROD.defaultInstance)
                .addItem(WitcheryItems.ATTUNED_STONE.get().defaultInstance)
        }

        this.page("${ID}_4") {
            BookPotionEffectPageModel.create()
                .addItem(Items.REDSTONE.defaultInstance)
                .addItem(Items.OBSIDIAN.defaultInstance)
                .addItem(Items.GUNPOWDER.defaultInstance)
        }

        this.page("${ID}_5") {
            BookPotionEffectPageModel.create()
                .addItem(Items.COCOA_BEANS.defaultInstance)
                .addItem(WitcheryItems.WISPY_COTTON.get().defaultInstance)
                .addItem(Items.DRAGON_BREATH.defaultInstance)
        }
        this.page("${ID}_6") {
            BookPotionEffectPageModel.create()
                .addItem(WitcheryItems.BELLADONNA_FLOWER.get().defaultInstance)
                .addItem(Items.LAPIS_LAZULI.defaultInstance)
                .addItem(Items.END_STONE.defaultInstance)
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
        return BookIconModel.create(Items.GUNPOWDER)
    }

    override fun entryId(): String {
        return ID
    }
}