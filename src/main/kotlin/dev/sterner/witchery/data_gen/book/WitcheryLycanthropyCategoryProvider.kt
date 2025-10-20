package dev.sterner.witchery.data_gen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_gen.book.entry.werewolf.WerewolfLevelOneEntryProvider
import dev.sterner.witchery.data_gen.book.entry.werewolf.WerewolfLevelTenEntryProvider
import dev.sterner.witchery.data_gen.book.entry.werewolf.WerewolfLevelTwoEntryProvider
import dev.sterner.witchery.data_gen.book.util.advancement
import dev.sterner.witchery.data_gen.book.util.requiresAndFollows
import net.minecraft.world.item.Items


class WitcheryLycanthropyCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "lycanthropy"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "_______________b___d___f___h___j__",
            "_____________a____________________",
            "_________________c___e___g___i____",
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

        val were1 = WerewolfLevelOneEntryProvider("were_1", this).generate("a")
            .withCondition(advancement(Witchery.id("werewolf/1")))

        addEntry(were1)

        val were2 = WerewolfLevelTwoEntryProvider("were_2", this).generate("b")
            .requiresAndFollows(were1, advancement(Witchery.id("werewolf/2")))
        addEntry(were2)

        val were3 = WerewolfLevelTwoEntryProvider("were_3", this).generate("c")
            .requiresAndFollows(were2, advancement(Witchery.id("werewolf/3")))
        addEntry(were3)

        val were4 = WerewolfLevelTwoEntryProvider("were_4", this).generate("d")
            .requiresAndFollows(were3, advancement(Witchery.id("werewolf/4")))
        addEntry(were4)

        val were5 = WerewolfLevelTwoEntryProvider("were_5", this).generate("e")
            .requiresAndFollows(were4, advancement(Witchery.id("werewolf/5")))
        addEntry(were5)

        val were6 = WerewolfLevelTwoEntryProvider("were_6", this).generate("f")
            .requiresAndFollows(were5, advancement(Witchery.id("werewolf/6")))
        addEntry(were6)

        val were7 = WerewolfLevelTwoEntryProvider("were_7", this).generate("g")
            .requiresAndFollows(were6, advancement(Witchery.id("werewolf/7")))
        addEntry(were7)

        val were8 = WerewolfLevelTwoEntryProvider("were_8", this).generate("h")
            .requiresAndFollows(were7, advancement(Witchery.id("werewolf/8")))
        addEntry(were8)

        val were9 = WerewolfLevelTenEntryProvider("were_9", this).generate("i")
            .requiresAndFollows(were8, advancement(Witchery.id("werewolf/9")))
        addEntry(were9)
    }

    override fun categoryName(): String {
        return "lycanthropy"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(Items.MUTTON)
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withCondition(BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("werewolf/1")))
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}