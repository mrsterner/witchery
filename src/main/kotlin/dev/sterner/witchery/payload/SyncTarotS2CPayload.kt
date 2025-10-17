package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.TarotPlayerAttachment
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncTarotS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: TarotPlayerAttachment.Data) : this(
        CompoundTag().apply {
            putUUID("playerUUID", player.uuid)
            putIntArray("drawnCards", data.drawnCards.toIntArray())
            val reversedArray = data.reversedCards.map { if (it) 1 else 0 }.toIntArray()
            putIntArray("reversedCards", reversedArray)
            putLong("readingTimestamp", data.readingTimestamp)
        }
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        client.execute {
            val playerUUID = nbt.getUUID("playerUUID")
            val player = client.level?.getPlayerByUUID(playerUUID) ?: return@execute

            val drawnCards = nbt.getIntArray("drawnCards").toList()
            val reversedArray = nbt.getIntArray("reversedCards")
            val reversedCards = reversedArray.map { it == 1 }
            val timestamp = nbt.getLong("readingTimestamp")

            val data = TarotPlayerAttachment.Data(
                drawnCards = drawnCards,
                reversedCards = reversedCards,
                readingTimestamp = timestamp
            )

            player.setData(WitcheryDataAttachments.ARCANA_PLAYER_DATA_ATTACHMENT, data)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncTarotS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_tarot"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncTarotS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncTarotS2CPayload(buf) }
            )
    }
}