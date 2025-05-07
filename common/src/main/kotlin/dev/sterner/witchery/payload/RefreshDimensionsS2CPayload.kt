package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class RefreshDimensionsS2CPayload() : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this()

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {

    }

    fun handleS2C(payload: RefreshDimensionsS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        client.execute {
            context.player.refreshDimensions()
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<RefreshDimensionsS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("refresh_dimensions"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, RefreshDimensionsS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> RefreshDimensionsS2CPayload(buf) }
            )
    }
}