package dev.sterner.witchery.core.registry

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.content.recipe.PendantDataComponentRecipe
import dev.sterner.witchery.content.recipe.PotionDataComponentTransferRecipe
import dev.sterner.witchery.content.recipe.TaglockDataComponentTransferRecipe
import dev.sterner.witchery.content.recipe.brazier.BrazierSummoningRecipe
import dev.sterner.witchery.content.recipe.cauldron.CauldronBrewingRecipe
import dev.sterner.witchery.content.recipe.cauldron.CauldronCraftingRecipe
import dev.sterner.witchery.content.recipe.cauldron.CauldronInfusionRecipe
import dev.sterner.witchery.content.recipe.distillery.DistilleryCraftingRecipe
import dev.sterner.witchery.content.recipe.oven.OvenCookingRecipe
import dev.sterner.witchery.content.recipe.ritual.RitualRecipe
import dev.sterner.witchery.content.recipe.spinning_wheel.SpinningWheelRecipe
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier


object WitcheryRecipeSerializers {

    val RECIPE_SERIALIZERS: DeferredRegister<RecipeSerializer<*>> =
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, Witchery.MODID)

    val CAULDRON_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register(CauldronCraftingRecipe.NAME, Supplier { CauldronCraftingRecipe.Serializer() })

    val CAULDRON_BREWING_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register(CauldronBrewingRecipe.NAME, Supplier { CauldronBrewingRecipe.Serializer() })

    val OVEN_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register(OvenCookingRecipe.NAME, Supplier { OvenCookingRecipe.Serializer() })

    val RITUAL_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register(RitualRecipe.NAME, Supplier { RitualRecipe.Serializer() })

    val DISTILLERY_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register(DistilleryCraftingRecipe.NAME, Supplier { DistilleryCraftingRecipe.Serializer() })

    val SPINNING_WHEEL_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register(SpinningWheelRecipe.NAME, Supplier { SpinningWheelRecipe.Serializer() })

    val TAGLOCK_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register("crafting_special_taglock", Supplier {
            SimpleCraftingRecipeSerializer { TaglockDataComponentTransferRecipe() }
        })

    val POTION_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register("crafting_special_potion", Supplier {
            SimpleCraftingRecipeSerializer { PotionDataComponentTransferRecipe() }
        })

    val PENDANT_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register("crafting_special_pendant", Supplier {
            SimpleCraftingRecipeSerializer { PendantDataComponentRecipe() }
        })

    val CAULDRON_INFUSION_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("cauldron_infusion", Supplier {
        CauldronInfusionRecipe.Serializer()
    })

    val BRAZIER_SUMMONING_RECIPE_SERIALIZER =
        RECIPE_SERIALIZERS.register(BrazierSummoningRecipe.NAME, Supplier { BrazierSummoningRecipe.Serializer() })

}