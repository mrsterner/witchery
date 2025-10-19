package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class RefreshDimensionsS2CPayload() : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this()

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {

    }

    fun handleOnClient(ctx: IPayloadContext) {
        val client = Minecraft.getInstance()

        client.execute {
            ctx.player().refreshDimensions()
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