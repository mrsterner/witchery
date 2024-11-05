package dev.sterner.witchery.entity.goal

import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.util.DefaultRandomPos
import java.util.*

open class WitcheryPanicGoal(
    private val mob: PathfinderMob,
    private val speedModifier: Double
) :
    Goal() {

    private var posX: Double = 0.0
    private var posY: Double = 0.0
    private var posZ: Double = 0.0
    private var isRunning: Boolean = false

    init {
        this.flags = EnumSet.of(Flag.MOVE)
    }

    override fun canUse(): Boolean {
        return if (!this.shouldPanic()) {
            false
        } else {
            this.findRandomPosition()
        }
    }

    protected open fun shouldPanic(): Boolean {
        return true
    }

    private fun findRandomPosition(): Boolean {
        val vec3 = DefaultRandomPos.getPos(this.mob, 5, 4)
        if (vec3 == null) {
            return false
        } else {
            this.posX = vec3.x
            this.posY = vec3.y
            this.posZ = vec3.z
            return true
        }
    }

    override fun start() {
        mob.navigation.moveTo(this.posX, this.posY, this.posZ, this.speedModifier)
        this.isRunning = true
    }

    override fun stop() {
        this.isRunning = false
    }

    override fun canContinueToUse(): Boolean {
        return !mob.navigation.isDone
    }
}