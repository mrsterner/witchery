package dev.sterner.witchery.fabric.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryParentModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.entry.vampire.*
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
            "_________________p________________",
            "_______________x_____u____________",
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

        val vamp1 = VampireLevelOneEntryProvider("vamp_1",this).generate("a")
        vamp1.withCondition(
            BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/1"))
        )

        addEntry(vamp1)

        val vamp2 = VampireLevelTwoEntryProvider("vamp_2",this).generate("b")
        vamp2
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/2"))
            )
            .addParent(BookEntryParentModel.create(vamp1.id).withDrawArrow(true))
        addEntry(vamp2)

        val vamp3 = VampireLevelTwoEntryProvider("vamp_3",this).generate("c")
        vamp3
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/3"))
            )
            .addParent(BookEntryParentModel.create(vamp2.id).withDrawArrow(true))
        addEntry(vamp3)

        val vamp4 = VampireLevelTwoEntryProvider("vamp_4",this).generate("d")
        vamp4
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/4"))
            )
            .addParent(BookEntryParentModel.create(vamp3.id).withDrawArrow(true))
        addEntry(vamp4)

        val vamp5 = VampireLevelTwoEntryProvider("vamp_5",this).generate("e")
        vamp5
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/5"))
            )
            .addParent(BookEntryParentModel.create(vamp4.id).withDrawArrow(true))
        addEntry(vamp5)

        val vamp6 = VampireLevelTwoEntryProvider("vamp_6",this).generate("f")
        vamp6
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/6"))
            )
            .addParent(BookEntryParentModel.create(vamp5.id).withDrawArrow(true))
        addEntry(vamp6)

        val vamp7 = VampireLevelTwoEntryProvider("vamp_7",this).generate("g")
        vamp7
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/7"))
            )
            .addParent(BookEntryParentModel.create(vamp6.id).withDrawArrow(true))
        addEntry(vamp7)

        val vamp8 = VampireLevelTwoEntryProvider("vamp_8",this).generate("h")
        vamp8
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/8"))
            )
            .addParent(BookEntryParentModel.create(vamp7.id).withDrawArrow(true))
        addEntry(vamp8)

        val vamp9 = VampireLevelTenEntryProvider("vamp_9",this).generate("i")
        vamp9
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/9"))
            )
            .addParent(BookEntryParentModel.create(vamp8.id).withDrawArrow(true))
        addEntry(vamp9)

        val armor = VampireArmorEntryProvider("armor",this).generate("x")
        armor
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/2"))
            )
            .addParent(BookEntryParentModel.create(vamp1.id).withDrawArrow(true))
        addEntry(armor)

        val cane = VampireCaneEntryProvider("cane",this).generate("p")
        cane
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/2"))
            )
            .addParent(BookEntryParentModel.create(armor.id).withDrawArrow(true))
        addEntry(cane)

        val sun = VampireSunCollectorEntryProvider(this).generate("u")
        sun
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("vampire/5"))
            )
            .addParent(BookEntryParentModel.create(vamp5.id).withDrawArrow(true))
        addEntry(sun)
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