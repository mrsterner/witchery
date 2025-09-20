package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncBarkS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object BarkBeltPlayerAttachment {

    @JvmStatic
    fun getData(player: Player): BarkBeltPlayerAttachment.Data {
        return player.getData(BARK_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: BarkBeltPlayerAttachment.Data) {
        player.setData(BARK_PLAYER_DATA_ATTACHMENT, data)
        BarkBeltPlayerAttachment.sync(player, data)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncBarkS2CPayload(player, data))
        }
    }

    data class Data(
        val currentBark: Int = 0,
        val maxBark: Int = 0,
        val rechargeRate: Int = 1,
        val tickCounter: Int = 0
    ) {

        companion object {
            val ID: ResourceLocation = Witchery.id("player_bark_belt")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("currentBark").forGetter { it.currentBark },
                    Codec.INT.fieldOf("maxBark").forGetter { it.maxBark },
                    Codec.INT.fieldOf("rechargeRate").forGetter { it.rechargeRate },
                    Codec.INT.fieldOf("tickCounter").forGetter { it.tickCounter }
                ).apply(instance, ::Data)
            }
        }
    }
}