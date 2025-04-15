package dev.sterner.witchery.api.potion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

data class DurationAmplifier(val duration: Int, val amplifier: Int){
    companion object {
        val CODEC: Codec<DurationAmplifier> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.INT.fieldOf("duration").forGetter { it.duration },
                Codec.INT.fieldOf("amplifier").forGetter { it.amplifier }
            ).apply(instance, ::DurationAmplifier)
        }
    }
}
