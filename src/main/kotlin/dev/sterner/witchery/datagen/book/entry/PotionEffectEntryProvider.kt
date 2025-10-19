package dev.sterner.witchery.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.datagen.book.page.BookPotionEffectPageModel
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.world.item.Items

class PotionEffectEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "potion_effect"
    }

    override fun generatePages() {
        this.page(ID) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.${ID}.title.1")
                .withText("${parent.categoryId()}.${ID}.page.1")
        }
        this.page("${ID}_2") {
            BookPotionEffectPageModel.create()
                .withTitle("Effect Modifiers")
                .addItem(Items.BLAZE_POWDER.defaultInstance)
                .addItem(Items.GLISTERING_MELON_SLICE.defaultInstance)
                .addItem(Items.SPIDER_EYE.defaultInstance)
        }

        this.page("${ID}_3") {
            BookPotionEffectPageModel.create()
                .addItem(Items.GHAST_TEAR.defaultInstance)
                .addItem(Items.SUGAR.defaultInstance)
                .addItem(Items.APPLE.defaultInstance)
        }

        this.page("${ID}_4") {
            BookPotionEffectPageModel.create()
                .addItem(Items.BONE_MEAL.defaultInstance)
                .addItem(Items.COAL.defaultInstance)
                .addItem(Items.DANDELION.defaultInstance)
        }
        this.page("${ID}_5") {
            BookPotionEffectPageModel.create()
                .addItem(Items.DIRT.defaultInstance)
                .addItem(WitcheryItems.ENDER_DEW.get().defaultInstance)
                .addItem(Items.LILY_PAD.defaultInstance)
        }
        this.page("${ID}_6") {
            BookPotionEffectPageModel.create()
                .addItem(Items.DEAD_BUSH.defaultInstance)
                .addItem(Items.SAND.defaultInstance)
                .addItem(Items.WHEAT_SEEDS.defaultInstance)
        }
        this.page("${ID}_7") {
            BookPotionEffectPageModel.create()
                .addItem(WitcheryItems.WOLFSBANE.get().defaultInstance)
                .addItem(Items.STRING.defaultInstance)
                .addItem(Items.COBBLESTONE.defaultInstance)
        }
        this.page("${ID}_8") {
            BookPotionEffectPageModel.create()
                .addItem(WitcheryItems.ENT_TWIG.get().defaultInstance)
                .addItem(Items.SLIME_BALL.defaultInstance)
                .addItem(Items.STICK.defaultInstance)
        }
        this.page("${ID}_9") {
            BookPotionEffectPageModel.create()
                .addItem(Items.ENDER_PEARL.defaultInstance)
                .addItem(Items.ROSE_BUSH.defaultInstance)
                .addItem(Items.POPPY.defaultInstance)
        }
        this.page("${ID}_10") {
            BookPotionEffectPageModel.create()
                .addItem(Items.BROWN_MUSHROOM.defaultInstance)
                .addItem(Items.RED_MUSHROOM.defaultInstance)
                .addItem(WitcheryItems.WITCHES_HAT.get().defaultInstance)
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
        return BookIconModel.create(Items.BLAZE_POWDER)
    }

    override fun entryId(): String {
        return ID
    }
}