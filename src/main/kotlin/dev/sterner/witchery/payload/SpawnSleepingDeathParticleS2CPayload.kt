package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class SpawnSleepingDeathParticleS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(x: Double, y: Double, z: Double) : this(CompoundTag().apply {
        putDouble("x", x)
        putDouble("y", y)
        putDouble("z", z)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()

        val x = nbt.getDouble("x")
        val y = nbt.getDouble("y")
        val z = nbt.getDouble("z")


        client.execute {
            for (i in 0..19) {
                val d = client.level!!.random.nextGaussian() * 0.02
                val e = client.level!!.random.nextGaussian() * 0.02
                val f = client.level!!.random.nextGaussian() * 0.02
                client.level!!.addParticle(
                    ParticleTypes.POOF,
                    x,
                    y,
                    z, d, e, f
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SpawnSleepingDeathParticleS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("spawn_death_poof"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SpawnSleepingDeathParticleS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SpawnSleepingDeathParticleS2CPayload(buf) }
            )
    }
}