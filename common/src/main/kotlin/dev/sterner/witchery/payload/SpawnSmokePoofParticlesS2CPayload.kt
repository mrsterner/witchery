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

class SpawnSmokePoofParticlesS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    constructor(vec3: Vec3) : this(CompoundTag().apply {
        putDouble("x", vec3.x)
        putDouble("y", vec3.y)
        putDouble("z", vec3.z)
    })

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SpawnSmokePoofParticlesS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        client.execute {
            val pos = Vec3(payload.nbt.getDouble("x"), payload.nbt.getDouble("y"), payload.nbt.getDouble("z"))
            for (i in 0..16) {
                client.level!!.addAlwaysVisibleParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    true,
                    pos.x + 0.0 + Mth.nextDouble(client.level!!.random, -0.5, 0.5),
                    (pos.y + 1.0) + Mth.nextDouble(client.level!!.random, -0.25, 0.25),
                    pos.z + 0.0 + Mth.nextDouble(client.level!!.random, -0.5, 0.5),
                    0.0, 0.05, 0.0
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SpawnSmokePoofParticlesS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("spawn_smoke_poof"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SpawnSmokePoofParticlesS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SpawnSmokePoofParticlesS2CPayload(buf) }
            )
    }
}