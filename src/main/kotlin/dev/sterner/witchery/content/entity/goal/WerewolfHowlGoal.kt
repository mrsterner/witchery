package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.content.entity.WerewolfEntity
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.Goal
import java.util.EnumSet


class WerewolfHowlGoal(private val werewolf: WerewolfEntity) : Goal() {

    private var howlTimer = 0

    init {
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK))
    }

    override fun canUse(): Boolean {
        return werewolf.target != null &&
                werewolf.canHowl() &&
                werewolf.random.nextFloat() < 0.02f
    }

    override fun start() {
        howlTimer = 40
        werewolf.setHowlCooldown(800)
        werewolf.navigation.stop()

        werewolf.setHowling(true)
    }

    override fun stop() {
        howlTimer = 0

        werewolf.setHowling(false)
    }

    override fun canContinueToUse(): Boolean {
        return howlTimer > 0
    }

    override fun tick() {
        howlTimer--

        if (howlTimer <= 0) {
            werewolf.setHowling(false)
            return
        }

        if (howlTimer == 35) {
            werewolf.level().playSound(
                null,
                werewolf.blockPosition(),
                SoundEvents.WOLF_HOWL,
                werewolf.soundSource,
                2.0f,
                0.8f
            )
        }

        if (howlTimer == 20) {
            val radius = 16.0
            val nearbyEntities = werewolf.level().getEntitiesOfClass(
                LivingEntity::class.java,
                werewolf.boundingBox.inflate(radius)
            ) { entity ->
                entity != werewolf && entity.isAlive && werewolf.canAttack(entity)
            }

            nearbyEntities.forEach { entity ->
                entity.addEffect(
                    MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        20 * 5,
                        2
                    )
                )

                if (entity is Mob) {
                    entity.target = null
                }
            }
        }
    }
}