package dev.sterner.witchery.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import com.klikli_dev.modonomicon.api.datagen.book.condition.BookAdvancementConditionModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.datagen.book.entry.BrewEntryProvider
import dev.sterner.witchery.datagen.book.util.EntryProviders
import dev.sterner.witchery.datagen.book.util.advancement
import dev.sterner.witchery.datagen.book.util.requiresAndFollows
import dev.sterner.witchery.registry.WitcheryItems

class WitcheryTarotCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "tarot"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(
            "__________________________________",
            "__________________________________",
            "______________v_w_b_______________",
            "____________u_______c_____________",
            "___________t_________d____________",
            "__________s___________e___________",
            "_________r_____________f__________",
            "________________a_________________",
            "_________q_____________g__________",
            "__________p___________h___________",
            "___________o_________i____________",
            "____________n_______j_____________",
            "______________m_l_k_______________",
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

        val tarotReading = EntryProviders.singleItem(this, "tarot_reading", WitcheryItems.TAROT_DECK.get())
            .generate("a")
        addEntry(tarotReading)

        // Major Arcana
        val theFool = EntryProviders.tarot(this, "the_fool").generate("b")
            .requiresAndFollows(tarotReading)
        addEntry(theFool)

        val theMagician = EntryProviders.tarot(this, "the_magician").generate("c")
            .requiresAndFollows(tarotReading)
        addEntry(theMagician)

        val theHighPriestess = EntryProviders.tarot(this, "the_high_priestess").generate("d")
            .requiresAndFollows(tarotReading)
        addEntry(theHighPriestess)

        val theEmpress = EntryProviders.tarot(this, "the_empress").generate("e")
            .requiresAndFollows(tarotReading)
        addEntry(theEmpress)

        val theEmperor = EntryProviders.tarot(this, "the_emperor").generate("f")
            .requiresAndFollows(tarotReading)
        addEntry(theEmperor)

        val theHierophant = EntryProviders.tarot(this, "the_hierophant").generate("g")
            .requiresAndFollows(tarotReading)
        addEntry(theHierophant)

        val theLovers = EntryProviders.tarot(this, "the_lovers").generate("h")
            .requiresAndFollows(tarotReading)
        addEntry(theLovers)

        val theChariot = EntryProviders.tarot(this, "the_chariot").generate("i")
            .requiresAndFollows(tarotReading)
        addEntry(theChariot)

        val strength = EntryProviders.tarot(this, "strength").generate("j")
            .requiresAndFollows(tarotReading)
        addEntry(strength)

        val theHermit = EntryProviders.tarot(this, "the_hermit").generate("k")
            .requiresAndFollows(tarotReading)
        addEntry(theHermit)

        val wheelOfFortune = EntryProviders.tarot(this, "wheel_of_fortune").generate("l")
            .requiresAndFollows(tarotReading)
        addEntry(wheelOfFortune)

        val justice = EntryProviders.tarot(this, "justice").generate("m")
            .requiresAndFollows(tarotReading)
        addEntry(justice)

        val theHangedMan = EntryProviders.tarot(this, "the_hanged_man").generate("n")
            .requiresAndFollows(tarotReading)
        addEntry(theHangedMan)

        val death = EntryProviders.tarot(this, "death").generate("o")
            .requiresAndFollows(tarotReading)
        addEntry(death)

        val temperance = EntryProviders.tarot(this, "temperance").generate("p")
            .requiresAndFollows(tarotReading)
        addEntry(temperance)

        val theDevil = EntryProviders.tarot(this, "the_devil").generate("q")
            .requiresAndFollows(tarotReading)
        addEntry(theDevil)

        val theTower = EntryProviders.tarot(this, "the_tower").generate("r")
            .requiresAndFollows(tarotReading)
        addEntry(theTower)

        val theStar = EntryProviders.tarot(this, "the_star").generate("s")
            .requiresAndFollows(tarotReading)
        addEntry(theStar)

        val theMoon = EntryProviders.tarot(this, "the_moon").generate("t")
            .requiresAndFollows(tarotReading)
        addEntry(theMoon)

        val theSun = EntryProviders.tarot(this, "the_sun").generate("u")
            .requiresAndFollows(tarotReading)
        addEntry(theSun)

        val judgement = EntryProviders.tarot(this, "judgement").generate("v")
            .requiresAndFollows(tarotReading)
        addEntry(judgement)

        val theWorld = EntryProviders.tarot(this, "the_world").generate("w")
            .requiresAndFollows(tarotReading)
        addEntry(theWorld)
    }

    override fun categoryName(): String {
        return "tarot"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.TAROT_DECK.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}