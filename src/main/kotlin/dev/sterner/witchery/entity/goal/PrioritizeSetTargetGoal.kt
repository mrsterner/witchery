package dev.sterner.witchery.entity.goal

import dev.sterner.witchery.entity.DeathEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.Goal
import java.util.EnumSet

class PrioritizeSetTargetGoal(
    private val mob: Mob,
    private val priorityDuration: Int = 400
) : Goal() {

    private var forcedTarget: LivingEntity? = null
    private var forcedTargetSetTime: Long = 0

    init {
        setFlags(EnumSet.of(Flag.TARGET))
    }

    override fun canUse(): Boolean {
        if (mob is DeathEntity && !mob.hasForcedTarget) {
            return false
        }

        val target = mob.target
        if (target != null && forcedTarget == null) {
            forcedTarget = target
            forcedTargetSetTime = mob.level().gameTime
            return true
        }

        if (forcedTarget != null) {
            val timeSinceSet = mob.level().gameTime - forcedTargetSetTime

            if (timeSinceSet >= priorityDuration) {
                forcedTarget = null
                if (mob is DeathEntity) {
                    mob.hasForcedTarget = false
                }
                return false
            }

            if (!forcedTarget!!.isAlive || forcedTarget!!.isRemoved) {
                forcedTarget = null
                if (mob is DeathEntity) {
                    mob.hasForcedTarget = false
                }
                return false
            }

            return true
        }

        return false
    }

    override fun canContinueToUse(): Boolean {
        if (forcedTarget == null) return false

        val timeSinceSet = mob.level().gameTime - forcedTargetSetTime

        return timeSinceSet < priorityDuration &&
                forcedTarget!!.isAlive &&
                !forcedTarget!!.isRemoved
    }

    override fun start() {
        if (forcedTarget != null) {
            mob.target = forcedTarget
        }
    }

    override fun tick() {
        if (forcedTarget != null && mob.target != forcedTarget) {
            mob.target = forcedTarget
        }
    }

    override fun stop() {
        if (mob is DeathEntity) {
            mob.hasForcedTarget = false
        }
    }
}