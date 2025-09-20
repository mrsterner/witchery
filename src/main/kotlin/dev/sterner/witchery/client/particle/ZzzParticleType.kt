package dev.sterner.witchery.client.particle

import com.mojang.serialization.MapCodec
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec

class ZzzParticleType : ParticleType<ZzzData>(true) {

    override fun codec(): MapCodec<ZzzData> {
        return ZzzData.CODEC
    }

    override fun streamCodec(): StreamCodec<in RegistryFriendlyByteBuf, ZzzData> {
        return ZzzData.STREAM_CODEC
    }
}