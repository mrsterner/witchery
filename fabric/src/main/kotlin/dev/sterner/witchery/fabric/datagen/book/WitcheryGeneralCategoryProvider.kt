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
            "______________________kj__________",
            "________________________v_________",
            "________________c_m_y_s__z________",
            "___________________w____g_________",
            "_____________________e_u__________",
            "________________b__o______________",
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

        val beginning = SingleEntryProvider(this, "beginning", WitcheryItems.GUIDEBOOK.get()).generate("b")
        addEntry(beginning)

        val oven = OvenEntryProvider(this).generate("o")
        oven
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(beginning.id)
                )
            )
            .addParent(BookEntryParentModel.create(beginning.id).withDrawArrow(true))
        addEntry(oven)

        val cauldron = DoubleItemEntryProvider(this, "cauldron", WitcheryItems.CAULDRON.get(),  WitcheryItems.COPPER_CAULDRON.get()).generate("c")
        cauldron
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(beginning.id)
                )
            )
            .addParent(BookEntryParentModel.create(beginning.id).withDrawArrow(true))
        addEntry(cauldron)

        val mutandis = ItemRecipeEntryProvider(this, "mutandis", "cauldron_crafting", WitcheryItems.MUTANDIS.get()).generate("m")
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

        val mutandisExtremis = ItemRecipeEntryProvider(this, "mutandis_extremis", "cauldron_crafting", WitcheryItems.MUTANDIS_EXTREMIS.get()).generate("y")
        mutandisExtremis
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(mutandis.id)
                )
            )
            .addParent(BookEntryParentModel.create(mutandis.id).withDrawArrow(true))
        addEntry(mutandisExtremis)

        val spring = ItemRecipeEntryProvider(this, "mutating_spring", "cauldron_crafting", WitcheryItems.MUTATING_SPRING.get()).generate("s")
        spring
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(mutandisExtremis.id)
                )
            )
            .addParent(BookEntryParentModel.create(mutandisExtremis.id).withDrawArrow(true))
        addEntry(spring)

        val grassper = GrassperEntryProvider(this).generate("g")
        grassper
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(spring.id)
                )
            )
            .addParent(BookEntryParentModel.create(spring.id).withDrawArrow(true))
        addEntry(grassper)

        val critter = CritterSnareEntryProvider(this).generate("z")
        critter
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(spring.id)
                )
            )
            .addParent(BookEntryParentModel.create(spring.id).withDrawArrow(true))
        addEntry(critter)

        val louse = ParasyticLouseEntryProvider(this).generate("k")
        louse
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(spring.id)
                )
            )
            .addParent(BookEntryParentModel.create(spring.id).withDrawArrow(true))
        addEntry(louse)

        val owl = OwlEntryProvider(this).generate("j")
        owl
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(spring.id)
                )
            )
            .addParent(BookEntryParentModel.create(spring.id).withDrawArrow(true))
        addEntry(owl)

        val wormwood = WormwoodEntryProvider(this).generate("v")
        wormwood
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(spring.id)
                )
            )
            .addParent(BookEntryParentModel.create(spring.id).withDrawArrow(true))
        addEntry(wormwood)

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

        val whiffOfMagic = SingleItemEntryProvider(this, "whiff_of_magic", WitcheryItems.WHIFF_OF_MAGIC.get()).generate("w")
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


        val hintOfRebirth = SingleItemEntryProvider(this, "hint_of_rebirth", WitcheryItems.HINT_OF_REBIRTH.get()).generate("h")
        hintOfRebirth
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(oven.id)
                )
            )
            .addParent(BookEntryParentModel.create(oven.id).withDrawArrow(true))
        addEntry(hintOfRebirth)

        val exhaleOfTheHornedOne = SingleItemEntryProvider(this, "exhale_of_the_horned_one", WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get()).generate("e")
        exhaleOfTheHornedOne
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(oven.id)
                )
            )
            .addParent(BookEntryParentModel.create(oven.id).withDrawArrow(true))
        addEntry(exhaleOfTheHornedOne)

        val breathOfTheGoddess = SingleItemEntryProvider(this, "breath_of_the_goddess", WitcheryItems.BREATH_OF_THE_GODDESS.get()).generate("x")
        breathOfTheGoddess
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(oven.id)
                )
            )
            .addParent(BookEntryParentModel.create(oven.id).withDrawArrow(true))
        addEntry(breathOfTheGoddess)

        val tearOfTheGoddess = SingleItemEntryProvider(this, "tear_of_the_goddess", WitcheryItems.TEAR_OF_THE_GODDESS.get()).generate("t")
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

        val expansion = FumeExtensionEntryProvider(this).generate("u")
        expansion
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(oven.id)
                )
            )
            .addParent(BookEntryParentModel.create(oven.id).withDrawArrow(true))
        addEntry(expansion)


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