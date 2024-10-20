package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.recipe.oven.OvenCookingRecipe
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.recipe.spinning_wheel.SpinningWheelRecipe
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType


object WitcheryRecipeTypes {

    val RECIPE_TYPES: DeferredRegister<RecipeType<*>> =
        DeferredRegister.create(Witchery.MODID, Registries.RECIPE_TYPE)


    val CAULDRON_RECIPE_TYPE: RegistrySupplier<RecipeType<CauldronCraftingRecipe>> =
        RECIPE_TYPES.register(CauldronCraftingRecipe.NAME) {
            registerRecipeType(CauldronCraftingRecipe.NAME)
        }

    val CAULDRON_BREWING_RECIPE_TYPE: RegistrySupplier<RecipeType<CauldronBrewingRecipe>> =
        RECIPE_TYPES.register(CauldronBrewingRecipe.NAME) {
            registerRecipeType(CauldronBrewingRecipe.NAME)
        }

    val OVEN_RECIPE_TYPE: RegistrySupplier<RecipeType<OvenCookingRecipe>> =
        RECIPE_TYPES.register(OvenCookingRecipe.NAME) {
            registerRecipeType(OvenCookingRecipe.NAME)
        }

    val RITUAL_RECIPE_TYPE: RegistrySupplier<RecipeType<RitualRecipe>> =
        RECIPE_TYPES.register(RitualRecipe.NAME) {
            registerRecipeType(RitualRecipe.NAME)
        }

    val DISTILLERY_RECIPE_TYPE: RegistrySupplier<RecipeType<DistilleryCraftingRecipe>> =
        RECIPE_TYPES.register(DistilleryCraftingRecipe.NAME) {
            registerRecipeType(DistilleryCraftingRecipe.NAME)
        }

    val SPINNING_WHEEL_RECIPE_TYPE: RegistrySupplier<RecipeType<SpinningWheelRecipe>> =
        RECIPE_TYPES.register(SpinningWheelRecipe.NAME) {
            registerRecipeType(SpinningWheelRecipe.NAME)
        }

    private fun <T : Recipe<*>> registerRecipeType(identifier: String): RecipeType<T> {
        return object : RecipeType<T> {
            override fun toString(): String {
                return Witchery.MODID + ":" + identifier
            }
        }
    }
}