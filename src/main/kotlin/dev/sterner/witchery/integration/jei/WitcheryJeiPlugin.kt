package dev.sterner.witchery.integration.jei

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.content.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.content.recipe.cauldron.CauldronInfusionRecipe
import dev.sterner.witchery.content.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.content.recipe.oven.OvenCookingRecipe
import dev.sterner.witchery.content.recipe.spinning_wheel.SpinningWheelRecipe
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryRecipeTypes
import dev.sterner.witchery.integration.jei.wrapper.BrazierSummoningJeiRecipe
import dev.sterner.witchery.integration.jei.wrapper.RitualJeiRecipe
import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.registration.IRecipeCatalystRegistration
import mezz.jei.api.registration.IRecipeCategoryRegistration
import mezz.jei.api.registration.IRecipeRegistration
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.resources.ResourceLocation
import java.util.stream.Collectors


@JeiPlugin
class WitcheryJeiPlugin : IModPlugin {

    override fun registerCategories(registration: IRecipeCategoryRegistration) {
        val guiHelper = registration.jeiHelpers

        for (recipe in registration.jeiHelpers.getRecipeType(Witchery.id("brazier_summoning")).stream()) {

        }
        registration.addRecipeCategories(RitualJeiRecipeCategory(guiHelper))
        registration.addRecipeCategories(BrazierRecipeCategory(guiHelper))
        registration.addRecipeCategories(CauldronBrewingRecipeCategory(guiHelper))
        registration.addRecipeCategories(CauldronInfusionRecipeCategory(guiHelper))
        registration.addRecipeCategories(CauldronCraftingRecipeCategory(guiHelper))
        registration.addRecipeCategories(DistillingRecipeCategory(guiHelper))
        registration.addRecipeCategories(OvenRecipeCategory(guiHelper))
        registration.addRecipeCategories(SpinningRecipeCategory(guiHelper))
    }

    override fun registerRecipes(registration: IRecipeRegistration) {
        val level: ClientLevel? = Minecraft.getInstance().level
        if (level != null) {

            val wrappedRitualRecipes = level.recipeManager
                .getAllRecipesFor(WitcheryRecipeTypes.RITUAL_RECIPE_TYPE.get())
                .map { RitualJeiRecipe(it.id, it.value) }

            registration.addRecipes(RITUAL, wrappedRitualRecipes)

            val wrappedRecipes = level.recipeManager
                .getAllRecipesFor(WitcheryRecipeTypes.BRAZIER_SUMMONING_RECIPE_TYPE.get())
                .map { BrazierSummoningJeiRecipe(it.id, it.value) }

            registration.addRecipes(BRAZIER, wrappedRecipes)


            registration.addRecipes(
                CAULDRON_BREWING,
                level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_BREWING_RECIPE_TYPE.get())
                    .stream().map { it.value }.collect(Collectors.toList())
            )

            registration.addRecipes(
                CAULDRON_CRAFTING,
                level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_RECIPE_TYPE.get())
                    .stream().map { it.value }.collect(Collectors.toList())
            )


