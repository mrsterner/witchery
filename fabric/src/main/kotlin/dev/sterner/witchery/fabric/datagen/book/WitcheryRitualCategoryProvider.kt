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
import dev.sterner.witchery.fabric.datagen.WitcheryAdvancementProvider
import dev.sterner.witchery.fabric.datagen.book.entry.BeginningEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.MutandisEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.OvenEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.WhiffOfMagicEntryProvider
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.item.Items


class WitcheryRitualCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "ritual"
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
            "________________r_________________",
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

        val ritualChalk = BeginningEntryProvider(this).generate("r")
        ritualChalk.withCondition(
            BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("gypsum"))
        )
        addEntry(ritualChalk)

    }

    override fun categoryName(): String {
        return "ritual"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.RITUAL_CHALK.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}