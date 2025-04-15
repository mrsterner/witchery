package dev.sterner.witchery.api.potion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.item.potion.WitcheryPotionIngredient

data class FinalPotionData(val durationAmplifier: DurationAmplifier, val ingredientInfo: WitcheryPotionIngredient) {
    companion object {
        val CODEC: Codec<FinalPotionData> = RecordCodecBuilder.create { instance ->
            instance.group(
                DurationAmplifier.CODEC.fieldOf("durationAmplifier").forGetter { it.durationAmplifier },
                WitcheryPotionIngredient.CODEC.fieldOf("ingredientInfo").forGetter { it.ingredientInfo }
            ).apply(instance, ::FinalPotionData)
        }
    }
}
