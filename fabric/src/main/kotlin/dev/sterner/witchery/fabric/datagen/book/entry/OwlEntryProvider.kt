package dev.sterner.witchery.fabric.datagen.book.entry

import com.klikli_dev.modonomicon.api.datagen.CategoryProviderBase
import com.klikli_dev.modonomicon.api.datagen.EntryBackground
import com.klikli_dev.modonomicon.api.datagen.EntryProvider
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookEntityPageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookImagePageModel
import com.klikli_dev.modonomicon.api.datagen.book.page.BookTextPageModel
import com.mojang.datafixers.util.Pair
import dev.sterner.witchery.Witchery
import net.minecraft.world.item.Items

class OwlEntryProvider(parent: CategoryProviderBase?) : EntryProvider(parent) {

    companion object {
        val ID = "owl"
    }

    override fun generatePages() {
        this.page(ID) {
            BookTextPageModel.create()
                .withTitle("${parent.categoryId()}.$ID.title")
                .withText("${parent.categoryId()}.$ID.page.1")
        }
        this.page("entity") {
            BookEntityPageModel.create()
                .withEntityId("witchery:owl")
        }
        this.page("owl_image") {
            BookImagePageModel.create()
                .withImages(Witchery.id("textures/gui/modonomicon/images/owl_image.png"))
        }
    }

    override fun entryName(): String {
        return ID
    }

    override fun entryDescription(): String {
        return ""
    }

    override fun entryBackground(): Pair<Int, Int> {
        return EntryBackground.DEFAULT
    }

    override fun entryIcon(): BookIconModel {
        return BookIconModel.create(Items.BAT_SPAWN_EGG)
    }

    override fun entryId(): String {
        return ID
    }
}