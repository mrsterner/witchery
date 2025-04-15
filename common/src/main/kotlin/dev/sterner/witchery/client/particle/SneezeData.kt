package dev.sterner.witchery.client.particle

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.registry.WitcheryParticleTypes
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec


class SneezeData(val red: Float, val green: Float, val blue: Float) : ParticleOptions {

    override fun getType(): ParticleType<*> {
        return WitcheryParticleTypes.SNEEZE.get()
    }

    companion object {

        val CODEC: MapCodec<SneezeData> =
            RecordCodecBuilder.mapCodec { instance: RecordCodecBuilder.Instance<SneezeData> ->
                instance.group(
                    Codec.FLOAT.fieldOf("red").forGetter { data -> data.red },
                    Codec.FLOAT.fieldOf("green").forGetter { data -> data.green },
                    Codec.FLOAT.fieldOf("blue").forGetter { data -> data.blue }
                ).apply(instance) { red, green, blue -> SneezeData(red, green, blue) }
            }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SneezeData> =
            StreamCodec.composite(
                ByteBufCodecs.FLOAT,
                SneezeData::red,
                ByteBufCodecs.FLOAT,
                SneezeData::green,
                ByteBufCodecs.FLOAT,
                SneezeData::blue
            ) { red, green, blue ->
                SneezeData(
                    red,
                    green,
                    blue
                )
            }
    }
}