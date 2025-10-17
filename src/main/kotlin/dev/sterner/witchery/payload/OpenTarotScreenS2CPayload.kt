package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.tarot.TarotReadingScreen
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext

class OpenTarotScreenS2CPayload : CustomPacketPayload {

    constructor()
    constructor(buf: RegistryFriendlyByteBuf)

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    private fun write(buf: RegistryFriendlyByteBuf) {

    }

    fun handleOnClient(ctx: IPayloadContext) {
        val player = ctx.player() ?: return
        TarotReadingScreen.open(player)
    }

    companion object {
        val ID: CustomPacketPayload.Type<OpenTarotScreenS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("open_tarot_screen"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, OpenTarotScreenS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> OpenTarotScreenS2CPayload(buf) }
            )
    }
}