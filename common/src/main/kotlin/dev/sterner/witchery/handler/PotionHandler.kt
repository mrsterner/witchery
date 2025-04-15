package dev.sterner.witchery.handler

import dev.architectury.event.EventResult
import dev.architectury.event.events.common.EntityEvent
import dev.sterner.witchery.registry.WitcheryMobEffects
import net.minecraft.tags.EntityTypeTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity

object PotionHandler {

    fun registerEvents() {
        EntityEvent.LIVING_HURT.register(PotionHandler::poisonWeaponAttack)
    }

    private fun poisonWeaponAttack(
        livingEntity: LivingEntity,
        damageSource: DamageSource?,
        amount: Float
    ): EventResult {
        if (damageSource != null && damageSource.entity is LivingEntity) {
            val attacker = (damageSource.entity as LivingEntity)
            if (attacker.hasEffect(WitcheryMobEffects.POISON_WEAPON)) {
                val amp = attacker.getEffect(WitcheryMobEffects.POISON_WEAPON)?.amplifier ?: 0
                livingEntity.addEffect(MobEffectInstance(MobEffects.POISON, 20 * 10, amp))
            }
        }

        return EventResult.pass()
    }

    fun handleHurt(entity: LivingEntity, damageSource: DamageSource, remainingDamage: Float): Float {
        if (entity.type.`is`(EntityTypeTags.ARTHROPOD) && damageSource.entity is LivingEntity) {
            val attacker = (damageSource.entity as LivingEntity)
            if (attacker.hasEffect(WitcheryMobEffects.BANE_OF_ARTHROPODS_WEAPON)) {
                val amp = attacker.getEffect(WitcheryMobEffects.BANE_OF_ARTHROPODS_WEAPON)?.amplifier ?: 0
                return remainingDamage + (amp.toFloat() * 2)
            }
        }

        return remainingDamage
    }
}