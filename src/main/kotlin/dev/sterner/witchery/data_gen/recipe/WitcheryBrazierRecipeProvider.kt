package dev.sterner.witchery.data_gen.recipe

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.brazier.BrazierPassiveRecipeBuilder
import dev.sterner.witchery.content.recipe.brazier.BrazierSummoningRecipeBuilder
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.brazier.AnguishOfTheDeadBrazierPassive
import dev.sterner.witchery.features.brazier.DeathlyVeilBrazierPassive
import dev.sterner.witchery.features.brazier.DrainGrowthBrazierPassive
import dev.sterner.witchery.features.brazier.FortificationOfTheCorpseBrazierPassive
import dev.sterner.witchery.features.brazier.GraveyardMistBrazierPassive
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.Items

object WitcheryBrazierRecipeProvider {

    fun braze(exporter: RecipeOutput) {

        BrazierSummoningRecipeBuilder.create()
            .addInput(WitcheryItems.WORMWOOD.get())
            .addInput(WitcheryItems.CONDENSED_FEAR.get())
            .addInput(WitcheryItems.SPECTRAL_DUST.get())
            .setAltarPower(500)
            .addSummon(WitcheryEntityTypes.BANSHEE.get())
            .save(exporter, Witchery.id("summon_banshee"))

        BrazierSummoningRecipeBuilder.create()
            .addInput(WitcheryItems.WORMWOOD.get())
            .addInput(WitcheryItems.WOOL_OF_BAT.get())
            .addInput(WitcheryItems.SPECTRAL_DUST.get())
            .setAltarPower(500)
            .addSummon(WitcheryEntityTypes.POLTERGEIST.get())
            .save(exporter, Witchery.id("summon_spectre"))

        BrazierSummoningRecipeBuilder.create()
            .addInput(WitcheryItems.WORMWOOD.get())
            .addInput(WitcheryItems.REFINED_EVIL.get())
            .addInput(WitcheryItems.FOCUSED_WILL.get())
            .setAltarPower(500)
            .addSummon(WitcheryEntityTypes.POLTERGEIST.get())
            .save(exporter, Witchery.id("summon_poltergeist"))

        BrazierPassiveRecipeBuilder.create()
            .addInput(Items.GUNPOWDER)
            .addInput(WitcheryItems.WOOD_ASH.get())
            .addInput(Items.GLOWSTONE_DUST)
            .setAltarPower(200)
            .setPassive(GraveyardMistBrazierPassive())
            .save(exporter, Witchery.id("graveyard_mist"))


        BrazierPassiveRecipeBuilder.create()
            .addInput(Items.BONE)
            .addInput(Items.BLAZE_ROD)
            .addInput(WitcheryItems.TEAR_OF_THE_GODDESS.get())
            .setAltarPower(200)
            .setPassive(AnguishOfTheDeadBrazierPassive())
            .save(exporter, Witchery.id("anguish_of_the_dead"))


        BrazierPassiveRecipeBuilder.create()
            .addInput(WitcheryItems.TEAR_OF_THE_GODDESS.get())
            .addInput(Items.ROTTEN_FLESH)
            .addInput(Items.BLAZE_POWDER)
            .setAltarPower(200)
            .setPassive(FortificationOfTheCorpseBrazierPassive())
            .save(exporter, Witchery.id("fortification_of_the_corpse"))


        BrazierPassiveRecipeBuilder.create()
            .addInput(Items.ENDER_PEARL)
            .addInput(Items.SPIDER_EYE)
            .addInput(Items.BLAZE_ROD)
            .setAltarPower(200)
            .setPassive(DeathlyVeilBrazierPassive())
            .save(exporter, Witchery.id("deathly_veil"))

        BrazierPassiveRecipeBuilder.create()
            .addInput(Items.APPLE)
            .addInput(WitcheryItems.CONDENSED_FEAR.get())
            .addInput(WitcheryItems.SPECTRAL_DUST.get())
            .setAltarPower(200)
            .setPassive(DrainGrowthBrazierPassive())
            .save(exporter, Witchery.id("drain_growth"))
    }
}