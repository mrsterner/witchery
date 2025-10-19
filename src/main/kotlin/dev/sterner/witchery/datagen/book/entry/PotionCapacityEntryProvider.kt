package dev.sterner.witchery.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.klikli_dev.modonomicon.book.BookTextHolder
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.datagen.book.page.BookPotionPageModel
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Items

class PotionCapacityEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "potion_capacity"
    }

    override fun generatePages() {
        this.page(ID) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.${ID}.title")
                .withText("${parent.categoryId()}.${ID}.page.1")
        }
        this.page("${ID}_2") {
            BookPotionPageModel.create()
                .withTitle("Capacity Modifiers")
                .addItem(
                    WitcheryItems.MANDRAKE_ROOT.get().defaultInstance, BookTextHolder(
                        Component.translatable("witchery.potion_crafting.mandrake", 1, 50)
                    )
                )
                .addItem(
                    WitcheryItems.TEAR_OF_THE_GODDESS.get().defaultInstance, BookTextHolder(
                        Component.translatable("witchery.potion_crafting.tear_of_the_goddess", 1, 100)
                    )
                )
                .addItem(
                    WitcheryItems.PHANTOM_VAPOR.get().defaultInstance, BookTextHolder(
                        Component.translatable("witchery.potion_crafting.phantom_vapour", 1, 150)
                    )
                )
                .addItem(
                    Items.AMETHYST_SHARD.defaultInstance, BookTextHolder(
                        Component.translatable("witchery.potion_crafting.amethyst", 1, 200)
                    )
                )
                .addItem(
                    Items.HEART_OF_THE_SEA.defaultInstance, BookTextHolder(
                        Component.translatable("witchery.potion_crafting.heart_of_the_sea", 1, 250)
                    )
                )

        }
        this.page("${ID}_3") {
            BookPotionPageModel.create()
                .addItem(
                    Items.NETHER_STAR.defaultInstance, BookTextHolder(
                        Component.translatable("witchery.potion_crafting.nether_star", 3, 0)
                    )
                )
                .addItem(
                    WitcheryItems.PENTACLE.get().defaultInstance, BookTextHolder(
                        Component.translatable("witchery.potion_crafting.pentacle", 4, 0)
                    )
                )
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
        return BookIconModel.create(WitcheryItems.MANDRAKE_ROOT.get())
    }

    override fun entryId(): String {
        return ID
    }
}