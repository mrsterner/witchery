package dev.sterner.witchery.mobeffect

import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.AbstractArrow
import kotlin.math.atan2
import kotlin.math.sqrt

//Reflect Arrows	Cobweb	2	250	Positive
class ReflectArrowsMobEffect(category: MobEffectCategory, color: Int) :
    MobEffect(category, color) {

    override fun applyEffectTick(entity: LivingEntity, amplifier: Int): Boolean {
        if (!entity.level().isClientSide) {
            val radius = 1.0 + (amplifier * 2)
            val level = entity.level()

            val nearbyArrows = level.getEntitiesOfClass(
                AbstractArrow::class.java,
                entity.boundingBox.inflate(radius)
            )

            for (arrow in nearbyArrows) {
                val shooter = arrow.owner
                if (shooter is LivingEntity && shooter != entity && !arrow.isCritArrow) {
                    val direction = shooter.position().subtract(arrow.position()).normalize()

                    val dx = direction.x
                    val dy = direction.y
                    val dz = direction.z

                    val yaw = Math.toDegrees(atan2(-dx, dz)).toFloat()
                    val pitch = Math.toDegrees(-atan2(dy, sqrt(dx * dx + dz * dz))).toFloat()

                    val speed = arrow.deltaMovement.length().toFloat() * 0.9f
                    arrow.shootFromRotation(entity, pitch, yaw, 0.0f, speed, 0.1f)

                    arrow.setOwner(entity)
                    arrow.hasImpulse = true

                    level.playSound(null, arrow.blockX.toDouble(), arrow.blockY.toDouble(), arrow.blockZ.toDouble(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0f, 1.0f)
                }
            }
        }
        return true
    }

    override fun shouldApplyEffectTickThisTick(duration: Int, amplifier: Int): Boolean {
        return true
    }
}
