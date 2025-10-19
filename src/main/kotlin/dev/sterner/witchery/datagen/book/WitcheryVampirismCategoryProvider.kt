package dev.sterner.witchery.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.datagen.book.entry.vampire.*
import dev.sterner.witchery.datagen.book.util.advancement
import dev.sterner.witchery.datagen.book.util.requiresAndFollows
import dev.sterner.witchery.core.registry.WitcheryItems


class WitcheryVampirismCategoryProvider(
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

        val vamp1 = VampireLevelOneEntryProvider("vamp_1", this).generate("a")
            .withCondition(advancement(Witchery.id("vampire/1")))

        addEntry(vamp1)

        val vamp2 = VampireLevelTwoEntryProvider("vamp_2", this).generate("b")
            .requiresAndFollows(vamp1, advancement(Witchery.id("vampire/2")))
        addEntry(vamp2)

        val vamp3 = VampireLevelTwoEntryProvider("vamp_3", this).generate("c")
            .requiresAndFollows(vamp2, advancement(Witchery.id("vampire/3")))
        addEntry(vamp3)

        val vamp4 = VampireLevelTwoEntryProvider("vamp_4", this).generate("d")
            .requiresAndFollows(vamp3, advancement(Witchery.id("vampire/4")))
        addEntry(vamp4)

        val vamp5 = VampireLevelTwoEntryProvider("vamp_5", this).generate("e")
            .requiresAndFollows(vamp4, advancement(Witchery.id("vampire/5")))
        addEntry(vamp5)

        val vamp6 = VampireLevelTwoEntryProvider("vamp_6", this).generate("f")
            .requiresAndFollows(vamp5, advancement(Witchery.id("vampire/6")))
        addEntry(vamp6)

        val vamp7 = VampireLevelTwoEntryProvider("vamp_7", this).generate("g")
            .requiresAndFollows(vamp6, advancement(Witchery.id("vampire/7")))
        addEntry(vamp7)

        val vamp8 = VampireLevelTwoEntryProvider("vamp_8", this).generate("h")
            .requiresAndFollows(vamp7, advancement(Witchery.id("vampire/8")))
        addEntry(vamp8)

        val vamp9 = VampireLevelTenEntryProvider("vamp_9", this).generate("i")
            .requiresAndFollows(vamp8, advancement(Witchery.id("vampire/9")))
        addEntry(vamp9)

        val armor = VampireArmorEntryProvider("armor", this).generate("x")
            .requiresAndFollows(vamp1, advancement(Witchery.id("vampire/2")))
        addEntry(armor)

        val cane = VampireCaneEntryProvider("cane", this).generate("p")
            .requiresAndFollows(armor, advancement(Witchery.id("vampire/2")))
        addEntry(cane)

        val sun = VampireSunCollectorEntryProvider(this).generate("u")
            .requiresAndFollows(vamp5, advancement(Witchery.id("vampire/5")))
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