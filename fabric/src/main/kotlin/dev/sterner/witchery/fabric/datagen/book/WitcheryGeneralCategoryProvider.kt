package dev.sterner.witchery.fabric.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.entry.*
import dev.sterner.witchery.fabric.datagen.book.util.EntryProviders
import dev.sterner.witchery.fabric.datagen.book.util.alsoFollows
import dev.sterner.witchery.fabric.datagen.book.util.requiresAndFollows
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

        val beginning = EntryProviders.single(this, "beginning", WitcheryItems.GUIDEBOOK.get()).generate("b")
        addEntry(beginning)

        val oven = OvenEntryProvider(this).generate("o")
            .requiresAndFollows(beginning)
        addEntry(oven)

        val cauldron = EntryProviders.doubleItem(
            this,
            "cauldron",
            WitcheryItems.CAULDRON.get(),
            WitcheryItems.COPPER_CAULDRON.get(),
            noSecondTitle = true
        ).generate("c")
            .requiresAndFollows(beginning)
        addEntry(cauldron)

        val mutandis = EntryProviders.recipe(this, "mutandis", WitcheryItems.MUTANDIS.get(), "cauldron_crafting").generate("m")
            .requiresAndFollows(cauldron, BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("cauldron")))
        addEntry(mutandis)

        val mutandisExtremis = EntryProviders.recipe(
            this,
            "mutandis_extremis",
            WitcheryItems.MUTANDIS_EXTREMIS.get(),
            "cauldron_crafting"
        ).generate("y")
            .requiresAndFollows(mutandis)
        addEntry(mutandisExtremis)

        val spring = EntryProviders.recipe(
            this,
            "mutating_spring",
            WitcheryItems.MUTATING_SPRING.get(),
            "cauldron_crafting"
        ).generate("s")
            .requiresAndFollows(mutandisExtremis)
        addEntry(spring)

        val grassper = EntryProviders.textWithImage(
            this,
            "grassper",
            WitcheryItems.GRASSPER.get(),
            "textures/gui/modonomicon/images/grassper_image.png"
        ).generate("g")
            .requiresAndFollows(spring)
        addEntry(grassper)

        val critter = EntryProviders.textWithImage(
            this,
            "critter_snare",
            WitcheryItems.CRITTER_SNARE.get(),
            "textures/gui/modonomicon/images/critter_snare_image.png"
        ).generate("z")
            .requiresAndFollows(spring)
        addEntry(critter)

        val louse = ParasyticLouseEntryProvider(this).generate("k")
            .requiresAndFollows(spring)
        addEntry(louse)

        val owl = OwlEntryProvider(this).generate("j")
            .requiresAndFollows(spring)
        addEntry(owl)

        val wormwood = WormwoodEntryProvider(this).generate("v")
            .requiresAndFollows(spring)
        addEntry(wormwood)

        val distillery = DistilleryEntryProvider(this).generate("d")
            .requiresAndFollows(beginning, BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("oven")))
        addEntry(distillery)

        val whiffOfMagic = EntryProviders.single(this, "whiff_of_magic", WitcheryItems.WHIFF_OF_MAGIC.get()).generate("w")
            .requiresAndFollows(mutandis, BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("mutandis")))
            .alsoFollows(oven)
        addEntry(whiffOfMagic)

        val hintOfRebirth = EntryProviders.single(this, "hint_of_rebirth", WitcheryItems.HINT_OF_REBIRTH.get()).generate("h")
            .requiresAndFollows(oven)
        addEntry(hintOfRebirth)

        val exhaleOfTheHornedOne = EntryProviders.single(
            this,
            "exhale_of_the_horned_one",
            WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get()
        ).generate("e")
            .requiresAndFollows(oven)
        addEntry(exhaleOfTheHornedOne)

        val breathOfTheGoddess = EntryProviders.single(
            this,
            "breath_of_the_goddess",
            WitcheryItems.BREATH_OF_THE_GODDESS.get()
        ).generate("x")
            .requiresAndFollows(oven)
        addEntry(breathOfTheGoddess)

        val tearOfTheGoddess = EntryProviders.single(this, "tear_of_the_goddess", WitcheryItems.TEAR_OF_THE_GODDESS.get()).generate("t")
            .requiresAndFollows(breathOfTheGoddess)
            .alsoFollows(distillery)
        addEntry(tearOfTheGoddess)

        val expansion = FumeExtensionEntryProvider(this).generate("u")
            .requiresAndFollows(oven)
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