package dev.sterner.witchery.client.particle

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.core.registry.WitcheryParticleTypes
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec


class ColorBubbleData(val red: Float, val green: Float, val blue: Float) : ParticleOptions {

    override fun getType(): ParticleType<*> {
        return WitcheryParticleTypes.COLOR_BUBBLE.get()
    }

    companion object {

        val CODEC: MapCodec<ColorBubbleData> =
            RecordCodecBuilder.mapCodec { instance: RecordCodecBuilder.Instance<ColorBubbleData> ->
                instance.group(
                    Codec.FLOAT.fieldOf("red").forGetter { data -> data.red },
                    Codec.FLOAT.fieldOf("green").forGetter { data -> data.green },
                    Codec.FLOAT.fieldOf("blue").forGetter { data -> data.blue }
                ).apply(instance) { red, green, blue -> ColorBubbleData(red, green, blue) }
            }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ColorBubbleData> =
            StreamCodec.composite(
                ByteBufCodecs.FLOAT,
                ColorBubbleData::red,
                ByteBufCodecs.FLOAT,
                ColorBubbleData::green,
                ByteBufCodecs.FLOAT,
                ColorBubbleData::blue
            ) { red, green, blue ->
                ColorBubbleData(
                    red,
                    green,
                    blue
                )
            }
    }
}