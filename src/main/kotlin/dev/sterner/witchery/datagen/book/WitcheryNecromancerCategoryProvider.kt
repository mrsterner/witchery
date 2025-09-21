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
import dev.sterner.witchery.fabric.datagen.book.entry.necro.NecroLevelOneEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.necro.NecroLevelTwoEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.vampire.*
import dev.sterner.witchery.fabric.datagen.book.util.advancement
import dev.sterner.witchery.fabric.datagen.book.util.requiresAndFollows
import dev.sterner.witchery.registry.WitcheryItems


class WitcheryNecromancerCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "necromancy"
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
            "_____________a_b_c_d______________",
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

        val necro1 = NecroLevelOneEntryProvider("necro_1", this).generate("a")
            .withCondition(BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                .withEntry("witchery:ritual/infuse_necromancy"),
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("necro/1"))), )

        addEntry(necro1)

        val necro2 = NecroLevelTwoEntryProvider("carving_1", this).generate("b")
            .requiresAndFollows(necro1, advancement(Witchery.id("necro/2")))
        addEntry(necro2)

        val necro3 = NecroLevelTwoEntryProvider("carving_2", this).generate("c")
            .requiresAndFollows(necro2, advancement(Witchery.id("necro/3")))
        addEntry(necro3)

        val necro4 = NecroLevelTwoEntryProvider("carving_3", this).generate("d")
            .requiresAndFollows(necro3, advancement(Witchery.id("necro/4")))
        addEntry(necro4)


    }

    override fun categoryName(): String {
        return "necromancy"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.GRAVESTONE.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withCondition(BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("necro/1")))
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}