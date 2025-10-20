package dev.sterner.witchery.core.api.block

import dev.sterner.witchery.content.item.potion.WitcheryPotionIngredient
import net.minecraft.resources.ResourceLocation

/**
 * Data class for active potion effects
 */
data class ActiveEffect(
    val id: ResourceLocation,
    val isSpecial: Boolean,
    val amplifier: Int = 0,
    var remainingTicks: Int, // -1 for infinite
    val originalDuration: Int,
    var dispersal: WitcheryPotionIngredient.DispersalModifier = WitcheryPotionIngredient.DispersalModifier(),
    var lastSpecialActivation: Long = 0L
)