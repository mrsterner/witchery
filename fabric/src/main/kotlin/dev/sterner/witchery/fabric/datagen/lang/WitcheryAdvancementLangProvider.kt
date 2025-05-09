package dev.sterner.witchery.fabric.datagen.lang

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider

object WitcheryAdvancementLangProvider {

    fun advancement(builder: FabricLanguageProvider.TranslationBuilder){
        builder.add("advancements.witchery.seeds.title", "The Start")
        builder.add("advancements.witchery.seeds.description", "Expensive on Etsy")
        builder.add("advancements.witchery.oven.title", "Smells nice")
        builder.add("advancements.witchery.oven.description", "In my gingerbread house")
        builder.add("advancements.witchery.cauldron.title", "A Stew is due")
        builder.add("advancements.witchery.cauldron.description", "Cookin'")
        builder.add("advancements.witchery.mutandis.title", "Lost plants")
        builder.add("advancements.witchery.mutandis.description", "CRISPR those flowers")
        builder.add("advancements.witchery.whiff_of_magic.title", "Wonder")
        builder.add("advancements.witchery.whiff_of_magic.description", "Unicorn farts")
        builder.add("advancements.witchery.chalk.title", "Rituals")
        builder.add("advancements.witchery.chalk.description", "And I started ritualing")
        builder.add("advancements.witchery.necromantic.title", "Necromantic")
        builder.add("advancements.witchery.necromantic.description", "No mind to break")
        builder.add("advancements.witchery.spirit_world.title", "The Spirit World")
        builder.add("advancements.witchery.spirit_world.description", "I have a dream")
        builder.add("advancements.witchery.disturbed.title", "The Disturbed")
        builder.add("advancements.witchery.disturbed.description", "Easy, just don't have a nightmare")

    }
}