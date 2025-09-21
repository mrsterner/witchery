package dev.sterner.witchery.fabric.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.entry.BrewEntryProvider
import dev.sterner.witchery.fabric.datagen.book.util.EntryProviders
import dev.sterner.witchery.fabric.datagen.book.util.advancement
import dev.sterner.witchery.fabric.datagen.book.util.requiresAndFollows
import dev.sterner.witchery.registry.WitcheryItems


class WitcherySpiritWorldCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "spirit_world"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "_____________t____________________",
            "___________f_c_i__________________",
            "__________________________________",
            "___________n_a_b_g________________",
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

        val disturbedCotton =
            EntryProviders.singleItem(this, "disturbed_cotton", WitcheryItems.DISTURBED_COTTON.get()).generate("a")
                .withCondition(advancement(Witchery.id("spirit_world")))
        addEntry(disturbedCotton)

        val hunger = EntryProviders.singleItem(this, "hunger", WitcheryItems.MELLIFLUOUS_HUNGER.get()).generate("b")
            .withCondition(advancement(Witchery.id("spirit_world")))
        addEntry(hunger)

        val wispyCotton =
            EntryProviders.singleItem(this, "wispy_cotton", WitcheryItems.WISPY_COTTON.get()).generate("c")
                .requiresAndFollows(disturbedCotton, advancement(Witchery.id("disturbed")))

        addEntry(wispyCotton)

        val dreamWeaverNightmare = EntryProviders.singleItem(
            this,
            "dream_weaver_of_nightmares",
            WitcheryItems.DREAM_WEAVER_OF_NIGHTMARES.get()
        ).generate("n")
            .requiresAndFollows(disturbedCotton, advancement(Witchery.id("disturbed")))

        addEntry(dreamWeaverNightmare)

        val dreamWeaverFleeting = EntryProviders.singleItem(
            this,
            "dream_weaver_of_fleet_foot",
            WitcheryItems.DREAM_WEAVER_OF_FLEET_FOOT.get()
        ).generate("f")
            .requiresAndFollows(wispyCotton, advancement(Witchery.id("disturbed")))

        addEntry(dreamWeaverFleeting)

        val dreamWeaverIron = EntryProviders.singleItem(
            this,
            "dream_weaver_of_iron_arm",
            WitcheryItems.DREAM_WEAVER_OF_IRON_ARM.get()
        ).generate("t")
            .requiresAndFollows(wispyCotton, advancement(Witchery.id("disturbed")))
        addEntry(dreamWeaverIron)

        val dreamWeaverFating = EntryProviders.singleItem(
            this,
            "dream_weaver_of_fasting",
            WitcheryItems.DREAM_WEAVER_OF_FASTING.get()
        ).generate("i")
            .requiresAndFollows(wispyCotton, advancement(Witchery.id("disturbed")))

        addEntry(dreamWeaverFating)


        val spirit =
            BrewEntryProvider(WitcheryItems.BREW_FLOWING_SPIRIT.get(), "brew_of_flowing_spirit", this).generate("g")
                .withCondition(advancement(Witchery.id("spirit_world")))

        addEntry(spirit)
    }

    override fun categoryName(): String {
        return "spirit_world"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.WISPY_COTTON.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withCondition(BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("spirit_world")))
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}