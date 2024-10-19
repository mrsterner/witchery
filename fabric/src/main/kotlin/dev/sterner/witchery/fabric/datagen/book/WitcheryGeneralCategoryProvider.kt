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
import dev.sterner.witchery.fabric.datagen.book.entry.*
import dev.sterner.witchery.registry.WitcheryItems


class WitcheryGeneralCategoryProvider(
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
            "________________c_m_______________",
            "___________________w______________",
            "__________________________________",
            "________________b__o_e____________",
            "__________________x_h_____________",
            "________________d_________________",
            "__________________________________",
            "__________________t_______________",
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

        val cauldron = CauldronEntryProvider(this).generate("c")
        cauldron
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(beginning.id)
                ))
            .addParent(BookEntryParentModel.create(beginning.id).withDrawArrow(true))
        addEntry(cauldron)

        val mutandis = MutandisEntryProvider(this).generate("m")
        mutandis
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(cauldron.id),
                    BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("cauldron"))
                )
            )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))
        addEntry(mutandis)

        val distillery = DistilleryEntryProvider(this).generate("d")
        distillery
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(beginning.id),
                    BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("oven"))
                )
            )
            .addParent(BookEntryParentModel.create(beginning.id).withDrawArrow(true))
        addEntry(distillery)

        val whiffOfMagic = WhiffOfMagicEntryProvider(this).generate("w")
        whiffOfMagic
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(mutandis.id),
                    BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("mutandis"))
                )
            )
            .addParent(BookEntryParentModel.create(mutandis.id).withDrawArrow(true))
        whiffOfMagic.addParent(BookEntryParentModel.create(oven.id).withDrawArrow(true))
        addEntry(whiffOfMagic)




        val hintOfRebirth = HintOfRebirthEntryProvider(this).generate("h")
        hintOfRebirth
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(oven.id)
                )
            )
            .addParent(BookEntryParentModel.create(oven.id).withDrawArrow(true))
        addEntry(hintOfRebirth)

        val exhaleOfTheHornedOne = ExhaleOfTheHornedOneEntryProvider(this).generate("e")
        exhaleOfTheHornedOne
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(oven.id)
                )
            )
            .addParent(BookEntryParentModel.create(oven.id).withDrawArrow(true))
        addEntry(exhaleOfTheHornedOne)

        val breathOfTheGoddess = BreathOfTheGoddessEntryProvider(this).generate("x")
        breathOfTheGoddess
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(oven.id)
                )
            )
            .addParent(BookEntryParentModel.create(oven.id).withDrawArrow(true))
        addEntry(breathOfTheGoddess)

        val tearOfTheGoddess = TearOfTheGoddessEntryProvider(this).generate("t")
        tearOfTheGoddess
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(breathOfTheGoddess.id)
                )
            )
            .addParent(BookEntryParentModel.create(breathOfTheGoddess.id).withDrawArrow(true))
        tearOfTheGoddess.addParent(BookEntryParentModel.create(distillery.id).withDrawArrow(true))
        addEntry(tearOfTheGoddess)
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