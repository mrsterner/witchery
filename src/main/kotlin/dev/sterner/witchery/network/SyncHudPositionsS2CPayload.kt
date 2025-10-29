package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.hud.HudPositionData
import dev.sterner.witchery.features.misc.HudPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class SyncHudPositionsS2CPayload(val hudPositions: HudPositionData) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(
        HudPositionData.STREAM_CODEC.decode(buf)
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    private fun write(buf: RegistryFriendlyByteBuf) {
        HudPositionData.STREAM_CODEC.encode(buf, hudPositions)
    }

    fun handleOnClient(ctx: IPayloadContext) {

        val client = Minecraft.getInstance()
        client.execute {
            val player = Minecraft.getInstance().player ?: return@execute
            HudPlayerAttachment.setHudPositions(player, hudPositions)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncHudPositionsS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_hud_positions"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncHudPositionsS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncHudPositionsS2CPayload(buf) }
            )
    }
}
