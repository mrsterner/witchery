package dev.sterner.witchery.payload

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

    fun handleOnClient() {
        val client = Minecraft.getInstance()

        val x = nbt.getDouble("x")
        val y = nbt.getDouble("y")
        val z = nbt.getDouble("z")


        client.execute {
            client.level!!.addAlwaysVisibleParticle(
                ParticleTypes.SOUL,
                true,
                x + 0.0 + Mth.nextDouble(client.level!!.random, -0.5, 0.5),
                (y + 0.0) + Mth.nextDouble(client.level!!.random, -0.5, 0.5),
                z + 0.0 + Mth.nextDouble(client.level!!.random, -0.5, 0.5),
                0.0, 0.01, 0.0
            )
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