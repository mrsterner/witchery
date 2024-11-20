package dev.sterner.witchery.entity.goal

import dev.sterner.witchery.entity.VampireEntity
import net.minecraft.world.entity.ai.goal.target.TargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import java.util.*

open class VampireHurtByTargetGoal(val vampire: VampireEntity) : TargetGoal(vampire, true) {
    private var timestamp = 0

    init {
        this.flags = EnumSet.of(Flag.TARGET)
    }

    override fun canUse(): Boolean {
        val i = vampire.lastHurtByMobTimestamp
        val livingEntity = vampire.lastHurtByMob
        return if (i != this.timestamp && livingEntity != null && vampire.getOwnerUUID() != livingEntity.uuid) {
            this.canAttack(livingEntity, HURT_BY_TARGETING)
        } else {
            false
        }
    }

    override fun start() {
        mob.target = mob.lastHurtByMob
        this.targetMob = mob.target
        this.timestamp = mob.lastHurtByMobTimestamp
        this.unseenMemoryTicks = 300

        super.start()
    }

    companion object {
        private val HURT_BY_TARGETING: TargetingConditions =
            TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting()
    }
}