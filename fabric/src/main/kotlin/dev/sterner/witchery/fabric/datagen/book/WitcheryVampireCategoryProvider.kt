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
import dev.sterner.witchery.fabric.datagen.book.entry.GoldenChalkEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.InfernalChalkEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.OtherwhereChalkEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.RitualChalkEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.vampire.Vampire1EntryProvider
import dev.sterner.witchery.registry.WitcheryItems


class WitcheryVampireCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "vampirism"
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
            "_____________a_b_c_d_e_f_g_h_i_j__",
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

        val vamp1 = Vampire1EntryProvider("vamp_1",this).generate("a")
        vamp1.withCondition(
            BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/1"))
        )

        addEntry(vamp1)

        val vamp2 = Vampire1EntryProvider("vamp_2",this).generate("b")
        vamp2
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/2"))
            )
            .addParent(BookEntryParentModel.create(vamp1.id).withDrawArrow(true))
        addEntry(vamp2)

        val vamp3 = Vampire1EntryProvider("vamp_3",this).generate("c")
        vamp3
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/3"))
            )
            .addParent(BookEntryParentModel.create(vamp2.id).withDrawArrow(true))
        addEntry(vamp3)

        val vamp4 = Vampire1EntryProvider("vamp_4",this).generate("d")
        vamp4
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/4"))
            )
            .addParent(BookEntryParentModel.create(vamp3.id).withDrawArrow(true))
        addEntry(vamp4)

        val vamp5 = Vampire1EntryProvider("vamp_5",this).generate("e")
        vamp5
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/5"))
            )
            .addParent(BookEntryParentModel.create(vamp4.id).withDrawArrow(true))
        addEntry(vamp5)

        val vamp6 = Vampire1EntryProvider("vamp_6",this).generate("f")
        vamp6
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/6"))
            )
            .addParent(BookEntryParentModel.create(vamp5.id).withDrawArrow(true))
        addEntry(vamp6)

        val vamp7 = Vampire1EntryProvider("vamp_7",this).generate("g")
        vamp7
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/7"))
            )
            .addParent(BookEntryParentModel.create(vamp6.id).withDrawArrow(true))
        addEntry(vamp7)

        val vamp8 = Vampire1EntryProvider("vamp_8",this).generate("h")
        vamp8
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/8"))
            )
            .addParent(BookEntryParentModel.create(vamp7.id).withDrawArrow(true))
        addEntry(vamp8)

        val vamp9 = Vampire1EntryProvider("vamp_9",this).generate("i")
        vamp9
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/9"))
            )
            .addParent(BookEntryParentModel.create(vamp8.id).withDrawArrow(true))
        addEntry(vamp9)

        val vamp10 = Vampire1EntryProvider("vamp_10",this).generate("j")
        vamp10
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/10"))
            )
            .addParent(BookEntryParentModel.create(vamp9.id).withDrawArrow(true))
        addEntry(vamp10)
    }

    override fun categoryName(): String {
        return "vampirism"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.TORN_PAGE.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withCondition(BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/1")))
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}