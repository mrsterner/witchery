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

        val ritualChalk = RitualChalkEntryProvider(this).generate("r")
        ritualChalk.withCondition(
            BookAdvancementConditionModel.create().withAdvancementId(Witchery.id("chalk"))
        )

        addEntry(ritualChalk)

        val goldenChalk = GoldenChalkEntryProvider(this).generate("g")
        goldenChalk
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(goldenChalk)


        val otherwhereChalk = OtherwhereChalkEntryProvider(this).generate("o")
        otherwhereChalk
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(otherwhereChalk)

        val infernalChalk = InfernalChalkEntryProvider(this).generate("i")
        infernalChalk
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(infernalChalk)



        val summonDemon = RitualEntryProvider("summon_demon", WitcheryItems.DEMON_HEART.get(),this).generate("d")
        summonDemon
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(infernalChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(infernalChalk.id).withDrawArrow(true))
        addEntry(summonDemon)

        val summonImp = RitualEntryProvider("summon_imp", Items.NETHERRACK,this).generate("m")
        summonImp
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(infernalChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(infernalChalk.id).withDrawArrow(true))
        addEntry(summonImp)

        val summonWither = RitualEntryProvider("summon_wither", Items.WITHER_SKELETON_SKULL,this).generate("w")
        summonWither
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(infernalChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(infernalChalk.id).withDrawArrow(true))
        addEntry(summonWither)

        val summonWitch = RitualEntryProvider("summon_witch", WitcheryItems.WITCHES_HAND.get(),this).generate("c")
        summonWitch
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(infernalChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(infernalChalk.id).withDrawArrow(true))
        addEntry(summonWitch)

        val summonSpectral = RitualEntryProvider("summon_spectral_pig", WitcheryItems.SPECTRAL_DUST.get(),this).generate("s")
        summonSpectral
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(infernalChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(infernalChalk.id).withDrawArrow(true))
        addEntry(summonSpectral)



        val teleportOtW = RitualEntryProvider("teleport_owner_to_waystone", WitcheryItems.WAYSTONE.get(),this).generate("t")
        teleportOtW
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(otherwhereChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(otherwhereChalk.id).withDrawArrow(true))
        addEntry(teleportOtW)

        val infuseOtherwhere = RitualEntryProvider("infuse_otherwhere", WitcheryItems.SPIRIT_OF_OTHERWHERE.get(),this).generate("p")
        infuseOtherwhere
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(otherwhereChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(otherwhereChalk.id).withDrawArrow(true))
        addEntry(infuseOtherwhere)

        val infuseLight = RitualEntryProvider("infuse_light", WitcheryItems.GHOST_OF_THE_LIGHT.get(),this).generate("x")
        infuseLight
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(infuseLight)

        val applyOintment = RitualEntryProvider("apply_ointment", WitcheryItems.FLYING_OINTMENT.get(),this).generate("q")
        applyOintment
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(applyOintment)

        val midnight = RitualEntryProvider("set_midnight", Items.WOODEN_AXE,this).generate("k")
        midnight
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(midnight)

        val attuned = RitualEntryProvider("charge_attuned", WitcheryItems.ATTUNED_STONE.get(),this).generate("a")
        attuned
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(attuned)

        val necro = RitualEntryProvider("necro_stone", WitcheryItems.NECROMANTIC_STONE.get(),this).generate("n")
        necro
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(necro)

        val chargeInfusion = RitualEntryProvider("rite_of_charging_infusion", Items.FIRE_CHARGE,this).generate("u")
        chargeInfusion
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(chargeInfusion)

        val manifestation = RitualEntryProvider("manifestation", Items.WIND_CHARGE,this).generate("e")
        manifestation
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(otherwhereChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(otherwhereChalk.id).withDrawArrow(true))
        addEntry(manifestation)

        val bindFamiliar = RitualEntryProvider("bind_familiar", WitcheryItems.TEAR_OF_THE_GODDESS.get(),this).generate("b")
        bindFamiliar
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(bindFamiliar)

        val bindSpectralCreature = RitualEntryProvider("bind_spectral_creatures", WitcheryItems.ARTHANA.get(),this).generate("y")
        bindSpectralCreature
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
        addEntry(bindSpectralCreature)

        val pushMobs = RitualEntryProvider("push_mobs", Items.FEATHER,this).generate("f")
        pushMobs
            .withCondition(
                BookAndConditionModel.create().withChildren(
                    BookEntryReadConditionModel.create()
                        .withEntry(ritualChalk.id)
                )

            )
            .addParent(BookEntryParentModel.create(ritualChalk.id).withDrawArrow(true))
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