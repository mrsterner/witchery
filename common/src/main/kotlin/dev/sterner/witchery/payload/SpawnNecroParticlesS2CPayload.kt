package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3

class SpawnNecroParticlesS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(vec3: Vec3) : this(CompoundTag().apply {
        putDouble("x", vec3.x)
        putDouble("y", vec3.y)
        putDouble("z", vec3.z)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SpawnNecroParticlesS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val x = payload.nbt.getDouble("x")
        val y = payload.nbt.getDouble("y")
        val z = payload.nbt.getDouble("z")


        client.execute {
            for (i in 0..32) {
                client.level!!.addAlwaysVisibleParticle(
                    ParticleTypes.SMOKE,
                    true,
                    x + 0.0 + Mth.nextDouble(client.level!!.random, -0.5, 0.5),
                    (y + 1.0) + Mth.nextDouble(client.level!!.random, -1.25, 1.25),
                    z + 0.0 + Mth.nextDouble(client.level!!.random, -0.5, 0.5),
                    0.0, 0.2, 0.0
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SpawnNecroParticlesS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("spawn_necro_smoke"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SpawnNecroParticlesS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SpawnNecroParticlesS2CPayload(buf) }
            )
    }
}