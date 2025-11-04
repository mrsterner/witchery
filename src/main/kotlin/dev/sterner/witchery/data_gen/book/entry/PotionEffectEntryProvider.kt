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
                .addItem(Items.BLAZE_POWDER.defaultInstance, -1)
                .addItem(Items.GLISTERING_MELON_SLICE.defaultInstance, -1)
                .addItem(Items.SPIDER_EYE.defaultInstance, -1)
        }

        this.page("${ID}_3") {
            BookPotionEffectPageModel.create()
                .addItem(Items.GHAST_TEAR.defaultInstance, -1)
                .addItem(Items.SUGAR.defaultInstance, -1)
                .addItem(Items.APPLE.defaultInstance, -1)
        }

        this.page("${ID}_4") {
            BookPotionEffectPageModel.create()
                .addItem(Items.BONE_MEAL.defaultInstance, -1)
                .addItem(Items.COAL.defaultInstance, -1)
                .addItem(Items.DANDELION.defaultInstance, -1)
        }
        this.page("${ID}_5") {
            BookPotionEffectPageModel.create()
                .addItem(Items.DIRT.defaultInstance, -1)
                .addItem(WitcheryItems.ENDER_DEW.get().defaultInstance, -1)
                .addItem(Items.LILY_PAD.defaultInstance, -1)
        }
        this.page("${ID}_6") {
            BookPotionEffectPageModel.create()
                .addItem(Items.DEAD_BUSH.defaultInstance, -1)
                .addItem(Items.SAND.defaultInstance, -1)
                .addItem(Items.WHEAT_SEEDS.defaultInstance, -1)
        }
        this.page("${ID}_7") {
            BookPotionEffectPageModel.create()
                .addItem(WitcheryItems.WOLFSBANE.get().defaultInstance, -1)
                .addItem(Items.STRING.defaultInstance, -1)
                .addItem(Items.COBBLESTONE.defaultInstance, -1)
        }
        this.page("${ID}_8") {
            BookPotionEffectPageModel.create()
                .addItem(WitcheryItems.ENT_TWIG.get().defaultInstance, -1)
                .addItem(Items.SLIME_BALL.defaultInstance, -1)
                .addItem(Items.STICK.defaultInstance, -1)
        }
        this.page("${ID}_9") {
            BookPotionEffectPageModel.create()
                .addItem(Items.ENDER_PEARL.defaultInstance, -2)
                .addItem(Items.ROSE_BUSH.defaultInstance, -2)
                .addItem(Items.POPPY.defaultInstance, -2)
        }
        this.page("${ID}_10") {
            BookPotionEffectPageModel.create()
                .addItem(Items.BROWN_MUSHROOM.defaultInstance, -3)
                .addItem(Items.RED_MUSHROOM.defaultInstance, -3)
                .addItem(WitcheryItems.WITCHES_HAT.get().defaultInstance, -8)
        }
        this.page("${ID}_11") {
            BookPotionEffectPageModel.create()
                .addItem(WitcheryItems.SPECTRAL_DUST.get().defaultInstance, -3)
                .addItem(WitcheryItems.LIFEBLOOD_BERRY.get().defaultInstance, -2)
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