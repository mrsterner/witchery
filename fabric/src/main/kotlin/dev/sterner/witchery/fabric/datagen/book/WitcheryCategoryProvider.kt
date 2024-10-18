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
            "_____________________m_w__________",
            "________________b__o______________",
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

        val beginning = BeginningEntryProvider(this).generate("b")
        addEntry(beginning)

        val oven = OvenEntryProvider(this).generate("o")
        oven
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(beginning.id)
                ))
            .addParent(BookEntryParentModel.create(beginning.id).withDrawArrow(true))
        addEntry(oven)

        val mutandis = MutandisEntryProvider(this).generate("m")
        mutandis
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(oven.id)
                )
            )
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("oven"))
            )
            .addParent(BookEntryParentModel.create(oven.id).withDrawArrow(true))
        addEntry(mutandis)

        val whiffOfMagic = WhiffOfMagicEntryProvider(this).generate("w")
        whiffOfMagic
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(mutandis.id)
                )
            )
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("mutandis"))
            )
            .addParent(BookEntryParentModel.create(mutandis.id).withDrawArrow(true))
        addEntry(whiffOfMagic)
    }

    override fun categoryName(): String {
        return "general"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.IRON_WITCHES_OVEN.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}