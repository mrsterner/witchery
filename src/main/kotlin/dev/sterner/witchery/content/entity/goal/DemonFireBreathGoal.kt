package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.entity.DemonEntity
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.phys.Vec3
import java.util.EnumSet
import kotlin.math.cos

class DemonFireBreathGoal(
    private val demon: DemonEntity,
    private val cooldownMin: Int = 100,
    private val cooldownMax: Int = 140,
    private val breathDuration: Int = 40,
    private val range: Double = 8.0,
    private val coneAngle: Double = 30.0
) : Goal() {

    private var breathingTicks = 0
    private var cooldownTicks = 0

    init {
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK))
    }

    override fun canUse(): Boolean {
        val target = demon.target ?: return false

        if (cooldownTicks > 0) return false
        if (!demon.hasLineOfSight(target)) return false
        if (demon.distanceToSqr(target) > range * range) return false

        return true
    }

    override fun canContinueToUse(): Boolean {
        return breathingTicks > 0
    }

    override fun start() {
        breathingTicks = breathDuration
        demon.setBreathing(true)
        demon.navigation.stop()
    }

    override fun stop() {
        breathingTicks = 0
        cooldownTicks = demon.random.nextInt(cooldownMin, cooldownMax)
        demon.setBreathing(false)
    }

    override fun tick() {
        val target = demon.target ?: return

        if (cooldownTicks > 0) {
            cooldownTicks--
            return
        }

        if (breathingTicks > 0) {
            breathingTicks--

            demon.lookControl.setLookAt(target, 30f, 30f)

            if (breathingTicks % 2 == 0) {
                spawnFireBreath(target)
            }

            if (breathingTicks % 10 == 0) {
                demon.level().playSound(
                    null,
                    demon.blockPosition(),
                    SoundEvents.BLAZE_SHOOT,
                    SoundSource.HOSTILE,
                    1.0f,
                    0.8f + demon.random.nextFloat() * 0.4f
                )
            }
        }
    }

    private fun spawnFireBreath(target: LivingEntity) {
        val level = demon.level()
        if (level !is ServerLevel) return

        val eyePos = demon.getEyePosition(1f)
        val lookVec = demon.lookAngle

        val particleCount = if (demon.isEnraged()) 45 else 30
        for (i in 0 until particleCount) {
            val distance = demon.random.nextDouble() * range
            val spreadAngle = Math.toRadians(coneAngle)

            val yaw = (demon.random.nextDouble() - 0.5) * spreadAngle
            val pitch = (demon.random.nextDouble() - 0.5) * spreadAngle

            val particleVec = lookVec.yRot(yaw.toFloat()).xRot(pitch.toFloat())
            val particlePos = eyePos.add(particleVec.scale(distance))

            level.sendParticles(
                ParticleTypes.FLAME,
                particlePos.x, particlePos.y, particlePos.z,
                1,
                0.05, 0.05, 0.05,
                0.02
            )

            if (demon.random.nextFloat() < 0.3f) {
                level.sendParticles(
                    ParticleTypes.LARGE_SMOKE,
                    particlePos.x, particlePos.y, particlePos.z,
                    1,
                    0.1, 0.1, 0.1,
                    0.01
                )
            }
        }

        val nearbyEntities = level.getEntitiesOfClass(
            LivingEntity::class.java,
            demon.boundingBox.inflate(range),
            { it != demon && it.isAlive }
        )

        for (entity in nearbyEntities) {
            if (isInFireCone(entity, eyePos, lookVec)) {
                val damage = if (demon.isEnraged()) 3.5f else 2.5f
                entity.hurt(demon.damageSources().mobAttack(demon), damage)

                val fireDuration = if (demon.isEnraged()) 8 else 5
                entity.igniteForSeconds(fireDuration.toFloat())

                val knockbackStrength = 0.3
                val knockbackVec = entity.position().subtract(demon.position()).normalize()
                entity.deltaMovement = entity.deltaMovement.add(
                    knockbackVec.x * knockbackStrength,
                    0.1,
                    knockbackVec.z * knockbackStrength
                )
                entity.hurtMarked = true
            }
        }
    }

    private fun isInFireCone(entity: LivingEntity, eyePos: Vec3, lookVec: Vec3): Boolean {
        val toEntity = entity.position().add(0.0, entity.bbHeight / 2.0, 0.0).subtract(eyePos)
        val distance = toEntity.length()

        if (distance > range) return false

        val toEntityNorm = toEntity.normalize()
        val dot = lookVec.dot(toEntityNorm)
        val angleThreshold = cos(Math.toRadians(coneAngle))

        return dot >= angleThreshold
    }
}