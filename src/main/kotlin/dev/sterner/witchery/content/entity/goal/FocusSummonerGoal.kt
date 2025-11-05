package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.content.entity.DeathEntity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.Goal
import java.util.*

class FocusSummonerGoal(
    private val mob: Mob,
    private val focusDurationTicks: Int = 20 * 60
) : Goal() {

    private var summonerUUID: UUID? = null
    private var focusEndTime: Long = 0
    private var ticksWithoutTarget = 0

    init {
        flags = EnumSet.of(Flag.TARGET)
    }

    fun setSummoner(summoner: LivingEntity) {
        this.summonerUUID = summoner.uuid
        this.focusEndTime = mob.level().gameTime + focusDurationTicks
        this.ticksWithoutTarget = 0
    }

    fun isFocusActive(): Boolean {
        return summonerUUID != null && mob.level().gameTime < focusEndTime
    }

    fun getRemainingFocusTime(): Long {
        return if (isFocusActive()) {
            focusEndTime - mob.level().gameTime
        } else {
            0L
        }
    }

    override fun canUse(): Boolean {
        if (!isFocusActive()) {
            return false
        }

        val summoner = getSummoner()
        return summoner != null && summoner.isAlive
    }

    override fun canContinueToUse(): Boolean {
        if (!isFocusActive()) {
            return false
        }

        val summoner = getSummoner()
        if (summoner == null || !summoner.isAlive) {
            clearFocus()
            return false
        }

        if (mob.level() != summoner.level()) {
            ticksWithoutTarget++
            if (ticksWithoutTarget > 200) {
                clearFocus()
                return false
            }
        } else {
            ticksWithoutTarget = 0
        }

        return true
    }

    override fun start() {
        val summoner = getSummoner()
        if (summoner != null) {
            mob.target = summoner
        }
    }

    override fun tick() {
        val summoner = getSummoner() ?: return

        if (mob.target != summoner) {
            mob.target = summoner
        }

        if (mob is DeathEntity && !mob.hasForcedTarget) {
            mob.setForcedTarget(summoner)
        }

        if (mob.level() == summoner.level()) {
            val distanceSq = mob.distanceToSqr(summoner)

            if (distanceSq > 64.0 * 64.0) {
                attemptTeleportToSummoner(summoner)
            }
        }
    }

    override fun stop() {
        if (mob is DeathEntity) {
            mob.hasForcedTarget = false
        }
    }

    private fun getSummoner(): LivingEntity? {
        val uuid = summonerUUID ?: return null

        for (player in mob.level().players()) {
            if (player.uuid == uuid) {
                return player
            }
        }

        return null
    }

    private fun clearFocus() {
        summonerUUID = null
        focusEndTime = 0
        if (mob is DeathEntity) {
            mob.hasForcedTarget = false
        }
    }

    private fun attemptTeleportToSummoner(summoner: LivingEntity) {
        if (mob !is DeathEntity) return

        val death = mob as DeathEntity

        val random = mob.random
        for (i in 0 until 10) {
            val angle = random.nextDouble() * Math.PI * 2
            val distance = 5.0 + random.nextDouble() * 10.0
            val x = summoner.x + kotlin.math.cos(angle) * distance
            val z = summoner.z + kotlin.math.sin(angle) * distance
            val y = summoner.y

            death.teleportTo(x, y, z)

            if (mob.distanceToSqr(summoner) < 32.0 * 32.0) {
                break
            }
        }
    }

    fun onSummonerDeath() {
        clearFocus()
    }
}