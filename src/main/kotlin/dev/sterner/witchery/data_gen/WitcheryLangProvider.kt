package dev.sterner.witchery.data_gen

import dev.sterner.witchery.core.api.SpecialPotion
import dev.sterner.witchery.data_gen.lang.WitcheryAdvancementLangProvider
import dev.sterner.witchery.data_gen.lang.WitcheryBookLangProvider
import dev.sterner.witchery.data_gen.lang.WitcheryRitualLangProvider
import dev.sterner.witchery.core.registry.*
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.neoforged.neoforge.common.data.LanguageProvider

class WitcheryLangProvider(output: PackOutput, modid: String, locale: String) :
    LanguageProvider(output, modid, locale) {

    private fun formatId(id: ResourceLocation): String {
        val name = id.path.split('.').last()
        return formatId(name)
    }

    private fun formatId(name: String): String {
        val exceptions = setOf("of", "the", "and", "in", "for", "on", "to")

        return name
            .removeSuffix("_component")
            .split('_')
            .joinToString(" ") { word ->
                if (word in exceptions) {
                    word.lowercase()
                } else {
                    word.replaceFirstChar { it.uppercase() }
                }
            }
    }

    override fun addTranslations() {
        add("itemGroup.witchery.main", "Witchery")

        WitcheryRitualLangProvider.ritual(::add)
        WitcheryAdvancementLangProvider.advancement(::add)
        WitcheryBookLangProvider.book(::add)

        for (item in WitcheryItems.LANG_HELPER) {
            if (item == "censer_long") {
                add("item.witchery.censer_long", "Censer")
            } else {
                add("item.witchery.$item", formatId(item))
            }
        }

        for (block in WitcheryBlocks.LANG_HELPER) {
            add("block.witchery.$block", formatId(block))
        }

        for (entity in WitcheryEntityTypes.LANG_HELPER) {
            add("entity.witchery.$entity", formatId(entity))
        }

        for (entry in WitcherySpecialPotionEffects.SPECIAL_REGISTRY.entrySet()) {
            val key: ResourceKey<SpecialPotion>? = entry.key
            val id = key?.location()
            id?.let { add("witchery:${it.path}", formatId(id)) }
        }

        add("minecraft.villager.witchery.fortune_teller", "Fortune Teller")
        add("minecraft:the_end", "The End")
        add("item.witchery.leonards_urn.potions", "%s/%s Potions")
        add("item.witchery.quartz_sphere.loaded", "Loaded:")
        add("item.witchery.quartz_sphere.empty", "Empty - Use with Leonard's Urn")
        add("gui.witchery.select_potion", "Select Potion")
        add("gui.witchery.scroll_to_select", "Scroll to Select")

        add("item.witchery.tarot_deck.desc", "Fortune of three cards last 3 days")

        add("item.witchery.lifeblood_berry.tooltip", "Fills the soul with ethereal vitality")
        add("item.witchery.lifeblood_berry.tooltip2", "+5 Lifeblood")

        add("witchery.ability.death_teleport.already_used", "Already used this life")

        add("witchery.coven.needs_demon_heart", "This witch needs a Demon Heart to join your coven")
        add("witchery.coven.witch_limit", "Your coven has reached the maximum number of witches")
        add("witchery.coven.witch_added", "Witch has been bound to your coven")
        add("witchery.coven.witch_already_bound", "This witch is already bound to a coven")
        add("witchery.coven.player_limit", "Your coven has reached the maximum number of players")
        add("witchery.coven.already_member", "This player is already a member of your coven")
        add("witchery.coven.added_player", "%s has been added to your coven")
        add("witchery.coven.joined", "You have joined %s's coven")
        add("witchery.coven.not_member", "This player is not a member of your coven")
        add("witchery.coven.contract_signed", "You have signed the Coven Contract")
        add("witchery.coven.bound_members", "Successfully bound %s members to your coven")
        add("witchery.coven.summoned", "Summoned %s witches to the ritual circle")
        add("witchery.coven.no_witches", "You have no witches in your coven to summon")
        add("witchery.coven.no_ritual", "No ritual circle found nearby")
        add("witchery.coven.interrupted", "Ritual interrupted")
        add("witchery.coven.witch_died", "One of your coven witches has died")
        add("witchery.coven.disbanded", "You have been removed from the coven")
        add("witchery.coven.contract_destroyed", "The coven contract has been destroyed! Your coven has been disbanded.")


        add(WitcheryTags.ROWAN_LOG_ITEMS, "Rowan Logs")
        add(WitcheryTags.ALDER_LOG_ITEMS, "Alder Logs")
        add(WitcheryTags.HAWTHORN_LOG_ITEMS, "Hawthorn Logs")
        add(WitcheryTags.LEAF_ITEMS, "Witchery Leaves")
        add(WitcheryTags.CANDELABRA_ITEMS, "Candelabras")
        add(WitcheryTags.PLACEABLE_POPPETS, "Placeable Poppets")
        add(WitcheryTags.FROM_SPIRIT_WORLD_TRANSFERABLE, "From Spirit World Transferable")
        add(WitcheryTags.TO_SPIRIT_WORLD_TRANSFERABLE, "To Spirit World Transferable")

        add("death.attack.inSun", "Turned to ash but the sun")

        add("witchery.add_page.1", "Added the first page to the key")
        add("witchery.add_page.2", "Added the second page to the key")
        add("witchery.add_page.3", "Added the third page to the key")
        add("witchery.add_page.4", "Added the forth page to the key")
        add("witchery.add_page.5", "Added the fifth page to the key")
        add("witchery.add_page.6", "Added the sixth page to the key")
        add("witchery.add_page.7", "Added the seventh page to the key")
        add("witchery.add_page.8", "Added the eight page to the key")
        add("witchery.add_page.9", "Added the final page to the key")

        add("emi.category.witchery.cauldron_brewing", "Cauldron Brewing")
        add("emi.category.witchery.cauldron_crafting", "Cauldron Crafting")
        add("emi.category.witchery.ritual", "Ritual")
        add("emi.category.witchery.oven_cooking", "Oven Fumigation")
        add("emi.category.witchery.distilling", "Distilling")
        add("emi.category.witchery.spinning", "Spinning")



        add("container.witchery.oven_menu", "Witches Oven")
        add("container.witchery.altar_menu", "Altar")
        add("container.witchery.spinning_wheel", "Spinning Wheel")
        add("container.witchery.distillery", "Distillery")

        add("trinkets.slot.chest.charm", "Charm")
        add("trinkets.slot.legs.poppet", "Poppet")

        add("witchery.secondbrewbonus.25", "+25% chance of second brew")
        add("witchery.secondbrewbonus.35", "+35% chance of second brew")
        add("witchery.thirdbrewbonus.25", "+25% chance of third brew")
        add("witchery.infusion.ointment", "Flying Ointment")

        add("witchery.blood", "Blood")
        add("witchery.vampire_blood", "Blood?")
        add("witchery.use_with_needle", "Use with Bone Needle to fill")

        add("witchery:all_worlds", "All Worlds")
        add("witchery:dream_world", "Dream World")
        add("witchery:nightmare_world", "Nightmare World")

        add("witchery.item.tooltip.infinity_egg", "Creative Only")

        add("witchery.celestial.day", "Day")
        add("witchery.celestial.full", "Full Moon")
        add("witchery.celestial.new", "New Moon")
        add("witchery.celestial.waning", "Waning Moon")
        add("witchery.celestial.waxing", "Waxing Moon")

        add("witchery.captured.silverfish", "Silverfish")
        add("witchery.captured.slime", "Slime")
        add("witchery.captured.bat", "Bat")

        add("witchery.attuned.charged", "Attuned")
        add("witchery.has_sun", "Sunlight")

        add("attribute.name.witchery.vampire_bat_form_duration", "Bat-form Duration")
        add("attribute.name.witchery.vampire_drink_speed", "Blooding Drink Speed")
        add("attribute.name.witchery.vampire_sun_resistance", "Sun Resistance")

        add("entity.witchery.rowan_boat", "Rowan Boat")
        add("entity.witchery.rowan_chest_boat", "Rowan Chest Boat")
        add("entity.witchery.alder_boat", "Alder Boat")
        add("entity.witchery.alder_chest_boat", "Alder Chest Boat")
        add("entity.witchery.hawthorn_boat", "Hawthorn Boat")
        add("entity.witchery.hawthorn_chest_boat", "Hawthorn Chest Boat")

        add("witchery.brazier.category", "Brazier")
        add("witchery.cauldron_brewing.category", "Cauldron Brewing")
        add("witchery.cauldron_crafting.category", "Cauldron Crafting")
        add("witchery.ritual.category", "Ritual")
        add("witchery.oven.category", "Oven Fumigation")
        add("witchery.distilling.category", "Distilling")
        add("witchery.spinning.category", "Spinning")

        add("emi.category.witchery.brazier", "Brazier")
        add("witchery:brazier_summoning/summon_banshee", "Summon Banshee")
        add("witchery:brazier_summoning/summon_banshee.tooltip", "Summons a Banshee")
        add("witchery:brazier_summoning/summon_spectre", "Summon Spectre")
        add("witchery:brazier_summoning/summon_spectre.tooltip", "Summons a Spectre")


        add("witchery.too_few_in_coven", "Coven too small")

        add("witchery.curse.afflicted", "%s is afflicted by %s")
        add("witchery.curse.free", "%s is free from curses!")

        add("witchery.curse.misfortune.name", "the Curse of Misfortune")
        add("witchery.curse.insanity.name", "the Curse of Insanity")
        add("witchery.curse.corrupt_poppet.name", "the Curse of Corrupt Poppet")
        add("witchery.curse.overheating.name", "the Curse of Overheating")
        add("witchery.curse.sinking.name", "the Curse of Sinking")
        add("witchery.curse.befuddlement.name", "the Curse of Befuddlement")
        add("witchery.curse.hunger.name", "the Curse of Hunger")
        add("witchery.curse.fragility.name", "the Curse of Fragility")

        add("key.categories.witchery", "Witchery")
        add("key.witchery.dismount", "Dismount Broom")
        add("key.witchery.open_ability_selection", "Open Ability Selection")
        add("key.witchery.utility_button", "Utility Button")
    }
}