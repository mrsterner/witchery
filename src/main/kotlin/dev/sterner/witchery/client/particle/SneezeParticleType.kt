package dev.sterner.witchery.client.particle

import com.mojang.serialization.MapCodec
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec

class SneezeParticleType : ParticleType<SneezeData>(true) {

    override fun codec(): MapCodec<SneezeData> {
        return SneezeData.CODEC
    }

    override fun streamCodec(): StreamCodec<in RegistryFriendlyByteBuf, SneezeData> {
        return SneezeData.STREAM_CODEC
    }
}