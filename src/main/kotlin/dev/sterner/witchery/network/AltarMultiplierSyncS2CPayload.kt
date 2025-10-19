package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryBlockEntityTypes
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class AltarMultiplierSyncS2CPayload(val pos: BlockPos, val multiplier: Double) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readBlockPos(), buf.readDouble())

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeBlockPos(pos)
        buf.writeDouble(multiplier)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        client.execute {
            val be = client.level?.getBlockEntity(pos, WitcheryBlockEntityTypes.ALTAR.get())
            if (be != null && be.isPresent)
                be.get().powerMultiplier = multiplier
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<AltarMultiplierSyncS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("altar_multiplier_sync"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, AltarMultiplierSyncS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> AltarMultiplierSyncS2CPayload(buf) }
            )
    }
}