package dev.sterner.witchery.platform.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.event.EventResult
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncBloodS2CPacket
import dev.sterner.witchery.payload.SyncOtherBloodS2CPacket
import dev.sterner.witchery.payload.SyncVampireS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*

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
    fun increaseBlood(livingEntity: LivingEntity, amount: Int) {
        val data = getData(livingEntity)
        val maxBlood = data.maxBlood
        val newBloodPool = (data.bloodPool + amount).coerceAtMost(maxBlood)
        setData(livingEntity, data.copy(bloodPool = newBloodPool))
    }

    @JvmStatic
    fun decreaseBlood(livingEntity: LivingEntity, amount: Int) {
        val data = getData(livingEntity)
        val newBloodPool = (data.bloodPool - amount).coerceAtLeast(0)
        setData(livingEntity, data.copy(bloodPool = newBloodPool))
    }

    fun sync(livingEntity: LivingEntity, data: Data) {
        if (livingEntity.level() is ServerLevel) {
            if (livingEntity is Player) {
                WitcheryPayloads.sendToPlayers(livingEntity.level(), SyncBloodS2CPacket(livingEntity, data))
            } else {
                WitcheryPayloads.sendToPlayers(livingEntity.level(), SyncOtherBloodS2CPacket(livingEntity, data))
            }
        }
    }

    fun tick(player: Player?) {
        if (player != null && player.level() is ServerLevel) {
            val serverLevel = player.level() as ServerLevel

            val nearbyEntities = serverLevel.getEntities(player, player.boundingBox.inflate(5.0)) { it is LivingEntity && it != player}

            for (entity in nearbyEntities) {
                val uuid = entity.uuid
                if (uuid !in ClientBloodSyncTracker.syncedEntities) {
                    val bloodData = getData(entity as LivingEntity)
                    WitcheryPayloads.sendToPlayers(player.level(), SyncOtherBloodS2CPacket(entity, bloodData))
                    ClientBloodSyncTracker.syncedEntities.add(uuid)
                }
            }
        }
    }



    fun addEntity(entity: Entity?, level: Level?): EventResult? {
        return EventResult.pass()
    }

    //300 blood = 1 full blood drop
    class Data(val maxBlood: Int, val bloodPool: Int) {

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

    object ClientBloodSyncTracker {
        val syncedEntities = mutableSetOf<UUID>()
    }
}