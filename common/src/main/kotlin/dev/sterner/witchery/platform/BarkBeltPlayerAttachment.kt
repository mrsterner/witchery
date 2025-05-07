package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncBarkS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object BarkBeltPlayerAttachment {


    @ExpectPlatform
    @JvmStatic
    fun getData(player: Player): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(player: Player, data: Data) {
        throw AssertionError()
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncBarkS2CPacket(player, data))
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