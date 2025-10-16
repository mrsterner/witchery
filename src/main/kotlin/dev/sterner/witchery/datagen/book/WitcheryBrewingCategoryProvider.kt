package dev.sterner.witchery.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.datagen.book.entry.*
import dev.sterner.witchery.datagen.book.util.EntryProviders
import dev.sterner.witchery.datagen.book.util.advancement
import dev.sterner.witchery.datagen.book.util.requiresAndFollows
import dev.sterner.witchery.registry.WitcheryItems

class WitcheryBrewingCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "brewing"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(

            "____________________________________",
            "____________________________________",
            "____________________________________",
            "________ui__________________________",
            "_______y____________________________",
            "______1_______________p_____________",
            "______e__t__h___c___j__n____________",
            "______v_______________m_____________",
            "_______d____________________________",
            "________a2b_____w_x_________________",
            "________________r___________________",
            "________l_s___g___f_________________",
            "________________o___________________",
            "____________________________________",
            "____________________________________",
            "____________________________________",
            "____________________________________"
        )
    }

    override fun generateEntries() {
        var index = 0

        fun addEntry(entry: BookEntryModel) {
            this.add(entry.withSortNumber(index))
            index++
        }

        // Core cauldron
        val cauldron = EntryProviders.doubleItem(
            this, "cauldron", WitcheryItems.CAULDRON.get(),
            WitcheryItems.COPPER_CAULDRON.get(),
            noSecondTitle = true
        )
            .generate("c")
            .withCondition(advancement(Witchery.id("cauldron")))
        addEntry(cauldron)

        // Introduction
        val introduction = EntryProviders.double(this, "beginning_potions", WitcheryItems.WITCHERY_POTION.get())
            .generate("j")
            .requiresAndFollows(cauldron)
        addEntry(introduction)

        val capacity = PotionCapacityEntryProvider(this).generate("m")
            .requiresAndFollows(introduction)
        addEntry(capacity)

        val effect = PotionEffectEntryProvider(this).generate("n")
            .requiresAndFollows(introduction)
        addEntry(effect)

        val effectType = PotionEffectTypeEntryProvider(this).generate("p")
            .requiresAndFollows(introduction)
        addEntry(effectType)

        // === INFUSIONS SECTION ===
        val infusions = EntryProviders.single(this, "infusions", WitcheryItems.REDSTONE_SOUP.get())
            .generate("w")
            .requiresAndFollows(cauldron)
        addEntry(infusions)

        val ritualChalk = RitualChalkEntryProvider(this).generate("h")
            .requiresAndFollows(cauldron)
        addEntry(ritualChalk)

        val redstoneSoup = InfusionEntryProvider(this, "redstone_soup", WitcheryItems.REDSTONE_SOUP.get())
            .generate("r")
            .requiresAndFollows(infusions)
        addEntry(redstoneSoup)

        val flyingOintment = InfusionEntryProvider(this, "flying_ointment", WitcheryItems.FLYING_OINTMENT.get())
            .generate("f")
            .requiresAndFollows(redstoneSoup)
        addEntry(flyingOintment)

        val spiritOfOtherwhere =
            InfusionEntryProvider(this, "spirit_of_otherwhere", WitcheryItems.SPIRIT_OF_OTHERWHERE.get())
                .generate("o")
                .requiresAndFollows(redstoneSoup)
        addEntry(spiritOfOtherwhere)

        val ghostOfLight = InfusionEntryProvider(this, "ghost_of_the_light", WitcheryItems.GHOST_OF_THE_LIGHT.get())
            .generate("g")
            .requiresAndFollows(redstoneSoup)
        addEntry(ghostOfLight)

        val necroSoulbind = InfusionEntryProvider(this, "necromantic_soulbind", WitcheryItems.NECROMANTIC_SOULBIND.get())
            .generate("x")
            .requiresAndFollows(redstoneSoup)
        addEntry(necroSoulbind)

        // === BREWS SECTION ===
        val brewsIntro = EntryProviders.single(this, "brews", WitcheryItems.BREW_OF_THE_GROTESQUE.get())
            .generate("t")
            .requiresAndFollows(introduction)
        addEntry(brewsIntro)

        val grotesque = BrewEntryProvider(WitcheryItems.BREW_OF_THE_GROTESQUE.get(), "brew_of_the_grotesque", this)
            .generate("1")
            .requiresAndFollows(brewsIntro)
        addEntry(grotesque)

        // Basic Brews Branch
        val love = BrewEntryProvider(WitcheryItems.BREW_OF_LOVE.get(), "brew_of_love", this)
            .generate("b")
            .requiresAndFollows(brewsIntro)
        addEntry(love)

        val sleep = BrewEntryProvider(WitcheryItems.BREW_OF_SLEEPING.get(), "brew_of_sleeping", this)
            .generate("s")
            .requiresAndFollows(love)
        addEntry(sleep)

        val spirit = BrewEntryProvider(WitcheryItems.BREW_FLOWING_SPIRIT.get(), "brew_of_flowing_spirit", this)
            .generate("l")
            .requiresAndFollows(sleep, advancement(Witchery.id("spirit_world")))
        addEntry(spirit)

        val raising = BrewEntryProvider(WitcheryItems.BREW_OF_RAISING.get(), "brew_of_raising", this)
            .generate("a")
            .requiresAndFollows(brewsIntro)
        addEntry(raising)

        val wasting = BrewEntryProvider(WitcheryItems.BREW_OF_WASTING.get(), "brew_of_wasting", this)
            .generate("d")
            .requiresAndFollows(brewsIntro)
        addEntry(wasting)

        val soul = BrewEntryProvider(WitcheryItems.BREW_OF_SOUL_SEVERANCE.get(), "brew_of_soul_severance", this)
            .generate("2")
            .requiresAndFollows(brewsIntro)
        addEntry(soul)

        val depths = BrewEntryProvider(WitcheryItems.BREW_OF_THE_DEPTHS.get(), "brew_of_the_depths", this)
            .generate("e")
            .requiresAndFollows(brewsIntro)
        addEntry(depths)

        val ink = BrewEntryProvider(WitcheryItems.BREW_OF_INK.get(), "brew_of_ink", this)
            .generate("i")
            .requiresAndFollows(brewsIntro)
        addEntry(ink)

        val frost = BrewEntryProvider(WitcheryItems.BREW_OF_FROST.get(), "brew_of_frost", this)
            .generate("y")
            .requiresAndFollows(brewsIntro)
        addEntry(frost)

        val revealing = BrewEntryProvider(WitcheryItems.BREW_OF_REVEALING.get(), "brew_of_revealing", this)
            .generate("u")
            .requiresAndFollows(brewsIntro)
        addEntry(revealing)

        val erosion = BrewEntryProvider(WitcheryItems.BREW_OF_EROSION.get(), "brew_of_erosion", this)
            .generate("v")
            .requiresAndFollows(brewsIntro)
        addEntry(erosion)
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