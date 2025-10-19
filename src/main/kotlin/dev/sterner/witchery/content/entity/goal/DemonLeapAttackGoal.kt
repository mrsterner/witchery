package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.entity.DemonEntity
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.phys.Vec3
import java.util.EnumSet

class DemonLeapAttackGoal(
    private val demon: DemonEntity,
    private val leapCooldown: Int = 200
) : Goal() {

    private var cooldown = 0

    init {
        setFlags(EnumSet.of(Flag.MOVE, Flag.JUMP))
    }

    override fun canUse(): Boolean {
        if (cooldown > 0) {
            cooldown--
            return false
        }

        val target = demon.target ?: return false
        if (!demon.onGround()) return false

        val distance = demon.distanceTo(target)
        return distance in 4.0..12.0 && demon.hasLineOfSight(target)
    }

    override fun start() {
        val target = demon.target ?: return

        val toTarget = Vec3(
            target.x - demon.x,
            0.0,
            target.z - demon.z
        ).normalize()

        val leapStrength = if (demon.isEnraged()) 1.2 else 0.8

        demon.deltaMovement = demon.deltaMovement.add(
            toTarget.x * leapStrength,
            0.5,
            toTarget.z * leapStrength
        )

        demon.hasImpulse = true
        cooldown = leapCooldown

        if (demon.level() is ServerLevel) {
            (demon.level() as ServerLevel).sendParticles(
                ParticleTypes.LAVA,
                demon.x, demon.y, demon.z,
                15, 0.3, 0.1, 0.3, 0.1
            )
        }

        demon.level().playSound(
            null,
            demon.blockPosition(),
            SoundEvents.BLAZE_SHOOT,
            SoundSource.HOSTILE,
            1.0f,
            1.5f
        )
    }

    override fun canContinueToUse(): Boolean = false
}