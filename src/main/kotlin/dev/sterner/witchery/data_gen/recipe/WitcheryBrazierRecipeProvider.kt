package dev.sterner.witchery.data_gen.recipe

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.brazier.BrazierPassiveRecipeBuilder
import dev.sterner.witchery.content.recipe.brazier.BrazierSummoningRecipeBuilder
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.features.brazier.FogBrazierPassive
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
            .addInput(Items.DIAMOND)
            .addInput(Items.STICK)
            .setAltarPower(200)
            .setPassive(FogBrazierPassive())
            .save(exporter, Witchery.id("fog_brazier_passive"))
    }
}