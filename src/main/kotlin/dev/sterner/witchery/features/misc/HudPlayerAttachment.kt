package dev.sterner.witchery.features.misc

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.client.hud.HudPositionData
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import dev.sterner.witchery.network.SyncHudPositionsS2CPayload
import dev.sterner.witchery.network.UpdateHudPositionsC2SPayload
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object HudPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.HUD_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.HUD_PLAYER_DATA_ATTACHMENT, data)
        if (player.level().isClientSide) {
            PacketDistributor.sendToServer(UpdateHudPositionsC2SPayload(data.hudPositions))
        }
    }

    fun sync(player: ServerPlayer, data: Data) {
        PacketDistributor.sendToPlayer(
            player,
            SyncHudPositionsS2CPayload(data.hudPositions)
        )
    }


    fun setHudPositions(player: Player, positions: HudPositionData) {
        val old = getData(player)
        setData(player, old.copy(hudPositions = positions))
    }

    data class Data(
        var hudPositions: HudPositionData = HudPositionData()
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    HudPositionData.CODEC.fieldOf("hudPositions").forGetter { it.hudPositions }
                ).apply(instance, ::Data)
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Data> = StreamCodec.composite(
                HudPositionData.STREAM_CODEC,
                Data::hudPositions,
                ::Data
            )

            val ID: ResourceLocation = Witchery.id("hud_player_data")
        }
    }
}