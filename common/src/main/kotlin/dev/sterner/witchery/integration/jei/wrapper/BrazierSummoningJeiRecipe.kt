package dev.sterner.witchery.integration.jei.wrapper

import dev.sterner.witchery.recipe.brazier.BrazierSummoningRecipe
import net.minecraft.resources.ResourceLocation

data class BrazierSummoningJeiRecipe(
    val id: ResourceLocation,
    val recipe: BrazierSummoningRecipe
)
