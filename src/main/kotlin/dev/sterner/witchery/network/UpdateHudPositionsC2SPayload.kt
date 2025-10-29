package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.hud.HudPositionData
import dev.sterner.witchery.features.misc.HudPlayerAttachment
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class UpdateHudPositionsC2SPayload(val hudPositions: HudPositionData) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(
        HudPositionData.STREAM_CODEC.decode(friendlyByteBuf)
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        HudPositionData.STREAM_CODEC.encode(friendlyByteBuf, hudPositions)
    }

    fun handleOnServer(ctx: IPayloadContext) {
        ctx.player().server?.execute {
            HudPlayerAttachment.setHudPositions(ctx.player(), hudPositions)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<UpdateHudPositionsC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("update_hud_positions"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, UpdateHudPositionsC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> UpdateHudPositionsC2SPayload(buf) }
            )
    }
}