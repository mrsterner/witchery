package dev.sterner.witchery.features.nightmare

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncNightmareS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor
import java.util.Optional
import java.util.UUID

object NightmarePlayerAttachment {
    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.NIGHTMARE_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.NIGHTMARE_PLAYER_DATA_ATTACHMENT, data)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncNightmareS2CPayload(player, data)
            )
        }
    }

    class Data(var hasNightmare: Boolean = false, var nightmareUUID: Optional<UUID> = Optional.empty()) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("hasNightmare").forGetter { it.hasNightmare },
                    Codecs.UUID.optionalFieldOf("nightmareUUID").forGetter { it.nightmareUUID }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.Companion.id("nightmare_player_data")
        }
    }
}