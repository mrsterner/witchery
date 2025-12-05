package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.util.Mth

class SpawnPortalParticlesS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()

        val id = nbt.getUUID("Id")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                val pos = player.position()
                for (i in 0..4) {
                    client.level!!.addAlwaysVisibleParticle(
                        ParticleTypes.PORTAL,
                        true,
                        pos.x + 0.0 + Mth.nextDouble(client.level!!.random, -0.5, 0.5),
                        (pos.y + 1.0) + Mth.nextDouble(client.level!!.random, -1.25, 1.25),
                        pos.z + 0.0 + Mth.nextDouble(client.level!!.random, -0.5, 0.5),
                        0.0, 0.2, 0.0
                    )
                }
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SpawnPortalParticlesS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("spawn_portal"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SpawnPortalParticlesS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SpawnPortalParticlesS2CPayload(buf) }
            )
    }
}