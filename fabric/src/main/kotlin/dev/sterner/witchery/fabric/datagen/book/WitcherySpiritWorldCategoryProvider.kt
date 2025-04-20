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
import dev.sterner.witchery.fabric.datagen.book.entry.BrewEntryProvider
import dev.sterner.witchery.fabric.datagen.book.entry.SingleItemEntryProvider
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

        val disturbedCotton = SingleItemEntryProvider(this, "disturbed_cotton", WitcheryItems.DISTURBED_COTTON.get()).generate("a")
        disturbedCotton
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("spirit_world"))
        )

        addEntry(disturbedCotton)

        val hunger = SingleItemEntryProvider(this, "hunger", WitcheryItems.MELLIFLUOUS_HUNGER.get()).generate("b")
        hunger
            .withCondition(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("spirit_world"))
            )
        addEntry(hunger)

        val wispyCotton = SingleItemEntryProvider(this, "wispy_cotton", WitcheryItems.WISPY_COTTON.get()).generate("c")
        wispyCotton.withCondition(
            BookAndConditionModel.create().withChildren(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("disturbed")),
                BookEntryReadConditionModel.create()
                    .withEntry(disturbedCotton.id)
            )
        )
            .addParent(BookEntryParentModel.create(disturbedCotton.id).withDrawArrow(true))

        addEntry(wispyCotton)

        val dreamWeaverNightmare = SingleItemEntryProvider(this,"dream_weaver_of_nightmares", WitcheryItems.DREAM_WEAVER_OF_NIGHTMARES.get()).generate("n")
        dreamWeaverNightmare.withCondition(
            BookAndConditionModel.create().withChildren(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("disturbed")),
                BookEntryReadConditionModel.create()
                    .withEntry(disturbedCotton.id)
            )
        )
            .addParent(BookEntryParentModel.create(disturbedCotton.id).withDrawArrow(true))

        addEntry(dreamWeaverNightmare)

        val dreamWeaverFleeting = SingleItemEntryProvider(this, "dream_weaver_of_fleet_foot", WitcheryItems.DREAM_WEAVER_OF_FLEET_FOOT.get()).generate("f")
        dreamWeaverFleeting.withCondition(
            BookAndConditionModel.create().withChildren(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("disturbed")),
                BookEntryReadConditionModel.create()
                    .withEntry(wispyCotton.id)
            )
        )
            .addParent(BookEntryParentModel.create(wispyCotton.id).withDrawArrow(true))

        addEntry(dreamWeaverFleeting)

        val dreamWeaverIron = SingleItemEntryProvider(this,"dream_weaver_of_iron_arm", WitcheryItems.DREAM_WEAVER_OF_IRON_ARM.get()).generate("t")
        dreamWeaverIron.withCondition(
            BookAndConditionModel.create().withChildren(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("disturbed")),
                BookEntryReadConditionModel.create()
                    .withEntry(wispyCotton.id)
            )
        )
            .addParent(BookEntryParentModel.create(wispyCotton.id).withDrawArrow(true))

        addEntry(dreamWeaverIron)

        val dreamWeaverFating = SingleItemEntryProvider(this, "dream_weaver_of_fasting", WitcheryItems.DREAM_WEAVER_OF_FASTING.get()).generate("i")
        dreamWeaverFating.withCondition(
            BookAndConditionModel.create().withChildren(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("disturbed")),
                BookEntryReadConditionModel.create()
                    .withEntry(wispyCotton.id)
            )
        )
            .addParent(BookEntryParentModel.create(wispyCotton.id).withDrawArrow(true))

        addEntry(dreamWeaverFating)



        val spirit = BrewEntryProvider(WitcheryItems.BREW_FLOWING_SPIRIT.get(), "brew_of_flowing_spirit", this).generate("g")
        spirit.withCondition(
            BookAndConditionModel.create().withChildren(
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("spirit_world"))
            )
        )

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