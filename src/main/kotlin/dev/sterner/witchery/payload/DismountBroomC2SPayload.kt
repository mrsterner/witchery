package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.handling.IPayloadContext

class DismountBroomC2SPayload() : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this()

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(buf: RegistryFriendlyByteBuf) {

    }

    fun handleOnServer(ctx: IPayloadContext) {
        val player: Player? = ctx.player()
        if (player != null) {
            player.vehicle?.stopRiding()
            player.stopRiding()
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<DismountBroomC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("dismount_broom"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, DismountBroomC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> DismountBroomC2SPayload(buf) }
            )
    }
}