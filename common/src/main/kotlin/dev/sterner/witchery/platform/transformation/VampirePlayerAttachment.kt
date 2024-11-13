package dev.sterner.witchery.platform.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncVampireS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object VampirePlayerAttachment {

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

    @JvmStatic
    fun increaseBlood(player: Player, amount: Int) {
        val data = getData(player)
        val maxBlood = getMaxBlood(player)
        val newBloodPool = (data.bloodPool + amount).coerceAtMost(maxBlood)
        setData(player, data.copy(bloodPool = newBloodPool))
    }

    @JvmStatic
    fun decreaseBlood(player: Player, amount: Int) {
        val data = getData(player)
        val newBloodPool = (data.bloodPool - amount).coerceAtLeast(0)
        setData(player, data.copy(bloodPool = newBloodPool))
    }

    @JvmStatic
    fun getMaxBlood(player: Player): Int {
        val data = getData(player)
        return when (data.vampireLevel) {
            1 -> 900
            2 -> 1200
            3 -> 1500
            4 -> 1500
            5 -> 1800
            6 -> 2100
            7 -> 2400
            8 -> 2700
            9 -> 3000
            10 -> 3600
            else -> 0
        }
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncVampireS2CPacket(player, data))
        }
    }

    fun tick(player: Player?) {
        if (player != null) {

        }
    }

    //300 blood = 1 full blood drop
    class Data(val vampireLevel: Int = 0, val bloodPool: Int = 0) {

        fun copy(vampireLevel: Int = this.vampireLevel, bloodPool: Int = this.bloodPool): Data {
            return Data(vampireLevel, bloodPool)
        }

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("vampireLevel").forGetter { it.vampireLevel },
                    Codec.INT.fieldOf("bloodPool").forGetter { it.bloodPool },
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("vampire_player_data")
        }
    }
}