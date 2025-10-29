package dev.sterner.witchery.features.death

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import dev.sterner.witchery.network.SyncDeathS2CPayload
import dev.sterner.witchery.network.SyncMiscS2CPayload
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object DeathPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.DEATH_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.DEATH_PLAYER_DATA_ATTACHMENT, data)
        sync(player, data)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, SyncDeathS2CPayload(player, data))
        }
    }

    data class Data(
        var isDeath: Boolean = false,
        var hasDeathNightVision: Boolean = false,
        var hasDeathFluidWalking: Boolean = false,
    ) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("isDeath").forGetter { it.isDeath },
                    Codec.BOOL.fieldOf("hasDeathNightVision").forGetter { it.hasDeathNightVision },
                    Codec.BOOL.fieldOf("hasDeathFluidWalking").forGetter { it.hasDeathFluidWalking },
                ).apply(instance, ::Data)
            }

            val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, Data> = StreamCodec.composite(
                StreamCodec.of({ buf, value -> buf.writeBoolean(value) }, { it.readBoolean() }),
                Data::isDeath,
                StreamCodec.of({ buf, value -> buf.writeBoolean(value) }, { it.readBoolean() }),
                Data::hasDeathNightVision,
                StreamCodec.of({ buf, value -> buf.writeBoolean(value) }, { it.readBoolean() }),
                Data::hasDeathFluidWalking,
                ::Data
            )

            val ID: ResourceLocation = Witchery.id("death_player_data")
        }
    }
}