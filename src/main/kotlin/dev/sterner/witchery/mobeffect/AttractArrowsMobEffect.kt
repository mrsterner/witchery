package dev.sterner.witchery.mobeffect

import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.AbstractArrow
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * A mob effect that attracts arrows towards the entity with the effect.
 * The opposite of ReflectArrowsMobEffect.
 */
class AttractArrowsMobEffect(category: MobEffectCategory, color: Int) :
    MobEffect(category, color) {

    override fun applyEffectTick(entity: LivingEntity, amplifier: Int): Boolean {
        if (!entity.level().isClientSide) {
            val radius = 5.0 + (amplifier * 2)
            val level = entity.level()

            val nearbyArrows = level.getEntitiesOfClass(
                AbstractArrow::class.java,
                entity.boundingBox.inflate(radius)
            )

            for (arrow in nearbyArrows) {
                if (arrow.owner != entity && !arrow.onGround()) {
                    val direction = entity.position().subtract(arrow.position()).normalize()

                    val dx = direction.x
                    val dy = direction.y
                    val dz = direction.z

                    val yaw = Math.toDegrees(atan2(-dx, dz)).toFloat()
                    val pitch = Math.toDegrees(-atan2(dy, sqrt(dx * dx + dz * dz))).toFloat()

                    val speedMultiplier = 1.0f + (amplifier * 0.2f)
                    val speed = (arrow.deltaMovement.length().toFloat() * speedMultiplier).coerceAtMost(3.0f)

                    arrow.shootFromRotation(entity, pitch, yaw, 0.0f, speed, 0.1f)
                    arrow.hasImpulse = true

                    level.playSound(
                        null,
                        arrow.blockX.toDouble(),
                        arrow.blockY.toDouble(),
                        arrow.blockZ.toDouble(),
                        SoundEvents.FISHING_BOBBER_RETRIEVE,
                        SoundSource.PLAYERS,
                        0.7f,
                        1.3f + (level.random.nextFloat() * 0.4f)
                    )

                    if (level.isClientSide) {
                        (0..3).forEach { i ->
                            level.addParticle(
                                net.minecraft.core.particles.ParticleTypes.CRIT,
                                arrow.x, arrow.y, arrow.z,
                                0.0, 0.0, 0.0
                            )
                        }
                    }
                }
            }
        }
        return true
    }

    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        return true
    }
}