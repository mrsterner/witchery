package dev.sterner.witchery.features.bark_belt

import dev.sterner.witchery.core.registry.WitcheryTags
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object BarkBeltHandler {

    const val TIME_TO_RECHARGE = 20 * 5


    /**
     * Modifies the incoming damage if the player has a bark belt equipped and charged. Will absorb some of the damage.
     */
    fun hurt(livingEntity: LivingEntity?, damageSource: DamageSource, damage: Float): Float {
        if (livingEntity is Player) {
            val data = BarkBeltPlayerAttachment.getData(livingEntity)

            if (damageSource.entity is LivingEntity) {
                val living = damageSource.entity as LivingEntity
                if (living.mainHandItem.`is`(WitcheryTags.WOODEN_WEAPONS)) {
                    BarkBeltPlayerAttachment.setData(livingEntity, data.copy(currentBark = 0))
                    return damage
                }
            }

            if (data.currentBark > 0) {
                val absorbedDamage = (damage / 2).coerceAtMost(data.currentBark.toFloat())
                val newCharge = (data.currentBark - absorbedDamage).toInt()
                val remainingDamage = damage - absorbedDamage

                BarkBeltPlayerAttachment.setData(livingEntity, data.copy(currentBark = newCharge, tickCounter = 0))

                return remainingDamage
            }
        }

        return damage
    }

    /**
     * Recharges the bark belt
     */
    fun tick(player: Player?) {
        if (player is ServerPlayer) {
            val data = BarkBeltPlayerAttachment.getData(player)
            val newTickCounter = data.tickCounter + 1

            if (newTickCounter >= TIME_TO_RECHARGE && data.currentBark < data.maxBark) {
                val newCharge = (data.currentBark + data.rechargeRate).coerceAtMost(data.maxBark)
                BarkBeltPlayerAttachment.setData(player, data.copy(currentBark = newCharge, tickCounter = 0))
            } else {
                BarkBeltPlayerAttachment.setData(player, data.copy(tickCounter = newTickCounter))
            }
        }
    }
}