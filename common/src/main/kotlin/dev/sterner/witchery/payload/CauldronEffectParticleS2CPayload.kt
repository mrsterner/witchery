package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.client.particle.ParticleEngine
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.util.Mth
import org.joml.Vector3d

class CauldronEffectParticleS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(pos: Vector3d, color: Int) : this(
        CompoundTag().apply {
            putDouble("x", pos.x)
            putDouble("y", pos.y)
            putDouble("z", pos.z)
            putInt("color", color)
        }
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: CauldronEffectParticleS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()
        val pos = Vector3d(payload.nbt.getDouble("x"), payload.nbt.getDouble("y"), payload.nbt.getDouble("z"))
        val color = payload.nbt.getInt("color")

        val r = ((color shr 16) and 0xFF) / 255.0f
        val g = ((color shr 8) and 0xFF) / 255.0f
        val b = (color and 0xFF) / 255.0f

        client.execute {
            for (i in 0..2) {
                val manager: ParticleEngine = client.particleEngine

                val effectParticle = manager.createParticle(
                    ParticleTypes.EFFECT, pos.x, pos.y, pos.z,
                    0.0, 0.0, 0.0
                )

                effectParticle?.setColor(r, g, b)
            }
            val bl = client.level?.random?.nextFloat()!! > 0.75
            if (bl) {
                val randY = pos.y + Mth.nextDouble(client.level!!.random, -0.25, 0.25)
                client.level?.addParticle(ParticleTypes.END_ROD, pos.x, randY, pos.z, 0.0, 0.0, 0.0)
            }

        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<CauldronEffectParticleS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("cauldron_effect"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, CauldronEffectParticleS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> CauldronEffectParticleS2CPayload(buf) }
            )
    }
}