package dev.sterner.witchery.item.potion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.resources.ResourceLocation
import java.util.*

data class WitcheryPotionEffect(
    val effectId: ResourceLocation,
    val duration: Int,
    val amplifier: Int,
    val inverse: Optional<ResourceLocation>
) {
    companion object {
        val CODEC: Codec<WitcheryPotionEffect> = RecordCodecBuilder.create { instance ->
            instance.group(
                ResourceLocation.CODEC.fieldOf("effect").forGetter { it.effectId },
                Codec.INT.fieldOf("duration").forGetter { it.duration },
                Codec.INT.fieldOf("amplifier").forGetter { it.amplifier },
                ResourceLocation.CODEC.optionalFieldOf("inverse").forGetter { it.inverse }
            ).apply(instance, ::WitcheryPotionEffect)
        }
    }
}
