package dev.sterner.witchery.core.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.network.SyncTarotS2CPayload
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import dev.sterner.witchery.core.registry.WitcheryTarotEffects
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object TarotPlayerAttachment {

    const val THREE_DAYS = 24000L * 3

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.ARCANA_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        val oldData = getData(player)
        player.setData(WitcheryDataAttachments.ARCANA_PLAYER_DATA_ATTACHMENT, data)
        sync(player, data)

        if (player.level() is ServerLevel) {
            handleCardChanges(player, oldData, data)
        }
    }

    private fun handleCardChanges(player: Player, oldData: Data, newData: Data) {
        for (i in oldData.drawnCards.indices) {
            val cardNumber = oldData.drawnCards[i]
            val isReversed = oldData.reversedCards.getOrNull(i) ?: false

            if (!newData.drawnCards.contains(cardNumber)) {
                val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
                effect?.onRemoved(player, isReversed)
            }
        }

        for (i in newData.drawnCards.indices) {
            val cardNumber = newData.drawnCards[i]
            val isReversed = newData.reversedCards.getOrNull(i) ?: false

            if (!oldData.drawnCards.contains(cardNumber)) {
                val effect = WitcheryTarotEffects.getByCardNumber(cardNumber)
                effect?.onAdded(player, isReversed)
            }
        }
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncTarotS2CPayload(player, data)
            )
        }
    }

    fun serverTick(player: Player) {
        if (player.level() is ServerLevel) {
            val level = player.level()
            val data = getData(player)

            if (data.drawnCards.isNotEmpty() && level.gameTime - data.readingTimestamp >= THREE_DAYS) {
                val newData = Data(
                    drawnCards = emptyList(),
                    reversedCards = emptyList(),
                    readingTimestamp = 0L
                )

                setData(player, newData)

                player.displayClientMessage(
                    Component.literal("Your tarot reading has faded with time.")
                        .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC),
                    true
                )
            }
        }
    }


    data class Data(
        var drawnCards: List<Int> = emptyList(),
        var reversedCards: List<Boolean> = emptyList(),
        var readingTimestamp: Long = 0L
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.listOf().fieldOf("drawnCards").forGetter { it.drawnCards },
                    Codec.BOOL.listOf().fieldOf("reversedCards").forGetter { it.reversedCards },
                    Codec.LONG.fieldOf("readingTimestamp").forGetter { it.readingTimestamp }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("tarot_player_data")
        }
    }
}