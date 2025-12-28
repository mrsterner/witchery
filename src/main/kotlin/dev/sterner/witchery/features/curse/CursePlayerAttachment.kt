package dev.sterner.witchery.features.curse

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.network.SyncCurseS2CPayload
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object CursePlayerAttachment {

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.CURSE_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.CURSE_PLAYER_DATA_ATTACHMENT, data)
        sync(player, data)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, SyncCurseS2CPayload(player, data))
        }
    }

    data class PlayerCurseData(
        val curseId: ResourceLocation,
        var duration: Int,
        var catBoosted: Boolean,
        val witchPower: Int = 0, //Power level when curse was cast
        var failedRemovalAttempts: Int = 0
    ) {

        companion object {
            val CODEC: Codec<PlayerCurseData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    ResourceLocation.CODEC.fieldOf("curseId").forGetter { it.curseId },
                    Codec.INT.fieldOf("duration").forGetter { it.duration },
                    Codec.BOOL.fieldOf("catBoosted").forGetter { it.catBoosted },
                    Codec.INT.optionalFieldOf("witchPower", 0).forGetter { it.witchPower },
                    Codec.INT.optionalFieldOf("failedRemovalAttempts", 0).forGetter { it.failedRemovalAttempts }
                ).apply(instance, ::PlayerCurseData)
            }
        }
    }

    data class Data(var playerCurseList: MutableList<PlayerCurseData> = mutableListOf()) {

        companion object {
            val ID: ResourceLocation = Witchery.Companion.id("player_curse_list")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(PlayerCurseData.CODEC).fieldOf("playerCurseList").forGetter { it.playerCurseList }
                ).apply(instance, ::Data)
            }
        }
    }
}