package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.CursePlayerAttachment
import dev.sterner.witchery.registry.WitcheryParticleTypes
import net.minecraft.client.Minecraft
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

class SpawnBloodParticlesS2CPayload (val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, vec3: Vec3) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putDouble("x", vec3.x)
        putDouble("y", vec3.y)
        putDouble("z", vec3.z)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SpawnBloodParticlesS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")
        val x = payload.nbt.getDouble("x")
        val y = payload.nbt.getDouble("y")
        val z = payload.nbt.getDouble("z")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {

                for (i in 0..8) {
                    client.level!!.addAlwaysVisibleParticle(
                        WitcheryParticleTypes.SPLASHING_BLOOD.get(),
                        true,
                        x + 0.0 + Mth.nextDouble(client.level!!.random, -0.25, 0.25),
                        y + 0.0 + Mth.nextDouble(client.level!!.random, -0.25, 0.25),
                        z + 0.0 + Mth.nextDouble(client.level!!.random, -0.25, 0.25),
                        0.0, 0.0, 0.0
                    )
                }
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SpawnBloodParticlesS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("spawn_blood"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SpawnBloodParticlesS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SpawnBloodParticlesS2CPayload(buf!!) }
            )
    }
}