package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.entity.VampireEntity
import net.minecraft.world.entity.ai.goal.Goal

class NightHuntGoal(private val vampire: VampireEntity) : Goal() {

    private var huntAttemptCooldown: Int = 0

    override fun canUse(): Boolean {
        return vampire.level().isNight &&
                vampire.getOwnerUUID() != null &&
                !vampire.huntedLastNight &&
                huntAttemptCooldown <= 0
    }

    override fun start() {
        vampire.tryStartHunt()
        huntAttemptCooldown = 200
    }

    override fun tick() {
        if (huntAttemptCooldown > 0) {
            huntAttemptCooldown--
        }
    }

    override fun requiresUpdateEveryTick(): Boolean {
        return true
    }
}