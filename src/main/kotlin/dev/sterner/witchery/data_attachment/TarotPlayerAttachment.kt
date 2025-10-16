package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncTarotS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object TarotPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.ARCANA_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.ARCANA_PLAYER_DATA_ATTACHMENT, data)
        sync(player, data)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncTarotS2CPayload(player, data)
            )
        }
    }

    class Data(
        var drawnCards: List<Int> = emptyList(),
        var reversedCards: List<Boolean> = emptyList(),
        var cardEffectsActive: Boolean = false,
        var readingTimestamp: Long = 0L
    ) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.listOf().fieldOf("drawnCards").forGetter { it.drawnCards },
                    Codec.BOOL.listOf().fieldOf("reversedCards").forGetter { it.reversedCards },
                    Codec.BOOL.fieldOf("cardEffectsActive").forGetter { it.cardEffectsActive },
                    Codec.LONG.fieldOf("readingTimestamp").forGetter { it.readingTimestamp }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("tarot_player_data")
        }
    }
}