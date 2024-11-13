package dev.sterner.witchery.platform.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncBloodS2CPacket
import dev.sterner.witchery.payload.SyncVampireS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object BloodPoolLivingEntityAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(livingEntity: LivingEntity): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(livingEntity: LivingEntity, data: Data) {
        throw AssertionError()
    }

    @JvmStatic
    fun increaseBlood(player: Player, amount: Int) {
        val data = getData(player)
        val maxBlood = data.maxBlood
        val newBloodPool = (data.bloodPool + amount).coerceAtMost(maxBlood)
        setData(player, data.copy(bloodPool = newBloodPool))
    }

    @JvmStatic
    fun decreaseBlood(player: Player, amount: Int) {
        val data = getData(player)
        val newBloodPool = (data.bloodPool - amount).coerceAtLeast(0)
        setData(player, data.copy(bloodPool = newBloodPool))
    }

    fun sync(livingEntity: LivingEntity, data: Data) {
        if (livingEntity.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(livingEntity.level(), livingEntity.blockPosition(), SyncBloodS2CPacket(livingEntity, data))
        }
    }

    fun tick(player: Player?) {
        if (player != null) {

        }
    }

    //300 blood = 1 full blood drop
    class Data(val maxBlood: Int = 0, val bloodPool: Int = 0) {

        fun copy(maxBlood: Int = this.maxBlood, bloodPool: Int = this.bloodPool): Data {
            return Data(maxBlood, bloodPool)
        }

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("maxBlood").forGetter { it.maxBlood },
                    Codec.INT.fieldOf("bloodPool").forGetter { it.bloodPool },
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("blood_pool_entity_data")
        }
    }
}