            registration.addRecipes(
                CAULDRON_INFUSION,
                level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.CAULDRON_INFUSION_RECIPE_TYPE.get())
                    .stream().map { it.value }.collect(Collectors.toList())
            )

            registration.addRecipes(
                DISTILLING,
                level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.DISTILLERY_RECIPE_TYPE.get())
                    .stream().map { it.value }.collect(Collectors.toList())
            )

            registration.addRecipes(
                OVEN,
                level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.OVEN_RECIPE_TYPE.get())
                    .stream().map { it.value }.collect(Collectors.toList())
            )

            registration.addRecipes(
                SPINNING,
                level.recipeManager.getAllRecipesFor(WitcheryRecipeTypes.SPINNING_WHEEL_RECIPE_TYPE.get())
                    .stream().map { it.value }.collect(Collectors.toList())
            )
        }
    }

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {

        registration.addRecipeCatalyst(WitcheryItems.GOLDEN_CHALK.get().defaultInstance, RITUAL)
        registration.addRecipeCatalyst(WitcheryItems.RITUAL_CHALK.get().defaultInstance, RITUAL)

        registration.addRecipeCatalyst(WitcheryItems.BRAZIER.get().defaultInstance, BRAZIER)

        registration.addRecipeCatalyst(WitcheryItems.CAULDRON.get().defaultInstance, CAULDRON_BREWING, CAULDRON_CRAFTING, CAULDRON_INFUSION)
        registration.addRecipeCatalyst(WitcheryItems.COPPER_CAULDRON.get().defaultInstance, CAULDRON_BREWING, CAULDRON_CRAFTING, CAULDRON_INFUSION)
        registration.addRecipeCatalyst(WitcheryItems.EXPOSED_COPPER_CAULDRON.get().defaultInstance, CAULDRON_BREWING, CAULDRON_CRAFTING, CAULDRON_INFUSION)
        registration.addRecipeCatalyst(WitcheryItems.WEATHERED_COPPER_CAULDRON.get().defaultInstance, CAULDRON_BREWING, CAULDRON_CRAFTING, CAULDRON_INFUSION)
        registration.addRecipeCatalyst(WitcheryItems.OXIDIZED_COPPER_CAULDRON.get().defaultInstance, CAULDRON_BREWING, CAULDRON_CRAFTING, CAULDRON_INFUSION)
        registration.addRecipeCatalyst(WitcheryItems.WAXED_COPPER_CAULDRON.get().defaultInstance, CAULDRON_BREWING, CAULDRON_CRAFTING, CAULDRON_INFUSION)
        registration.addRecipeCatalyst(WitcheryItems.WAXED_EXPOSED_COPPER_CAULDRON.get().defaultInstance, CAULDRON_BREWING, CAULDRON_CRAFTING, CAULDRON_INFUSION)
        registration.addRecipeCatalyst(WitcheryItems.WAXED_WEATHERED_COPPER_CAULDRON.get().defaultInstance, CAULDRON_BREWING, CAULDRON_CRAFTING, CAULDRON_INFUSION)
        registration.addRecipeCatalyst(WitcheryItems.WAXED_OXIDIZED_COPPER_CAULDRON.get().defaultInstance, CAULDRON_BREWING, CAULDRON_CRAFTING, CAULDRON_INFUSION)

        registration.addRecipeCatalyst(WitcheryItems.IRON_WITCHES_OVEN.get().defaultInstance, OVEN)
        registration.addRecipeCatalyst(WitcheryItems.COPPER_WITCHES_OVEN.get().defaultInstance, OVEN)
        registration.addRecipeCatalyst(WitcheryItems.EXPOSED_COPPER_WITCHES_OVEN.get().defaultInstance, OVEN)
        registration.addRecipeCatalyst(WitcheryItems.WEATHERED_COPPER_WITCHES_OVEN.get().defaultInstance, OVEN)
        registration.addRecipeCatalyst(WitcheryItems.OXIDIZED_COPPER_WITCHES_OVEN.get().defaultInstance, OVEN)
        registration.addRecipeCatalyst(WitcheryItems.WAXED_COPPER_WITCHES_OVEN.get().defaultInstance, OVEN)
        registration.addRecipeCatalyst(WitcheryItems.WAXED_EXPOSED_COPPER_WITCHES_OVEN.get().defaultInstance, OVEN)
        registration.addRecipeCatalyst(WitcheryItems.WAXED_WEATHERED_COPPER_WITCHES_OVEN.get().defaultInstance, OVEN)
        registration.addRecipeCatalyst(WitcheryItems.WAXED_OXIDIZED_COPPER_WITCHES_OVEN.get().defaultInstance, OVEN)

        registration.addRecipeCatalyst(WitcheryItems.DISTILLERY.get().defaultInstance, DISTILLING)

        registration.addRecipeCatalyst(WitcheryItems.SPINNING_WHEEL.get().defaultInstance, SPINNING)
    }

    companion object {
        fun register() {

        }

        val ID: ResourceLocation = Witchery.id("main")

        val RITUAL: RecipeType<RitualJeiRecipe> = RecipeType(Witchery.id("ritual"), RitualJeiRecipe::class.java)
        val BRAZIER: RecipeType<BrazierSummoningJeiRecipe> =
            RecipeType(Witchery.id("brazier"), BrazierSummoningJeiRecipe::class.java)
        val CAULDRON_BREWING: RecipeType<CauldronBrewingRecipe> =
            RecipeType(Witchery.id("cauldron_brewing"), CauldronBrewingRecipe::class.java)
        val CAULDRON_CRAFTING: RecipeType<CauldronCraftingRecipe> =
            RecipeType(Witchery.id("cauldron_crafting"), CauldronCraftingRecipe::class.java)
        val CAULDRON_INFUSION: RecipeType<CauldronInfusionRecipe> =
            RecipeType(Witchery.id("cauldron_infusion"), CauldronInfusionRecipe::class.java)
        val DISTILLING: RecipeType<DistilleryCraftingRecipe> =
            RecipeType(Witchery.id("distilling"), DistilleryCraftingRecipe::class.java)
        val OVEN: RecipeType<OvenCookingRecipe> = RecipeType(Witchery.id("oven"), OvenCookingRecipe::class.java)
        val SPINNING: RecipeType<SpinningWheelRecipe> =
            RecipeType(Witchery.id("spinning"), SpinningWheelRecipe::class.java)
    }

    override fun getPluginUid(): ResourceLocation {
        return ID
    }
}