package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.data.BloodPoolReloadListener
import dev.sterner.witchery.handler.affliction.VampireLeveling
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment.getData
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment.setData
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment.sync
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

object BloodPoolHandler {

    private var ticker = 0

    fun registerEvents() {
        TickEvent.PLAYER_PRE.register(BloodPoolHandler::tick)
        EntityEvent.ADD.register(BloodPoolHandler::setBloodOnAdded)
    }

    fun tick(player: Player?) {

        if (player != null && player.level() is ServerLevel) {

            val bl = AfflictionPlayerAttachment.getData(player).getVampireLevel() > 0
            if (bl) {
                ticker++
                if (ticker > 10) {
                    ticker = 0
                    val entities = player.level().getEntities(player, player.boundingBox.inflate(5.0)).filter {
                        it.isAlive &&
                                it is LivingEntity &&
                                it != player &&
                                BloodPoolReloadListener.BLOOD_PAIR.contains(it.type)
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
            val bloodJson = BloodPoolReloadListener.BLOOD_PAIR
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
        if (BloodPoolReloadListener.BLOOD_PAIR.contains(livingEntity.type)) {
            val bloodData = getData(livingEntity)
            if (bloodData.bloodPool < bloodData.maxBlood && bloodData.maxBlood > 0) {
                if (livingEntity.tickCount % 1000 == 0) {
                    val bloodPool = BloodPoolReloadListener.BLOOD_PAIR[livingEntity.type]
                    increaseBlood(livingEntity, (bloodPool!!.qualityBloodDrops + 1) * 2)
                }
            }
        }
    }

    @JvmStatic
    fun increaseBlood(livingEntity: LivingEntity, amount: Int) {
        val data = getData(livingEntity)
        val maxBlood = data.maxBlood
        val newBloodPool = (data.bloodPool + amount).coerceAtMost(maxBlood)
        setData(livingEntity, data.copy(bloodPool = newBloodPool))

        if (livingEntity is ServerPlayer) {
            if (AfflictionPlayerAttachment.getData(livingEntity).getVampireLevel() == 1 && newBloodPool == 900) {
                VampireLeveling.increaseVampireLevel(livingEntity)
            }
        }
    }

    @JvmStatic
    fun decreaseBlood(livingEntity: LivingEntity, amount: Int) {
        val data = getData(livingEntity)
        val newBloodPool = (data.bloodPool - amount).coerceAtLeast(0)
        setData(livingEntity, data.copy(bloodPool = newBloodPool))
    }
}