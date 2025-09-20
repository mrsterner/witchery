package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncCurseS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object CursePlayerAttachment {

    @JvmStatic
    fun getData(player: Player): CursePlayerAttachment.Data {
        return player.getData(CURSE_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: CursePlayerAttachment.Data) {
        player.setData(CURSE_PLAYER_DATA_ATTACHMENT, data)
        CursePlayerAttachment.sync(player, data)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncCurseS2CPayload(player, data))
        }
    }

    data class PlayerCurseData(val curseId: ResourceLocation, var duration: Int, var catBoosted: Boolean) {

        companion object {
            val CODEC: Codec<PlayerCurseData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    ResourceLocation.CODEC.fieldOf("curseId").forGetter { it.curseId },
                    Codec.INT.fieldOf("duration").forGetter { it.duration },
                    Codec.BOOL.fieldOf("catBoosted").forGetter { it.catBoosted }

                ).apply(instance, ::PlayerCurseData)
            }
        }
    }

    data class Data(var playerCurseList: MutableList<PlayerCurseData> = mutableListOf()) {

        companion object {
            val ID: ResourceLocation = Witchery.id("player_curse_list")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(PlayerCurseData.CODEC).fieldOf("playerCurseList").forGetter { it.playerCurseList }
                ).apply(instance, ::Data)
            }
        }
    }
}