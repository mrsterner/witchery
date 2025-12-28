package dev.sterner.witchery.features.misc

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import dev.sterner.witchery.network.SyncMiscS2CPayload
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object MiscPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.MISC_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.MISC_PLAYER_DATA_ATTACHMENT, data)
        sync(player, data)
    }

    fun setWitcheryAligned(player: Player, aligned: Boolean) {
        val old = getData(player)
        setData(player, old.copy(isWitcheryAligned = aligned))
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, SyncMiscS2CPayload(player, data))
        }
    }

    data class Data(
        var isWitcheryAligned: Boolean = false,
        var hasDeathTeleport: Boolean = false
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("isWitcheryAligned").forGetter { it.isWitcheryAligned },
                    Codec.BOOL.fieldOf("hasDeathTeleport").forGetter { it.hasDeathTeleport },
                ).apply(instance, ::Data)
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Data> = StreamCodec.composite(
                StreamCodec.of({ buf, value -> buf.writeBoolean(value) }, { it.readBoolean() }),
                Data::isWitcheryAligned,
                StreamCodec.of({ buf, value -> buf.writeBoolean(value) }, { it.readBoolean() }),
                Data::hasDeathTeleport,
                ::Data
            )

            val ID: ResourceLocation = Witchery.id("misc_player_data")
        }
    }
}