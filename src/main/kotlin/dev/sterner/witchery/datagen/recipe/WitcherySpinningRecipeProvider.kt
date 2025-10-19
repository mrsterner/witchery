package dev.sterner.witchery.datagen.recipe

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.spinning_wheel.SpinningWheelRecipeBuilder
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.alchemy.Potions

object WitcherySpinningRecipeProvider {

    fun spin(exporter: RecipeOutput) {

        SpinningWheelRecipeBuilder.create()
            .addInput(WitcheryItems.DREAM_WEAVER.get())
            .addInput(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.POISON))
            .addInput(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.NIGHT_VISION))
            .addInput(ItemStack(WitcheryItems.TORMENTED_TWINE.get(), 2))
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.DREAM_WEAVER_OF_NIGHTMARES.get())
            .save(exporter, Witchery.id("dream_weaver_of_nightmares"))

        SpinningWheelRecipeBuilder.create()
            .addInput(WitcheryItems.DREAM_WEAVER.get())
            .addInput(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.SLOWNESS))
            .addInput(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.SWIFTNESS))
            .addInput(ItemStack(WitcheryItems.FANCIFUL_THREAD.get(), 2))
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.DREAM_WEAVER_OF_FLEET_FOOT.get())
            .save(exporter, Witchery.id("dream_weaver_of_fleet_foot"))

        SpinningWheelRecipeBuilder.create()
            .addInput(WitcheryItems.DREAM_WEAVER.get())
            .addInput(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.HEALING))
            .addInput(ItemStack(WitcheryItems.MELLIFLUOUS_HUNGER.get(), 1))
            .addInput(ItemStack(WitcheryItems.FANCIFUL_THREAD.get(), 2))
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.DREAM_WEAVER_OF_FASTING.get())
            .save(exporter, Witchery.id("dream_weaver_of_fasting"))

        SpinningWheelRecipeBuilder.create()
            .addInput(WitcheryItems.DREAM_WEAVER.get())
            .addInput(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.WEAKNESS))
            .addInput(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.STRENGTH))
            .addInput(ItemStack(WitcheryItems.FANCIFUL_THREAD.get(), 2))
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.DREAM_WEAVER_OF_IRON_ARM.get())
            .save(exporter, Witchery.id("dream_weaver_of_iron_arm"))

        SpinningWheelRecipeBuilder.create()
            .addInput(Items.HAY_BLOCK)
            .addInput(WitcheryItems.WHIFF_OF_MAGIC.get())
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.GOLDEN_THREAD.get())
            .save(exporter, Witchery.id("golden_thread"))

        SpinningWheelRecipeBuilder.create()
            .addInput(Items.WHITE_WOOL)
            .addInput(WitcheryItems.WHIFF_OF_MAGIC.get())
            .addInput(WitcheryItems.PHANTOM_VAPOR.get())
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.IMPREGNATED_FABRIC.get(), 2)
            .save(exporter, Witchery.id("impregnated_fabric"))

        SpinningWheelRecipeBuilder.create()
            .addInput(WitcheryItems.DISTURBED_COTTON.get())
            .addInput(Items.STRING)
            .addInput(WitcheryItems.REEK_OF_MISFORTUNE.get())
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.TORMENTED_TWINE.get(), 4)
            .save(exporter, Witchery.id("tormented_twine"))

        SpinningWheelRecipeBuilder.create()
            .addInput(WitcheryItems.WISPY_COTTON.get())
            .addInput(Items.STRING)
            .addInput(WitcheryItems.ODOR_OF_PURITY.get())
            .setAltarPower(5)
            .setCookingTime(100)
            .addOutput(WitcheryItems.FANCIFUL_THREAD.get(), 4)
            .save(exporter, Witchery.id("fanciful_thread"))

    }
}