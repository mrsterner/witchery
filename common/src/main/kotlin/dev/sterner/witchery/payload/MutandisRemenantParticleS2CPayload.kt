package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.util.Mth

class MutandisRemenantParticleS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(pos: BlockPos) : this(
        CompoundTag().apply {
            putInt("x", pos.x)
            putInt("y", pos.y)
            putInt("z", pos.z)
        }
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: MutandisRemenantParticleS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()
        val pos = BlockPos(payload.nbt.getInt("x"), payload.nbt.getInt("y"), payload.nbt.getInt("z"))
        client.execute {
            client.level!!.addAlwaysVisibleParticle(
                ParticleTypes.ENCHANTED_HIT,
                true,
                pos.x + 0.5 + Mth.nextDouble(client.level!!.random, -0.55, 0.55),
                pos.y + 0.5 + Mth.nextDouble(client.level!!.random, -0.55, 0.5),
                pos.z + 0.5 + Mth.nextDouble(client.level!!.random, -0.55, 0.55),
                0.0, 0.05, 0.0
            )
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<MutandisRemenantParticleS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("mutandis_particle"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, MutandisRemenantParticleS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> MutandisRemenantParticleS2CPayload(buf) }
            )
    }
}