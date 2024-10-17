package dev.sterner.witchery.recipe

import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack

abstract class WitcheryRecipeBuilder : RecipeBuilder {

    fun suffixHash(input: ResourceLocation, itemStack: List<ItemStack>): ResourceLocation {
        var hashedId = input

        itemStack.forEachIndexed { index, item ->
            val suffix = if (index == 0) {
                "_from_${item.item.`arch$registryName`()!!.path}${item.count}"
            } else {
                "_and_${item.item.`arch$registryName`()!!.path}${item.count}"
            }
            hashedId = hashedId.withSuffix(suffix)
        }

        return hashedId
    }
}