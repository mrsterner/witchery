package dev.sterner.witchery.fabric.datagen.lang

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider

object WitcheryRitualLangProvider {

    fun ritual(builder: FabricLanguageProvider.TranslationBuilder) {
        builder.add("witchery:ritual/rot", "Rite of Rot")
        builder.add("witchery:ritual/rot.tooltip", "Turns life to death")
        builder.add("witchery:ritual/rite_of_charging_infusion", "Rite of Charging")
        builder.add("witchery:ritual/rite_of_charging_infusion.tooltip", "Charges the power of an Infused player")
        builder.add("witchery:ritual/infuse_light", "Infusion of Light")
        builder.add("witchery:ritual/infuse_light.tooltip", "Infuses the player with the Ghost of the Light")
        builder.add("witchery:ritual/infuse_otherwhere", "Infusion of Otherwhere")
        builder.add("witchery:ritual/infuse_otherwhere.tooltip", "Infuses the player with the Spirit of Otherwhere")

        builder.add("witchery:ritual/charge_attuned", "Rite of Charging")
        builder.add("witchery:ritual/charge_attuned.tooltip", "Charges an Attuned Stone with 2000 Altar Power")
        builder.add("witchery:ritual/summon_lightning_on_waystone", "Lightning Strike")
        builder.add(
            "witchery:ritual/summon_lightning_on_waystone.tooltip",
            "Summons a Lightning Strike at bound Waystone location"
        )
        builder.add("witchery:ritual/remove_curse", "Rite of Remove Curse")
        builder.add("witchery:ritual/remove_curse.tooltip", "Removes the oldest curse")
        builder.add("witchery:ritual/curse_of_corrupt_poppet", "Curse of Corrupt Poppet")
        builder.add("witchery:ritual/curse_of_corrupt_poppet.tooltip", "Curses the poppets of the player")
        builder.add("witchery:ritual/curse_of_the_wolf", "Curse of the Wolf")
        builder.add("witchery:ritual/curse_of_the_wolf.tooltip", "Curse the player with Lycantropy")
        builder.add("witchery:ritual/curse_of_overheating", "Curse of Overheating")
        builder.add("witchery:ritual/curse_of_overheating.tooltip", "Too heat sensitive to be in the desert")
        builder.add("witchery:ritual/curse_of_sinking", "Curse of Sinking")
        builder.add("witchery:ritual/curse_of_sinking.tooltip", "Harder to swim")
        builder.add("witchery:ritual/curse_of_misfortune", "Curse of Misfortune")
        builder.add("witchery:ritual/curse_of_misfortune.tooltip", "Random negative status effects")
        builder.add("witchery:ritual/curse_of_insanity", "Curse of Insanity")
        builder.add("witchery:ritual/curse_of_insanity.tooltip", "Those mobs... are they real?")
        builder.add("witchery:ritual/infuse_seer", "Infuse Seer")
        builder.add("witchery:ritual/infuse_seer.tooltip", "Used to summon coven")
        builder.add("witchery:ritual/infuse_necromancy", "Infuse Necromancy")
        builder.add("witchery:ritual/infuse_necromancy.tooltip", "Infuse the player with Necromantic Soulbind")
        builder.add("witchery:ritual/summon_lightning", "Lightning Strike")
        builder.add("witchery:ritual/summon_lightning.tooltip", "Summons a Lightning Strike at ritual center")
        builder.add("witchery:ritual/set_midnight", "Turn Night")
        builder.add("witchery:ritual/set_midnight.tooltip", "Sets the time to midnight")
        builder.add("witchery:ritual/push_mobs", "Rite of Sanctity")
        builder.add("witchery:ritual/push_mobs.tooltip", "Pushes hostile mobs away from ritual center")
        builder.add("witchery:ritual/teleport_owner_to_waystone", "Teleportation")
        builder.add(
            "witchery:ritual/teleport_owner_to_waystone.tooltip",
            "Teleports the user to bound Waystone location"
        )
        builder.add("witchery:ritual/teleport_taglock_to_waystone", "Teleportation")
        builder.add(
            "witchery:ritual/teleport_taglock_to_waystone.tooltip",
            "Teleports the bound Taglock entity to bound Waystone location"
        )
        builder.add("witchery:ritual/summon_spectral_pig", "Summon Spectral Pig")
        builder.add("witchery:ritual/summon_spectral_pig.tooltip", "Summon an Spectral Pig at ritual center")
        builder.add("witchery:ritual/summon_imp", "Summon Imp")
        builder.add("witchery:ritual/summon_imp.tooltip", "Summon an Imp at ritual center")
        builder.add("witchery:ritual/apply_ointment", "Imbue Flying Ointment")
        builder.add(
            "witchery:ritual/apply_ointment.tooltip",
            "When imbued into broom-wood, the charm awakens. The air no longer resists, and the winds know thy name"
        )


        builder.add("witchery:ritual/summon_witch", "Summon Witch")
        builder.add("witchery:ritual/summon_witch.tooltip", "Summon a Witch at ritual center")
        builder.add("witchery:ritual/summon_demon", "Summon Demon")
        builder.add("witchery:ritual/summon_demon.tooltip", "Summon a Demon at ritual center")
        builder.add("witchery:ritual/summon_wither", "Summon Wither")
        builder.add("witchery:ritual/summon_wither.tooltip", "Summon a Wither at ritual center")


        builder.add("witchery:ritual/necro_stone", "Necromantic Stone")
        builder.add(
            "witchery:ritual/necro_stone.tooltip",
            "A stone as cold as the grave and thrice as silent. It is not carved, but called—formed beneath moon-lit skies where the veil is thin. Death clings to it like moss to tombstone."
        )

        builder.add("witchery:ritual/bind_spectral_creatures", "Bind Spectral Creatures")
        builder.add(
            "witchery:ritual/bind_spectral_creatures.tooltip",
            "Binds spectral being to Effigies. Multiple binds might yield unusual effects."
        )
        builder.add("witchery:ritual/bind_familiar", "Bind Familiar")
        builder.add(
            "witchery:ritual/bind_familiar.tooltip",
            "Binds a familiar to the Player. A Familiar might aid in certain prospects of a witches life."
        )
        builder.add("witchery:ritual/manifestation", "Rite of Manifestation")
        builder.add("witchery:ritual/manifestation.tooltip", "Allows you to pass through spirit portals")
        builder.add("witchery:ritual/resurrect_familiar", "Resurrect Familiar")
        builder.add("witchery:ritual/resurrect_familiar.tooltip", "A bound familiar will be revived")

    }
}