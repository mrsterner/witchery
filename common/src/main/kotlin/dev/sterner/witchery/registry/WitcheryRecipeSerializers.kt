package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer


object WitcheryRecipeSerializers {

    val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(Witchery.MODID, Registries.RECIPE_SERIALIZER)


    val CAULDRON_RECIPE_SERIALIZER: RegistrySupplier<CauldronCraftingRecipe.Serializer> =
        RECIPE_SERIALIZERS.register(CauldronCraftingRecipe.NAME) { CauldronCraftingRecipe.Serializer() }

    val CAULDRON_BREWING_RECIPE_SERIALIZER: RegistrySupplier<CauldronBrewingRecipe.Serializer> =
        RECIPE_SERIALIZERS.register(CauldronBrewingRecipe.NAME) { CauldronBrewingRecipe.Serializer() }

}