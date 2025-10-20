package dev.sterner.witchery.features.blood

import dev.sterner.witchery.core.data.BloodPoolReloadListener
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.affliction.vampire.VampireLeveling
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

object BloodPoolHandler {

    private var ticker = 0


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
                        BloodPoolLivingEntityAttachment.sync(
                            entity as LivingEntity,
                            BloodPoolLivingEntityAttachment.getData(entity)
                        )
                    }
                    BloodPoolLivingEntityAttachment.sync(player, BloodPoolLivingEntityAttachment.getData(player))
                }
            }
        }
    }

    fun setBloodOnAdded(entity: Entity?, level: Level?) {
        if (entity is LivingEntity) {
            val data = BloodPoolLivingEntityAttachment.getData(entity)
            val bloodJson = BloodPoolReloadListener.BLOOD_PAIR
            if (data.maxBlood == 0 && data.bloodPool == 0) {
                val entityType = entity.type
                val bloodValue = bloodJson[entityType]

                if (bloodValue != null) {
                    val maxBlood = bloodValue.bloodDrops * 300
                    val initializedData = data.copy(maxBlood = maxBlood, bloodPool = maxBlood)
                    BloodPoolLivingEntityAttachment.setData(entity, initializedData)
                }
            }
        }
    }

    fun tickBloodRegen(livingEntity: LivingEntity) {
        if (livingEntity is Player || livingEntity.level().isClientSide) {
            return
        }
        if (BloodPoolReloadListener.BLOOD_PAIR.contains(livingEntity.type)) {
            val bloodData = BloodPoolLivingEntityAttachment.getData(livingEntity)
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
        val data = BloodPoolLivingEntityAttachment.getData(livingEntity)
        val maxBlood = data.maxBlood
        val newBloodPool = (data.bloodPool + amount).coerceAtMost(maxBlood)
        BloodPoolLivingEntityAttachment.setData(livingEntity, data.copy(bloodPool = newBloodPool))

        if (livingEntity is ServerPlayer) {
            if (AfflictionPlayerAttachment.getData(livingEntity).getVampireLevel() == 1 && newBloodPool == 900) {
                VampireLeveling.increaseVampireLevel(livingEntity)
            }
        }
    }

    @JvmStatic
    fun decreaseBlood(livingEntity: LivingEntity, amount: Int) {
        val data = BloodPoolLivingEntityAttachment.getData(livingEntity)
        val newBloodPool = (data.bloodPool - amount).coerceAtLeast(0)
        BloodPoolLivingEntityAttachment.setData(livingEntity, data.copy(bloodPool = newBloodPool))
    }
}