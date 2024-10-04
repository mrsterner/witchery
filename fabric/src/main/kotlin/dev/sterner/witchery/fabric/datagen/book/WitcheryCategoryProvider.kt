package dev.sterner.witchery.fabric.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.entry.BeginningEntryProvider
import net.minecraft.world.item.Items


class WitcheryCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "general"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "________________m_________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________"

        )
    }

    override fun generateEntries() {
        var index = 0

        fun addEntry(entry: BookEntryModel) {
            this.add(entry.withSortNumber(index))
            index++
        }

        val marrow = BeginningEntryProvider(this).generate("m")
        addEntry(marrow)
    }

    override fun categoryName(): String {
        return "general"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(Items.HONEYCOMB)
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}