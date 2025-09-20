package dev.sterner.witchery.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.TaglockDataComponentTransferRecipe
import dev.sterner.witchery.recipe.brazier.BrazierSummoningRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.recipe.oven.OvenCookingRecipe
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.recipe.spinning_wheel.SpinningWheelRecipe
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier


object WitcheryRecipeTypes {

    val RECIPE_TYPES: DeferredRegister<RecipeType<*>> =
        DeferredRegister.create(Registries.RECIPE_TYPE, Witchery.MODID)

    val CAULDRON_RECIPE_TYPE =
        RECIPE_TYPES.register(CauldronCraftingRecipe.NAME, Supplier {
            registerRecipeType(CauldronCraftingRecipe.NAME)
        })

    val CAULDRON_BREWING_RECIPE_TYPE =
        RECIPE_TYPES.register(CauldronBrewingRecipe.NAME, Supplier {
            registerRecipeType(CauldronBrewingRecipe.NAME)
        })

    val OVEN_RECIPE_TYPE =
        RECIPE_TYPES.register(OvenCookingRecipe.NAME, Supplier {
            registerRecipeType(OvenCookingRecipe.NAME)
        })

    val RITUAL_RECIPE_TYPE =
        RECIPE_TYPES.register(RitualRecipe.NAME, Supplier {
            registerRecipeType(RitualRecipe.NAME)
        })

    val DISTILLERY_RECIPE_TYPE =
        RECIPE_TYPES.register(DistilleryCraftingRecipe.NAME, Supplier {
            registerRecipeType(DistilleryCraftingRecipe.NAME)
        })

    val SPINNING_WHEEL_RECIPE_TYPE =
        RECIPE_TYPES.register(SpinningWheelRecipe.NAME, Supplier {
            registerRecipeType(SpinningWheelRecipe.NAME)
        })

    val TAGLOCK_RECIPE_TYPE =
        RECIPE_TYPES.register(TaglockDataComponentTransferRecipe.NAME, Supplier {
            registerRecipeType(TaglockDataComponentTransferRecipe.NAME)
        })

    val BRAZIER_SUMMONING_RECIPE_TYPE =
        RECIPE_TYPES.register(BrazierSummoningRecipe.NAME, Supplier {
            registerRecipeType(BrazierSummoningRecipe.NAME)
        })

    private fun registerRecipeType(identifier: String): RecipeType<*> {
        return object : RecipeType<Recipe<*>> {
            override fun toString(): String {
                return "${Witchery.MODID}:$identifier"
            }
        }
    }
}
