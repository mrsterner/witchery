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


class ZzzData(val alpha: Float) : ParticleOptions {

    override fun getType(): ParticleType<*> {
        return WitcheryParticleTypes.ZZZ.get()
    }

    companion object {

        val CODEC: MapCodec<ZzzData> =
            RecordCodecBuilder.mapCodec { instance: RecordCodecBuilder.Instance<ZzzData> ->
                instance.group(
                    Codec.FLOAT.fieldOf("alpha").forGetter { data -> data.alpha }
                ).apply(instance) { alpha -> ZzzData(alpha) }
            }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ZzzData> =
            StreamCodec.composite(
                ByteBufCodecs.FLOAT,
                ZzzData::alpha
            ) { alpha ->
                ZzzData(
                    alpha
                )
            }
    }
}