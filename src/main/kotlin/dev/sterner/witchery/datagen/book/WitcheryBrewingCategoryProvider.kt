package dev.sterner.witchery.fabric.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.entry.*
import dev.sterner.witchery.fabric.datagen.book.util.EntryProviders
import dev.sterner.witchery.fabric.datagen.book.util.advancement
import dev.sterner.witchery.fabric.datagen.book.util.requiresAndFollows
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
            "______________p_m_n_______________",
            "__________________________________",
            "_____________h__j___g_____________",
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

        // Core entries
        val cauldron = EntryProviders.doubleItem(this, "cauldron",WitcheryItems.CAULDRON.get(),
            WitcheryItems.COPPER_CAULDRON.get(),
            noSecondTitle = true)
            .generate("c")
            .withCondition(advancement(Witchery.id("cauldron")))

        addEntry(cauldron)

        val introduction = EntryProviders.double(this, "beginning_potions", WitcheryItems.WITCHERY_POTION.get())
            .generate("j")
            .requiresAndFollows(cauldron)
        addEntry(introduction)

        // Potion mechanics entries
        val capacity = PotionCapacityEntryProvider(this).generate("m")
            .requiresAndFollows(introduction)
        addEntry(capacity)

        val effect = PotionEffectEntryProvider(this).generate("n")
            .requiresAndFollows(introduction)
        addEntry(effect)

        val effectType = PotionEffectTypeEntryProvider(this).generate("p")
            .requiresAndFollows(introduction)
        addEntry(effectType)

        // Infusions
        val redstoneSoup = InfusionEntryProvider(this, "redstone_soup", WitcheryItems.REDSTONE_SOUP.get())
            .generate("r")
            .requiresAndFollows(cauldron)
        addEntry(redstoneSoup)

        val ritualChalk = RitualChalkEntryProvider(this).generate("h")
            .requiresAndFollows(cauldron)
        addEntry(ritualChalk)

        val flyingOintment = InfusionEntryProvider(this, "flying_ointment", WitcheryItems.FLYING_OINTMENT.get())
            .generate("f")
            .requiresAndFollows(redstoneSoup)
        addEntry(flyingOintment)

        val spiritOfOtherwhere = InfusionEntryProvider(this, "spirit_of_otherwhere", WitcheryItems.SPIRIT_OF_OTHERWHERE.get())
            .generate("o")
            .requiresAndFollows(redstoneSoup)
        addEntry(spiritOfOtherwhere)

        val ghostOfLight = InfusionEntryProvider(this, "ghost_of_the_light", WitcheryItems.GHOST_OF_THE_LIGHT.get())
            .generate("g")
            .requiresAndFollows(redstoneSoup)
        addEntry(ghostOfLight)

        // Brews
        val raising = BrewEntryProvider(WitcheryItems.BREW_OF_RAISING.get(), "brew_of_raising", this)
            .generate("a")
            .requiresAndFollows(cauldron)
        addEntry(raising)

        val love = BrewEntryProvider(WitcheryItems.BREW_OF_LOVE.get(), "brew_of_love", this)
            .generate("b")
            .requiresAndFollows(cauldron)
        addEntry(love)

        val wasting = BrewEntryProvider(WitcheryItems.BREW_OF_WASTING.get(), "brew_of_wasting", this)
            .generate("d")
            .requiresAndFollows(cauldron)
        addEntry(wasting)

        val depths = BrewEntryProvider(WitcheryItems.BREW_OF_THE_DEPTHS.get(), "brew_of_the_depths", this)
            .generate("e")
            .requiresAndFollows(cauldron)
        addEntry(depths)

        val ink = BrewEntryProvider(WitcheryItems.BREW_OF_INK.get(), "brew_of_ink", this)
            .generate("i")
            .requiresAndFollows(cauldron)
        addEntry(ink)

        val frost = BrewEntryProvider(WitcheryItems.BREW_OF_FROST.get(), "brew_of_frost", this)
            .generate("y")
            .requiresAndFollows(cauldron)
        addEntry(frost)

        val revealing = BrewEntryProvider(WitcheryItems.BREW_OF_REVEALING.get(), "brew_of_revealing", this)
            .generate("u")
            .requiresAndFollows(cauldron)
        addEntry(revealing)

        val sleep = BrewEntryProvider(WitcheryItems.BREW_OF_SLEEPING.get(), "brew_of_sleeping", this)
            .generate("s")
            .requiresAndFollows(love)
        addEntry(sleep)

        val spirit = BrewEntryProvider(WitcheryItems.BREW_FLOWING_SPIRIT.get(), "brew_of_flowing_spirit", this)
            .generate("l")
            .requiresAndFollows(sleep, advancement(Witchery.id("spirit_world")))
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