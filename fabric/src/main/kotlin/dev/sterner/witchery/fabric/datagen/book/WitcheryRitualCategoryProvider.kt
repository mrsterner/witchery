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
import dev.sterner.witchery.fabric.datagen.book.entry.*
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
            "__________________o_______________",
            "________________r_g_______________",
            "__________________i_______________",
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

        val ritualChalk = RitualChalkEntryProvider(this).generate("r")
        ritualChalk.withCondition(
            BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("gypsum"))
        )
        addEntry(ritualChalk)

        val goldenChalk = GoldenChalkEntryProvider(this).generate("g")
        ritualChalk
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

        )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(goldenChalk)


        val otherwhereChalk = OtherwhereChalkEntryProvider(this).generate("o")
        ritualChalk
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(otherwhereChalk)

        val infernalChalk = InfernalChalkEntryProvider(this).generate("i")
        ritualChalk
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(infernalChalk)

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