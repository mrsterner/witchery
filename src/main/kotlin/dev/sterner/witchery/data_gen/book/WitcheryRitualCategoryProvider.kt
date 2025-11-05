package dev.sterner.witchery.data_gen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_gen.book.util.EntryProviders
import dev.sterner.witchery.data_gen.book.util.advancement
import dev.sterner.witchery.data_gen.book.util.requiresAndFollows
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.world.item.Items


class WitcheryRitualCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "ritual"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(
            "______________[___________________",
            "_______________€___@______________",
            "______________{_et=_______________",
            "__________________________________",
            "__________________________________",
            "______________f__o__p_____________",
            "__________b__k_x__________________",
            "________]__u_l__r_g_______________",
            "__________y__a_q________1234______",
            "______________n__i____j___________",
            "_____________h__________6785______",
            "_______________s___c______________",
            "_______________m___w______________",
            "_______________z___d______________",
            "________________&_£______________"

        )
    }

    override fun generateEntries() {
        var index = 0

        fun addEntry(entry: BookEntryModel) {
            this.add(entry.withSortNumber(index))
            index++
        }

        // Core ritual chalk entry
        val ritualChalk = EntryProviders.spotlightWithCauldronRecipes(
            this,
            "ritual_chalk",
            WitcheryItems.RITUAL_CHALK.get(),
            "golden_chalk",
            "infernal_chalk",
            "otherwhere_chalk"
        ).generate("r")
            .withCondition(advancement(Witchery.id("chalk")))
        addEntry(ritualChalk)

        // Chalk types
        val goldenChalk = EntryProviders.singleItem(this, "golden_chalk", WitcheryItems.GOLDEN_CHALK.get())
            .generate("g")
            .requiresAndFollows(ritualChalk)
        addEntry(goldenChalk)

        val otherwhereChalk = EntryProviders.singleItem(this, "otherwhere_chalk", WitcheryItems.OTHERWHERE_CHALK.get())
            .generate("o")
            .requiresAndFollows(ritualChalk)
        addEntry(otherwhereChalk)

        val infernalChalk = EntryProviders.singleItem(this, "infernal_chalk", WitcheryItems.INFERNAL_CHALK.get())
            .generate("i")
            .requiresAndFollows(ritualChalk)
        addEntry(infernalChalk)


        //CURSES
        val curses = EntryProviders.single(this, "curses", WitcheryItems.BREW_OF_THE_GROTESQUE.get())
            .generate("j")
            .requiresAndFollows(infernalChalk)
        add(curses)

        val curseOfBefuddlement = EntryProviders.ritual(this, "befuddlement", Items.POISONOUS_POTATO)
            .generate("1")
            .requiresAndFollows(curses)
        add(curseOfBefuddlement)

        val curseOfCorrupt = EntryProviders.ritual(this, "curse_of_corrupt_poppet", WitcheryItems.VOODOO_PROTECTION_POPPET.get())
            .generate("2")
            .requiresAndFollows(curses)
        add(curseOfCorrupt)

        val curseOfFragile = EntryProviders.ritual(this, "fragility", Items.BONE)
            .generate("3")
            .requiresAndFollows(curses)
        add(curseOfFragile)

        val curseOfHunger = EntryProviders.ritual(this, "hunger", Items.DRIED_KELP)
            .generate("4")
            .requiresAndFollows(curses)
        add(curseOfHunger)

        val curseOfInsanity = EntryProviders.ritual(this, "curse_of_insanity", Items.SUGAR)
            .generate("5")
            .requiresAndFollows(curses)
        add(curseOfInsanity)

        val curseOfMisfortune = EntryProviders.ritual(this, "curse_of_misfortune", Items.FERMENTED_SPIDER_EYE)
            .generate("6")
            .requiresAndFollows(curses)
        add(curseOfMisfortune)

        val curseOfOverheating = EntryProviders.ritual(this, "curse_of_overheating", Items.BLAZE_POWDER)
            .generate("7")
            .requiresAndFollows(curses)
        add(curseOfOverheating)

        val curseOfSinking = EntryProviders.ritual(this, "curse_of_sinking", Items.INK_SAC)
            .generate("8")
            .requiresAndFollows(curses)
        add(curseOfSinking)


        // Infernal chalk rituals
        val summonDemon = EntryProviders.ritual(this, "summon_demon", WitcheryItems.DEMON_HEART.get())
            .generate("d")
            .requiresAndFollows(infernalChalk)
        addEntry(summonDemon)

        val summonImp = EntryProviders.ritual(this, "summon_imp", Items.NETHERRACK)
            .generate("m")
            .requiresAndFollows(infernalChalk)
        addEntry(summonImp)

        val summonWither = EntryProviders.ritual(this, "summon_wither", Items.WITHER_SKELETON_SKULL)
            .generate("w")
            .requiresAndFollows(infernalChalk)
        addEntry(summonWither)

        val summonWitch = EntryProviders.ritual(this, "summon_witch", WitcheryItems.WITCHES_HAND.get())
            .generate("c")
            .requiresAndFollows(infernalChalk)
        addEntry(summonWitch)

        val summonSpectral = EntryProviders.ritual(this, "summon_spectral_pig", WitcheryItems.SPECTRAL_DUST.get())
            .generate("s")
            .requiresAndFollows(infernalChalk)
        addEntry(summonSpectral)

        // Otherwhere chalk rituals
        val teleportOtW = EntryProviders.ritual(this, "teleport_owner_to_waystone", WitcheryItems.WAYSTONE.get())
            .generate("t")
            .requiresAndFollows(otherwhereChalk)
        addEntry(teleportOtW)

        val binding = EntryProviders.ritual(this, "binding", Items.CHAIN)
            .generate("=")
            .requiresAndFollows(otherwhereChalk)
        addEntry(binding)

        val soulbind = EntryProviders.ritual(this, "soulbind", Items.GOLDEN_APPLE)
            .generate("z")
            .requiresAndFollows(infernalChalk)
        addEntry(soulbind)

        val soulSeverance = EntryProviders.ritual(this, "soul_severance", Items.SOUL_LANTERN)
            .generate("&")
            .requiresAndFollows(infernalChalk)
        addEntry(soulSeverance)



        val bestialCall = EntryProviders.ritual(this, "bestial_call", Items.CARROT)
            .generate("@")
            .requiresAndFollows(otherwhereChalk)
        addEntry(bestialCall)
        val rainingToads = EntryProviders.ritual(this, "raining_toad", WitcheryItems.TOE_OF_FROG.get())
            .generate("£")
            .requiresAndFollows(infernalChalk)
        addEntry(rainingToads)
        val blockBelowIron = EntryProviders.ritual(this, "blocks_below_iron", Items.IRON_ORE)
            .generate("€")
            .requiresAndFollows(otherwhereChalk)
        addEntry(blockBelowIron)
        val blockBelowCopper = EntryProviders.ritual(this, "blocks_below_copper", Items.COPPER_ORE)
            .generate("{")
            .requiresAndFollows(otherwhereChalk)
        addEntry(blockBelowCopper)
        val blockBelowGold = EntryProviders.ritual(this, "blocks_below_gold", Items.GOLD_ORE)
            .generate("[")
            .requiresAndFollows(otherwhereChalk)
        addEntry(blockBelowGold)



        val infuseOtherwhere =
            EntryProviders.ritual(this, "infuse_otherwhere", WitcheryItems.SPIRIT_OF_OTHERWHERE.get())
                .generate("p")
                .requiresAndFollows(otherwhereChalk)
        addEntry(infuseOtherwhere)

        val manifestation = EntryProviders.ritual(this, "manifestation", WitcheryItems.MELLIFLUOUS_HUNGER.get())
            .generate("e")
            .requiresAndFollows(otherwhereChalk)
        addEntry(manifestation)

        // Basic ritual chalk rituals
        val infuseLight = EntryProviders.ritual(this, "infuse_light", WitcheryItems.GHOST_OF_THE_LIGHT.get())
            .generate("x")
            .requiresAndFollows(ritualChalk)
        addEntry(infuseLight)

        val applyOintment = EntryProviders.ritual(this, "apply_ointment", WitcheryItems.FLYING_OINTMENT.get())
            .generate("q")
            .requiresAndFollows(ritualChalk)
        addEntry(applyOintment)

        val midnight = EntryProviders.ritual(this, "set_midnight", Items.CLOCK)
            .generate("k")
            .requiresAndFollows(ritualChalk)
        addEntry(midnight)

        val attuned = EntryProviders.ritual(this, "charge_attuned", WitcheryItems.ATTUNED_STONE.get())
            .generate("a")
            .requiresAndFollows(ritualChalk)
        addEntry(attuned)

        val necro = EntryProviders.ritual(this, "necro_stone", WitcheryItems.NECROMANTIC_STONE.get())
            .generate("n")
            .requiresAndFollows(ritualChalk)
        addEntry(necro)

        val infuseNecro = EntryProviders.ritual(this, "infuse_necromancy", WitcheryItems.NECROMANTIC_SOULBIND.get())
            .generate("h")
            .requiresAndFollows(necro)
        addEntry(infuseNecro)
        val chargeInfusion = EntryProviders.ritual(this, "rite_of_charging_infusion", Items.FIRE_CHARGE)
            .generate("u")
            .requiresAndFollows(ritualChalk)
        addEntry(chargeInfusion)

        val bindFamiliar = EntryProviders.ritual(this, "bind_familiar", WitcheryItems.TEAR_OF_THE_GODDESS.get())
            .generate("b")
            .requiresAndFollows(ritualChalk)
        addEntry(bindFamiliar)

        val bindSpectralCreature = EntryProviders.ritual(this, "bind_spectral_creatures", WitcheryItems.ARTHANA.get())
            .generate("y")
            .requiresAndFollows(ritualChalk)
        addEntry(bindSpectralCreature)

        val pushMobs = EntryProviders.ritual(this, "push_mobs", Items.FEATHER)
            .generate("f")
            .requiresAndFollows(ritualChalk)
        addEntry(pushMobs)

        val stone = EntryProviders.ritual(this, "stone_ritual", Items.STONE)
            .generate("]")
            .requiresAndFollows(ritualChalk)
        addEntry(stone)
    }

    override fun categoryName(): String {
        return "ritual"
    }

    override fun categoryIcon(): BookIconModel {
        return BookIconModel.create(WitcheryItems.RITUAL_CHALK.get())
    }

    override fun additionalSetup(category: BookCategoryModel?): BookCategoryModel {
        return super.additionalSetup(category)
            .withBackground(Witchery.id("textures/gui/modonomicon/parallax.png"))
    }
}