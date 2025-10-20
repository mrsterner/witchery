package dev.sterner.witchery.features.spirit_world

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.network.SyncManifestationS2CPayload
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object ManifestationPlayerAttachment {


    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.MANIFESTATION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.MANIFESTATION_PLAYER_DATA_ATTACHMENT, data)
        sync(player, data)
    }


    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncManifestationS2CPayload(player, data)
            )
        }
    }


    class Data(var hasRiteOfManifestation: Boolean = false, var manifestationTimer: Int = 0) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("hasRiteOfManifestation").forGetter { it.hasRiteOfManifestation },
                    Codec.INT.fieldOf("manifestationTimer").forGetter { it.manifestationTimer }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("manifestation_player_data")
        }
    }
}