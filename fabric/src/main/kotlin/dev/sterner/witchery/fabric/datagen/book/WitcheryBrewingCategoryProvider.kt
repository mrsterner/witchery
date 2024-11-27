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


class WitcheryBrewingCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "brewing"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "__________________________________",
            "________________h___g_____________",
            "__________________________________",
            "________l_s_b___c_r__o____________",
            "____________a_____________________",
            "_____________d______f_____________",
            "______________e___________________",
            "_______________yui________________",
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

        val cauldron = CauldronEntryProvider(this).generate("c")
        cauldron.withCondition(
            BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("cauldron"))
        )
        addEntry(cauldron)

        val redstoneSoup = RedstoneSoupEntryProvider(this).generate("r")
        redstoneSoup
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(cauldron.id)
                )

            )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))
        addEntry(redstoneSoup)

        val ritualChalk = RitualChalkEntryProvider(this).generate("h")
        ritualChalk.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(cauldron.id)
            )

        )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))

        addEntry(ritualChalk)

        val flyingOintment = FlyingOintmentEntryProvider(this).generate("f")
        flyingOintment
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(redstoneSoup.id)
                )

            )
            .addParent(BookEntryParentModel.create(redstoneSoup.id).withDrawArrow(true))
        addEntry(flyingOintment)

        val spiritOfOtherwhere = SpiritOfOtherwhereEntryProvider(this).generate("o")
        spiritOfOtherwhere
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(redstoneSoup.id)
                )

            )
            .addParent(BookEntryParentModel.create(redstoneSoup.id).withDrawArrow(true))
        addEntry(spiritOfOtherwhere)

        val ghostOfLight = GhostOfTheLightEntryProvider(this).generate("g")
        ghostOfLight
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(redstoneSoup.id)
                )

            )
            .addParent(BookEntryParentModel.create(redstoneSoup.id).withDrawArrow(true))
        addEntry(ghostOfLight)

        //BREWS
        val raising = BrewEntryProvider(WitcheryItems.BREW_OF_RAISING.get(), "brew_of_raising", this).generate("a")
        raising.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(cauldron.id)
            )

        )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))

        addEntry(raising)

        val love = BrewEntryProvider(WitcheryItems.BREW_OF_LOVE.get(), "brew_of_love", this).generate("b")
        love.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(cauldron.id)
            )

        )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))

        addEntry(love)

        val wasting = BrewEntryProvider(WitcheryItems.BREW_OF_WASTING.get(), "brew_of_wasting", this).generate("d")
        wasting.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(cauldron.id)
            )

        )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))

        addEntry(wasting)

        val depths = BrewEntryProvider(WitcheryItems.BREW_OF_THE_DEPTHS.get(), "brew_of_the_depths", this).generate("e")
        depths.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(cauldron.id)
            )

        )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))

        addEntry(depths)

        val ink = BrewEntryProvider(WitcheryItems.BREW_OF_INK.get(), "brew_of_ink", this).generate("i")
        ink.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(cauldron.id)
            )

        )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))

        addEntry(ink)

        val frost = BrewEntryProvider(WitcheryItems.BREW_OF_FROST.get(), "brew_of_frost", this).generate("y")
        frost.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(cauldron.id)
            )

        )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))

        addEntry(frost)


        val revealing = BrewEntryProvider(WitcheryItems.BREW_OF_REVEALING.get(), "brew_of_revealing", this).generate("u")
        revealing.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(cauldron.id)
            )

        )
            .addParent(BookEntryParentModel.create(cauldron.id).withDrawArrow(true))

        addEntry(revealing)

        val sleep = BrewEntryProvider(WitcheryItems.BREW_OF_SLEEPING.get(), "brew_of_sleeping", this).generate("s")
        sleep.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(love.id)
            )

        )
            .addParent(BookEntryParentModel.create(love.id).withDrawArrow(true))

        addEntry(sleep)

        val spirit = BrewEntryProvider(WitcheryItems.BREW_FLOWING_SPIRIT.get(), "brew_of_flowing_spirit", this).generate("l")
        spirit.withCondition(
            BookAndConditionModel.create().withChildren(
                BookEntryReadConditionModel.create()
                    .withEntry(sleep.id),
                BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("spirit_world"))
            )

        )
            .addParent(BookEntryParentModel.create(sleep.id).withDrawArrow(true))

        addEntry(spirit)
    }

    override fun categoryName(): String {
        return "brewing"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.CAULDRON.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}