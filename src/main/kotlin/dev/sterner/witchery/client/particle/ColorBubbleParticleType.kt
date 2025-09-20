package dev.sterner.witchery.client.particle

import com.mojang.serialization.MapCodec
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec

class ColorBubbleParticleType : ParticleType<ColorBubbleData>(true) {

    override fun codec(): MapCodec<ColorBubbleData> {
        return ColorBubbleData.CODEC
    }

    override fun streamCodec(): StreamCodec<in RegistryFriendlyByteBuf, ColorBubbleData> {
        return ColorBubbleData.STREAM_CODEC
    }
}