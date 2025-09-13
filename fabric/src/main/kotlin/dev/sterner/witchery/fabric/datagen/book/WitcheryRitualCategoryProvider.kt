package dev.sterner.witchery.fabric.datagen.book

import com.klikli_dev.modonomicon.api.datagen.CategoryProvider
import com.klikli_dev.modonomicon.api.datagen.ModonomiconProviderBase
import com.klikli_dev.modonomicon.api.datagen.book.BookCategoryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookEntryModel
import com.klikli_dev.modonomicon.api.datagen.book.BookIconModel
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.datagen.book.util.EntryProviders
import dev.sterner.witchery.fabric.datagen.book.util.advancement
import dev.sterner.witchery.fabric.datagen.book.util.requiresAndFollows
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.world.item.Items


class WitcheryRitualCategoryProvider(
    parent: ModonomiconProviderBase?
) : CategoryProvider(parent) {

    override fun categoryId(): String {
        return "ritual"
    }

    override fun generateEntryMap(): Array<String> {
        return arrayOf(
            "__________________________________",
            "__________________________________",
            "________________et________________",
            "__________________________________",
            "__________________________________",
            "______________f__o_p______________",
            "__________b__k_x__________________",
            "___________u_l__r_g_______________",
            "__________y__a_q__________________",
            "______________n__i________________",
            "__________________________________",
            "_______________s___c______________",
            "________________mdw_______________",
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

        val infuseOtherwhere = EntryProviders.ritual(this, "infuse_otherwhere", WitcheryItems.SPIRIT_OF_OTHERWHERE.get())
            .generate("p")
            .requiresAndFollows(otherwhereChalk)
        addEntry(infuseOtherwhere)

        val manifestation = EntryProviders.ritual(this, "manifestation", Items.WIND_CHARGE)
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

        val midnight = EntryProviders.ritual(this, "set_midnight", Items.WOODEN_AXE)
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