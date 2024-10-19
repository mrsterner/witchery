package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class AltarMultiplierSyncS2CPacket(val pos: BlockPos, val multiplier: Double) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readBlockPos(), buf.readDouble())

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(buf: RegistryFriendlyByteBuf?) {
        buf?.writeBlockPos(pos)
        buf?.writeDouble(multiplier)
    }

    fun handleS2C(payload: AltarMultiplierSyncS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()
        val pos = payload.pos
        client.execute {
            val be = client.level?.getBlockEntity(pos, WitcheryBlockEntityTypes.ALTAR.get())
            if (be != null && be.isPresent)
                be.get().powerMultiplier = payload.multiplier
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<AltarMultiplierSyncS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("altar_multiplier_sync"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, AltarMultiplierSyncS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> AltarMultiplierSyncS2CPacket(buf!!) }
            )
    }
}