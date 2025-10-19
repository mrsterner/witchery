package dev.sterner.witchery.content.entity.goal

import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.goal.FleeSunGoal
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation


open class VampireEscapeSunGoal(val mob: PathfinderMob, speedModifier: Double) : FleeSunGoal(mob, speedModifier) {

    override fun canUse(): Boolean {
        return if (!mob.level().isDay) {
            false
        } else if (!mob.level().canSeeSky(mob.blockPosition())) {
            false
        } else {
            this.setWantedPos()
        }
    }

    override fun start() {
        (this.mob.navigation as GroundPathNavigation).setAvoidSun(true)
        super.start()
    }
}