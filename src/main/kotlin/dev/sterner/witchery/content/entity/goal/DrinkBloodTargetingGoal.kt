package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.core.data.BloodPoolReloadListener
import dev.sterner.witchery.core.data_attachment.BloodPoolLivingEntityAttachment
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.player.Player

class DrinkBloodTargetingGoal<T : LivingEntity?>(mob: Mob, targetClass: Class<T>, checkVisibility: Boolean) :
    NearestAttackableTargetGoal<T>(mob, targetClass, checkVisibility) {

    override fun canUse(): Boolean {
        val bloodPool = BloodPoolLivingEntityAttachment.getData(mob)

        if (bloodPool.bloodPool == bloodPool.maxBlood) {
            return false
        }

        return super.canUse()
    }

    override fun findTarget() {
        if (targetType != Player::class.java && targetType != ServerPlayer::class.java) {
            val entities = mob.level().getEntitiesOfClass(targetType, getTargetSearchArea(this.followDistance)) {
                BloodPoolReloadListener.BLOOD_PAIR.contains(it.type)
            }
            target = mob.level().getNearestEntity(entities, targetConditions, mob, mob.x, mob.eyeY, mob.z)

        }
        target = mob.level().getNearestPlayer(targetConditions, mob, mob.x, mob.eyeY, mob.z)
    }
}