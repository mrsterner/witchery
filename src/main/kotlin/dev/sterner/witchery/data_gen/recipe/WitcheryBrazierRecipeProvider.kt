package dev.sterner.witchery.data_gen.recipe

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.brazier.BrazierSummoningRecipeBuilder
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.data.recipes.RecipeOutput

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
            .addSummon(WitcheryEntityTypes.SPECTRE.get())
            .save(exporter, Witchery.id("summon_spectre"))

    }
}