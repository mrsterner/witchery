package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.possession.PossessionManager
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.handling.IPayloadContext

class StopPossessionC2SPayload : CustomPacketPayload {

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun handleOnServer(context: IPayloadContext) {
        val player = context.player()
        if (player !is ServerPlayer) return

        context.enqueueWork {
            PossessionManager.stopPossessing(player)
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<StopPossessionC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("stop_possession"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, StopPossessionC2SPayload> =
            StreamCodec.unit(StopPossessionC2SPayload())
    }
}