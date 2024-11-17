package dev.sterner.witchery.platform.transformation

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.event.EventResult
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data.BloodPoolHandler
import dev.sterner.witchery.payload.SyncBloodS2CPacket
import dev.sterner.witchery.payload.SyncOtherBloodS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
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

        if (livingEntity is Player) {
            if (VampirePlayerAttachment.getData(livingEntity).vampireLevel == 1 && newBloodPool == 900) {
                VampirePlayerAttachment.increaseVampireLevel(livingEntity)
            }
        }
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

    var ticker = 0

    fun tick(player: Player?) {

        if (player != null) {
            //println(getData(player).bloodPool)
        }

        if (player != null && player.level() is ServerLevel) {

            val bl = VampirePlayerAttachment.getData(player).vampireLevel > 0
            if (bl) {
                ticker++
                if (ticker > 10) {
                    ticker = 0
                    val entities = player.level().getEntities(player, player.boundingBox.inflate(5.0)).filter {
                        it.isAlive &&
                                it is LivingEntity &&
                                it != player &&
                                BloodPoolHandler.BLOOD_PAIR.contains(it.type)
                    }
                    for (entity in entities) {
                        sync(entity as LivingEntity, getData(entity))
                    }
                    sync(player, getData(player))
                }
            }
        }
    }

    fun setBloodOnAdded(entity: Entity?, level: Level?): EventResult? {
        if (entity is LivingEntity) {
            val data = getData(entity)
            val bloodJson = BloodPoolHandler.BLOOD_PAIR
            if (data.maxBlood == 0 && data.bloodPool == 0) {
                val entityType = entity.type
                val bloodValue = bloodJson[entityType]

                if (bloodValue != null) {
                    val maxBlood = bloodValue.bloodDrops * 300
                    val initializedData = data.copy(maxBlood = maxBlood, bloodPool = maxBlood)
                    setData(entity, initializedData)
                }
            }
        }

        return EventResult.pass()
    }

    fun tickBloodRegen(livingEntity: LivingEntity) {
        if (livingEntity is Player || livingEntity.level().isClientSide) {
            return
        }
        if (BloodPoolHandler.BLOOD_PAIR.contains(livingEntity.type)) {
            val bloodData = getData(livingEntity)
            if (bloodData.bloodPool < bloodData.maxBlood && bloodData.maxBlood > 0) {
                if (livingEntity.tickCount % 1000 == 0) {
                    val bloodPool = BloodPoolHandler.BLOOD_PAIR[livingEntity.type]
                    increaseBlood(livingEntity, (bloodPool!!.qualityBloodDrops + 1) * 2)
                }
            }
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