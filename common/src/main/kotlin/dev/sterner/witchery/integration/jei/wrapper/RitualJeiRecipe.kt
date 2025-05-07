package dev.sterner.witchery.integration.jei.wrapper

import dev.sterner.witchery.recipe.ritual.RitualRecipe
import net.minecraft.resources.ResourceLocation

data class RitualJeiRecipe(
    val id: ResourceLocation,
    val recipe: RitualRecipe
)
