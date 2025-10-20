package dev.sterner.witchery.data_gen.recipe

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.oven.OvenCookingRecipeBuilder
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient

object WitcheryOvenRecipeProvider {

    fun oven(exporter: RecipeOutput) {

        OvenCookingRecipeBuilder.create()
            .addIngredient(Ingredient.of(Items.OAK_SAPLING))
            .addExtraIngredient(Ingredient.of(WitcheryItems.JAR.get()))
            .addResult(WitcheryItems.WOOD_ASH.get())
            .addExtraOutput(WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get(), 0.5f)
            .setExperience(0.5f)
            .setCookingTime(85)
            .save(exporter, Witchery.id("exhale_of_the_horned_one"))

        OvenCookingRecipeBuilder.create()
            .addIngredient(Ingredient.of(Items.DARK_OAK_SAPLING))
            .addExtraIngredient(Ingredient.of(WitcheryItems.JAR.get()))
            .addResult(WitcheryItems.WOOD_ASH.get())
            .addExtraOutput(WitcheryItems.EXHALE_OF_THE_HORNED_ONE.get(), 0.5f)
            .setExperience(0.5f)
            .setCookingTime(85)
            .save(exporter, Witchery.id("exhale_of_the_horned_one2"))

        OvenCookingRecipeBuilder.create()
            .addIngredient(Ingredient.of(Items.BIRCH_SAPLING))
            .addExtraIngredient(Ingredient.of(WitcheryItems.JAR.get()))
            .addResult(WitcheryItems.WOOD_ASH.get())
            .addExtraOutput(WitcheryItems.BREATH_OF_THE_GODDESS.get(), 0.5f)
            .setExperience(0.5f)
            .setCookingTime(85)
            .save(exporter, Witchery.id("breath_of_the_goddess"))

        OvenCookingRecipeBuilder.create()
            .addIngredient(Ingredient.of(Items.CHERRY_SAPLING))
            .addExtraIngredient(Ingredient.of(WitcheryItems.JAR.get()))
            .addResult(WitcheryItems.WOOD_ASH.get())
            .addExtraOutput(WitcheryItems.BREATH_OF_THE_GODDESS.get(), 0.5f)
            .setExperience(0.5f)
            .setCookingTime(85)
            .save(exporter, Witchery.id("breath_of_the_goddess2"))

        OvenCookingRecipeBuilder.create()
            .addIngredient(Ingredient.of(Items.SPRUCE_SAPLING))
            .addExtraIngredient(Ingredient.of(WitcheryItems.JAR.get()))
            .addResult(WitcheryItems.WOOD_ASH.get())
            .addExtraOutput(WitcheryItems.HINT_OF_REBIRTH.get(), 0.5f)
            .setExperience(0.5f)
            .setCookingTime(85)
            .save(exporter, Witchery.id("hint_of_rebirth"))

        OvenCookingRecipeBuilder.create()
            .addIngredient(Ingredient.of(Items.JUNGLE_SAPLING))
            .addExtraIngredient(Ingredient.of(WitcheryItems.JAR.get()))
            .addResult(WitcheryItems.WOOD_ASH.get())
            .addExtraOutput(WitcheryItems.HINT_OF_REBIRTH.get(), 0.5f)
            .setExperience(0.5f)
            .setCookingTime(85)
            .save(exporter, Witchery.id("hint_of_rebirth2"))

        OvenCookingRecipeBuilder.create()
            .addIngredient(Ingredient.of(WitcheryItems.ROWAN_SAPLING.get()))
            .addExtraIngredient(Ingredient.of(WitcheryItems.JAR.get()))
            .addResult(WitcheryItems.WOOD_ASH.get())
            .addExtraOutput(WitcheryItems.WHIFF_OF_MAGIC.get(), 0.5f)
            .setExperience(0.5f)
            .setCookingTime(85)
            .save(exporter, Witchery.id("whiff_of_magic"))

        OvenCookingRecipeBuilder.create()
            .addIngredient(Ingredient.of(WitcheryItems.ALDER_SAPLING.get()))
            .addExtraIngredient(Ingredient.of(WitcheryItems.JAR.get()))
            .addResult(WitcheryItems.WOOD_ASH.get())
            .addExtraOutput(WitcheryItems.REEK_OF_MISFORTUNE.get(), 0.5f)
            .setExperience(0.5f)
            .setCookingTime(85)
            .save(exporter, Witchery.id("reek_of_misfortune"))

        OvenCookingRecipeBuilder.create()
            .addIngredient(Ingredient.of(WitcheryItems.HAWTHORN_SAPLING.get()))
            .addExtraIngredient(Ingredient.of(WitcheryItems.JAR.get()))
            .addResult(WitcheryItems.WOOD_ASH.get())
            .addExtraOutput(WitcheryItems.ODOR_OF_PURITY.get(), 0.5f)
            .setExperience(0.5f)
            .setCookingTime(85)
            .save(exporter, Witchery.id("odor_of_purity"))

        OvenCookingRecipeBuilder.create()
            .addIngredient(Ingredient.of(ItemTags.LOGS))
            .addExtraIngredient(Ingredient.of(WitcheryItems.JAR.get()))
            .addResult(WitcheryItems.WOOD_ASH.get())
            .addExtraOutput(WitcheryItems.FOUL_FUME.get(), 0.5f)
            .setExperience(0.5f)
            .setCookingTime(85)
            .save(exporter, Witchery.id("foul_fume_logs"))


    }
}