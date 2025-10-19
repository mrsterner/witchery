package dev.sterner.witchery.content.mob_effect

import dev.sterner.witchery.core.registry.WitcheryMobEffects
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.npc.Villager

// Disease Rotten Flesh	4	2000	Negative
class DiseaseMobEffect(category: MobEffectCategory, color: Int) :
    MobEffect(category, color) {

    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int): Boolean {
        val level = livingEntity.level()
        if (!level.isClientSide) {
            val box = livingEntity.boundingBox.inflate(1.0)
            livingEntity.level().getEntitiesOfClass(LivingEntity::class.java, box).forEach { targetEntity ->
                if (!targetEntity.hasEffect(WitcheryMobEffects.DISEASE)) {
                    targetEntity.addEffect(MobEffectInstance(WitcheryMobEffects.DISEASE, 20 * 30, amplifier))
                }
            }

            livingEntity.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 20 * 10, amplifier))
            livingEntity.addEffect(MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20 * 10, amplifier))

            if (level.random.nextInt(10) == 0) {
                val damage = 1.0f

                if (livingEntity is Villager && livingEntity.health <= damage) {
                    val zombieVillager = livingEntity.convertTo(EntityType.ZOMBIE_VILLAGER, false)
                    if (zombieVillager != null) {
                        zombieVillager.setCanPickUpLoot(false)
                        zombieVillager.heal(10f)
                        level.playSound(
                            null,
                            zombieVillager.blockX.toDouble(),
                            zombieVillager.blockY.toDouble(),
                            zombieVillager.blockZ.toDouble(),
                            SoundEvents.ZOMBIE_INFECT,
                            SoundSource.PLAYERS,
                            1.0f,
                            1.0f
                        )
                    }
                } else {
                    val harm = livingEntity.isInvertedHealAndHarm
                    if (harm) {
                        livingEntity.heal(damage)
                    } else {
                        livingEntity.hurt(level.damageSources().starve(), damage)
                    }
                }
            }
        }

        return super.applyEffectTick(livingEntity, amplifier)
    }

    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        return duration % 20 == 0
    }
}