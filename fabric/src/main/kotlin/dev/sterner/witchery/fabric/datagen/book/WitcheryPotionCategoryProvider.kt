package dev.sterner.witchery.fabric.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.entry.PotionBeginningEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.PotionCapacityEntryProvider
import dev.sterner.witchery.registry.WitcheryItems


class WitcheryPotionCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "potions"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "_______________d_a________________",
            "__________________________________",
            "_____________b_c_m________________",
            "__________________________________",
            "_______________s__________________",
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

        val introduction = PotionBeginningEntryProvider(this).generate("b")
        addEntry(introduction)

        val capacity = PotionCapacityEntryProvider(
            this)
            .generate("c")
        capacity.addParent(BookEntryParentModel.create(introduction.id).withDrawArrow(true))
        addEntry(capacity)
    }

    override fun categoryName(): String {
        return "potions"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.WITCHERY_POTION.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}