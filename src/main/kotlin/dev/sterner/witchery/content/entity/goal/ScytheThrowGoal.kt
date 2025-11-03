package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.content.entity.DeathEntity
import net.minecraft.world.entity.ai.goal.Goal
import java.util.*


class ScytheThrowGoal(
    private val death: DeathEntity,
    private val maxRange: Double,
    private val attackIntervalMin: Int,
    private val attackIntervalMax: Int
) : Goal() {

    private var attackTime = -1
    private var seeTime = 0
    private var strafingClockwise = false
    private var strafingBackwards = false
    private var strafingTime = -1

    init {
        flags = EnumSet.of(Flag.MOVE, Flag.LOOK)
    }

    override fun canUse(): Boolean {
        val target = death.target ?: return false

        if (!target.isAlive) return false
        if (!death.canThrowScythe()) return false

        val distance = death.distanceToSqr(target)

        return distance >= 64.0 && distance <= maxRange * maxRange
    }

    override fun canContinueToUse(): Boolean {
        val target = death.target ?: return false

        if (!target.isAlive) return false

        val distance = death.distanceToSqr(target)

        return distance >= 49.0 && distance <= (maxRange + 5.0) * (maxRange + 5.0) && death.canThrowScythe()
    }

    override fun start() {
        super.start()
        death.isAggressive = true
        strafingTime = 0
    }

    override fun stop() {
        super.stop()
        death.isAggressive = false
        seeTime = 0
        attackTime = -1
    }

    override fun requiresUpdateEveryTick(): Boolean = true

    override fun tick() {
        val target = death.target ?: return

        val distanceSq = death.distanceToSqr(target)
        val canSee = death.sensing.hasLineOfSight(target)

        if (canSee) {
            seeTime++
        } else {
            seeTime = 0
        }

        death.lookControl.setLookAt(target, 30.0f, 30.0f)

        if (distanceSq <= maxRange * maxRange) {
            strafingTime++

            if (strafingTime >= 20) {
                if (death.random.nextFloat() < 0.3f) {
                    strafingClockwise = !strafingClockwise
                }

                if (death.random.nextFloat() < 0.3f) {
                    strafingBackwards = !strafingBackwards
                }

                strafingTime = 0
            }

            if (strafingTime > -1) {
                if (distanceSq > (maxRange * 0.75) * (maxRange * 0.75)) {
                    strafingBackwards = false
                } else if (distanceSq < (maxRange * 0.5) * (maxRange * 0.5)) {
                    strafingBackwards = true
                }

                death.moveControl.strafe(
                    if (strafingBackwards) -0.5f else 0.5f,
                    if (strafingClockwise) 0.5f else -0.5f
                )
            }

            if (attackTime <= 0 && canSee && seeTime >= 5) {
                death.throwScythe(target)

                val attackInterval = (attackIntervalMin +
                        death.random.nextInt(attackIntervalMax - attackIntervalMin + 1))
                attackTime = attackInterval
            }

            if (attackTime > 0) {
                attackTime--
            }
        }
    }
}