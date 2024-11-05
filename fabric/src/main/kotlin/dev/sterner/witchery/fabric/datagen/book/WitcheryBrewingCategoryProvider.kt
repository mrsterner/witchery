package dev.sterner.witchery.fabric.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAndConditionModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookEntryReadConditionModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.entry.CauldronEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.RedstoneSoupEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.RitualChalkEntryProvider
import dev.sterner.witchery.registry.WitcheryItems


class WitcheryBrewingCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "brewing"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "________________h_________________",
            "__________________________________",
            "________________c_r_______________",
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

        val cauldron = CauldronEntryProvider(this).generate("c")
        cauldron.withCondition(
            BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("cauldron"))
        )
        addEntry(cauldron)

        val redstoneSoup = RedstoneSoupEntryProvider(this).generate("r")
        redstoneSoup
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(cauldron.id)
                )

            )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))
        addEntry(redstoneSoup)

        val ritualChalk = RitualChalkEntryProvider(this).generate("h")
        ritualChalk.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(cauldron.id)
            )

        )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))

        addEntry(ritualChalk)
    }

    override fun categoryName(): String {
        return "brewing"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.CAULDRON.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}