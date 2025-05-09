package dev.sterner.witchery.fabric.datagen

import dev.sterner.witchery.fabric.datagen.lang.WitcheryAdvancementLangProvider
import dev.sterner.witchery.fabric.datagen.lang.WitcheryBookLangProvider
import dev.sterner.witchery.fabric.datagen.lang.WitcheryRitualLangProvider
import dev.sterner.witchery.registry.*
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceLocation
import java.util.concurrent.CompletableFuture

class WitcheryLangProvider(dataOutput: FabricDataOutput, registryLookup: CompletableFuture<HolderLookup.Provider>) :
    FabricLanguageProvider(dataOutput, registryLookup) {

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

    override fun generateTranslations(registryLookup: HolderLookup.Provider?, builder: TranslationBuilder) {
        builder.add("witchery.main", "Witchery")

        WitcheryRitualLangProvider.ritual(builder)
        WitcheryAdvancementLangProvider.advancement(builder)
        WitcheryBookLangProvider.book(builder)

        for (item in WitcheryItems.LANG_HELPER) {
            builder.add("item.witchery.$item", formatId(item))
        }

        for (block in WitcheryBlocks.LANG_HELPER) {
            builder.add("block.witchery.$block", formatId(block))
        }

        for (entity in WitcheryEntityTypes.LANG_HELPER) {
            builder.add("entity.witchery.$entity", formatId(entity))
        }

        for (special in WitcherySpecialPotionEffects.SPECIALS.entrySet()) {
            builder.add("witchery:${special.value.id.path}", formatId(special.value.id))
        }

        builder.add(WitcheryTags.ROWAN_LOG_ITEMS, "Rowan Logs")
        builder.add(WitcheryTags.ALDER_LOG_ITEMS, "Alder Logs")
        builder.add(WitcheryTags.HAWTHORN_LOG_ITEMS, "Hawthorn Logs")
        builder.add(WitcheryTags.LEAF_ITEMS, "Witchery Leaves")
        builder.add(WitcheryTags.CANDELABRA_ITEMS, "Candelabras")
        builder.add(WitcheryTags.PLACEABLE_POPPETS, "Placeable Poppets")
        builder.add(WitcheryTags.FROM_SPIRIT_WORLD_TRANSFERABLE, "From Spirit World Transferable")
        builder.add(WitcheryTags.TO_SPIRIT_WORLD_TRANSFERABLE, "To Spirit World Transferable")

        builder.add("death.attack.inSun", "Turned to ash but the sun")

        builder.add("witchery.add_page.1", "Added the first page to the key")
        builder.add("witchery.add_page.2", "Added the second page to the key")
        builder.add("witchery.add_page.3", "Added the third page to the key")
        builder.add("witchery.add_page.4", "Added the forth page to the key")
        builder.add("witchery.add_page.5", "Added the fifth page to the key")
        builder.add("witchery.add_page.6", "Added the sixth page to the key")
        builder.add("witchery.add_page.7", "Added the seventh page to the key")
        builder.add("witchery.add_page.8", "Added the eight page to the key")
        builder.add("witchery.add_page.9", "Added the final page to the key")

        builder.add("emi.category.witchery.cauldron_brewing", "Cauldron Brewing")
        builder.add("emi.category.witchery.cauldron_crafting", "Cauldron Crafting")
        builder.add("emi.category.witchery.ritual", "Ritual")
        builder.add("emi.category.witchery.oven_cooking", "Oven Fumigation")
        builder.add("emi.category.witchery.distilling", "Distilling")
        builder.add("emi.category.witchery.spinning", "Spinning")



        builder.add("container.witchery.oven_menu", "Witches Oven")
        builder.add("container.witchery.altar_menu", "Altar")
        builder.add("container.witchery.spinning_wheel", "Spinning Wheel")
        builder.add("container.witchery.distillery", "Distillery")

        builder.add("trinkets.slot.chest.charm", "Charm")
        builder.add("trinkets.slot.legs.poppet", "Poppet")

        builder.add("witchery.secondbrewbonus.25", "+25% chance of second brew")
        builder.add("witchery.secondbrewbonus.35", "+35% chance of second brew")
        builder.add("witchery.thirdbrewbonus.25", "+25% chance of third brew")
        builder.add("witchery.infusion.ointment", "Flying Ointment")

        builder.add("witchery.blood", "Blood")
        builder.add("witchery.vampire_blood", "Blood?")
        builder.add("witchery.use_with_needle", "Use with Bone Needle to fill")

        builder.add("witchery:all_worlds", "All Worlds")
        builder.add("witchery:dream_world", "Dream World")
        builder.add("witchery:nightmare_world", "Nightmare World")

        builder.add("witchery.item.tooltip.infinity_egg", "Creative Only")

        builder.add("witchery.celestial.day", "Day")
        builder.add("witchery.celestial.full", "Full Moon")
        builder.add("witchery.celestial.new", "New Moon")
        builder.add("witchery.celestial.waning", "Waning Moon")
        builder.add("witchery.celestial.waxing", "Waxing Moon")

        builder.add("witchery.captured.silverfish", "Silverfish")
        builder.add("witchery.captured.slime", "Slime")
        builder.add("witchery.captured.bat", "Bat")

        builder.add("witchery.attuned.charged", "Attuned")
        builder.add("witchery.has_sun", "Sunlight")

        builder.add("attribute.name.witchery.vampire_bat_form_duration", "Bat-form Duration")
        builder.add("attribute.name.witchery.vampire_drink_speed", "Blooding Drink Speed")
        builder.add("attribute.name.witchery.vampire_sun_resistance", "Sun Resistance")

        builder.add("entity.witchery.rowan_boat", "Rowan Boat")
        builder.add("entity.witchery.rowan_chest_boat", "Rowan Chest Boat")
        builder.add("entity.witchery.alder_boat", "Alder Boat")
        builder.add("entity.witchery.alder_chest_boat", "Alder Chest Boat")
        builder.add("entity.witchery.hawthorn_boat", "Hawthorn Boat")
        builder.add("entity.witchery.hawthorn_chest_boat", "Hawthorn Chest Boat")

        builder.add("witchery.brazier.category", "Brazier")
        builder.add("witchery.cauldron_brewing.category", "Cauldron Brewing")
        builder.add("witchery.cauldron_crafting.category", "Cauldron Crafting")
        builder.add("witchery.ritual.category", "Ritual")
        builder.add("witchery.oven.category", "Oven Fumigation")
        builder.add("witchery.distilling.category", "Distilling")
        builder.add("witchery.spinning.category", "Spinning")

        builder.add("emi.category.witchery.brazier", "Brazier")
        builder.add("witchery:brazier_summoning/summon_banshee", "Summon Banshee")
        builder.add("witchery:brazier_summoning/summon_banshee.tooltip", "Summons a Banshee")
        builder.add("witchery:brazier_summoning/summon_spectre", "Summon Spectre")
        builder.add("witchery:brazier_summoning/summon_spectre.tooltip", "Summons a Spectre")


        builder.add("witchery.too_few_in_coven", "Coven too small")
    }
}