package dev.sterner.witchery.registry

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.recipe.TaglockDataComponentTransferRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.recipe.oven.OvenCookingRecipe
import dev.sterner.witchery.recipe.ritual.RitualRecipe
import dev.sterner.witchery.recipe.spinning_wheel.SpinningWheelRecipe
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.*


object WitcheryRecipeSerializers {

    val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(Witchery.MODID, Registries.RECIPE_SERIALIZER)


    val CAULDRON_RECIPE_SERIALIZER: RegistrySupplier<CauldronCraftingRecipe.Serializer> =
        RECIPE_SERIALIZERS.register(CauldronCraftingRecipe.NAME) { CauldronCraftingRecipe.Serializer() }

    val CAULDRON_BREWING_RECIPE_SERIALIZER: RegistrySupplier<CauldronBrewingRecipe.Serializer> =
        RECIPE_SERIALIZERS.register(CauldronBrewingRecipe.NAME) { CauldronBrewingRecipe.Serializer() }

    val OVEN_RECIPE_SERIALIZER: RegistrySupplier<OvenCookingRecipe.Serializer> =
        RECIPE_SERIALIZERS.register(OvenCookingRecipe.NAME) { OvenCookingRecipe.Serializer() }

    val RITUAL_RECIPE_SERIALIZER: RegistrySupplier<RitualRecipe.Serializer> =
        RECIPE_SERIALIZERS.register(RitualRecipe.NAME) { RitualRecipe.Serializer() }

    val DISTILLERY_RECIPE_SERIALIZER: RegistrySupplier<DistilleryCraftingRecipe.Serializer> =
        RECIPE_SERIALIZERS.register(DistilleryCraftingRecipe.NAME) { DistilleryCraftingRecipe.Serializer() }

    val SPINNING_WHEEL_RECIPE_SERIALIZER: RegistrySupplier<SpinningWheelRecipe.Serializer> =
        RECIPE_SERIALIZERS.register(SpinningWheelRecipe.NAME) { SpinningWheelRecipe.Serializer() }

    val TAGLOCK_RECIPE_SERIALIZER: RegistrySupplier<RecipeSerializer<TaglockDataComponentTransferRecipe>> =
        RECIPE_SERIALIZERS.register("crafting_special_taglock") {
            SimpleCraftingRecipeSerializer { TaglockDataComponentTransferRecipe() }
        }
}