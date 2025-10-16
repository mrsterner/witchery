package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.TarotPlayerAttachment
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.handling.IPayloadContext

class LockInTarotCardsC2SPayload(val cardNumbers: List<Int>, val reversedStates: List<Boolean>) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(
        buf.readList { it.readInt() },
        buf.readList { it.readBoolean() }
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    private fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeCollection(cardNumbers) { buffer, num -> buffer.writeInt(num) }
        buf.writeCollection(reversedStates) { buffer, reversed -> buffer.writeBoolean(reversed) }
    }

    fun handleOnServer(ctx: IPayloadContext) {
        val player = ctx.player() as? ServerPlayer ?: return

        val data = TarotPlayerAttachment.getData(player)
        data.drawnCards = cardNumbers
        data.reversedCards = reversedStates
        data.cardEffectsActive = true
        data.readingTimestamp = player.serverLevel().gameTime

        TarotPlayerAttachment.setData(player, data)
    }

    companion object {
        val ID: CustomPacketPayload.Type<LockInTarotCardsC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("lock_in_tarot_cards"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, LockInTarotCardsC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> LockInTarotCardsC2SPayload(buf) }
            )
    }
}