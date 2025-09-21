package dev.sterner.witchery.datagen.recipe

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipeBuilder
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.Items

object WitcheryDistillingRecipeProvider {

    fun distill(exporter: RecipeOutput) {


        DistilleryCraftingRecipeBuilder.create()
            .addInput(WitcheryItems.FOUL_FUME.get())
            .addInput(WitcheryItems.WOOD_ASH.get())
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.OIL_OF_VITRIOL.get())
            .addOutput(WitcheryItems.GYPSUM.get())
            .addOutput(Items.SLIME_BALL)
            .setJarConsumption(1)
            .save(exporter, Witchery.id("oil_of_vitriol_gypsum"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(WitcheryItems.BREATH_OF_THE_GODDESS.get())
            .addInput(Items.LAPIS_LAZULI)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.TEAR_OF_THE_GODDESS.get())
            .addOutput(WitcheryItems.WHIFF_OF_MAGIC.get())
            .addOutput(Items.SLIME_BALL)
            .addOutput(WitcheryItems.FOUL_FUME.get())
            .setJarConsumption(3)
            .save(exporter, Witchery.id("tear_and_whiff"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(Items.ENDER_PEARL)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.ENDER_DEW.get(), 2)
            .addOutput(WitcheryItems.ENDER_DEW.get(), 2)
            .addOutput(WitcheryItems.ENDER_DEW.get(), 1)
            .addOutput(WitcheryItems.WHIFF_OF_MAGIC.get())
            .setJarConsumption(6)
            .save(exporter, Witchery.id("ender_dew"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(Items.BLAZE_POWDER)
            .addInput(Items.GUNPOWDER)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.REEK_OF_MISFORTUNE.get())
            .addOutput(Items.GLOWSTONE_DUST)
            .addOutput(Items.GLOWSTONE_DUST)
            .setJarConsumption(1)
            .save(exporter, Witchery.id("reek_of_misfortune_glowstone"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(Items.PHANTOM_MEMBRANE)
            .addInput(WitcheryItems.OIL_OF_VITRIOL.get())
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.PHANTOM_VAPOR.get(), 1)
            .addOutput(WitcheryItems.PHANTOM_VAPOR.get(), 1)
            .addOutput(WitcheryItems.ODOR_OF_PURITY.get())
            .setJarConsumption(3)
            .save(exporter, Witchery.id("phantom_vapor"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(WitcheryItems.DEMON_HEART.get())
            .addInput(WitcheryItems.PHANTOM_VAPOR.get())
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.DEMONS_BLOOD.get(), 2)
            .addOutput(WitcheryItems.DEMONS_BLOOD.get(), 2)
            .addOutput(WitcheryItems.REFINED_EVIL.get())
            .setJarConsumption(4)
            .save(exporter, Witchery.id("refined_evil"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(WitcheryItems.DEMON_HEART.get())
            .addInput(Items.NETHERRACK)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.DEMONS_BLOOD.get())
            .addOutput(WitcheryItems.DEMONS_BLOOD.get())
            .addOutput(Items.SOUL_SAND)
            .setJarConsumption(2)
            .save(exporter, Witchery.id("demons_blood"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(WitcheryItems.PHANTOM_VAPOR.get())
            .addInput(Items.GHAST_TEAR)
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.ODOR_OF_PURITY.get())
            .addOutput(WitcheryItems.REEK_OF_MISFORTUNE.get())
            .addOutput(WitcheryItems.FOUL_FUME.get())
            .addOutput(WitcheryItems.REFINED_EVIL.get())
            .setJarConsumption(3)
            .save(exporter, Witchery.id("refined_evil_from_ghast"))

        DistilleryCraftingRecipeBuilder.create()
            .addInput(WitcheryItems.BREW_FLOWING_SPIRIT.get())
            .addInput(WitcheryItems.OIL_OF_VITRIOL.get())
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.FOCUSED_WILL.get())
            .addOutput(WitcheryItems.CONDENSED_FEAR.get())
            //TODO .addOutput(ItemStack(WitcheryItems.BREW_OF_HOLLOW_TEARS.get(), 4))
            //TODO .addOutput(ItemStack(WitcheryItems.BREW_OF_HOLLOW_TEARS.get(), 4))
            .setJarConsumption(2)
            .save(exporter, Witchery.id("brew_of_hollow_tears"))
    }
}