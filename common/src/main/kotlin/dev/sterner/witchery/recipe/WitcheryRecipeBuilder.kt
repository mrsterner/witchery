package dev.sterner.witchery.recipe

import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

abstract class WitcheryRecipeBuilder : RecipeBuilder {

    fun suffixHash(input: ResourceLocation, itemStack: List<ItemStack>): ResourceLocation {
        var hashedId = input

        for (item in itemStack) {
            val suffix = "_from_${item.item.`arch$registryName`()!!.path}"
            hashedId = hashedId.withSuffix(suffix)
        }

        return hashedId
    }
}