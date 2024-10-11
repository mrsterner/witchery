package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
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

    private fun <T : Recipe<*>> registerRecipeType(identifier: String): RecipeType<T> {
        return object : RecipeType<T> {
            override fun toString(): String {
                return Witchery.MODID + ":" + identifier
            }
        }
    }
}