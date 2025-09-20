package dev.sterner.witchery.entity.goal

import dev.sterner.witchery.mixin.TargetingConditionsMixin.MobAccessor
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.goal.Goal

class DisorientationGoal(private val mob: Mob) : Goal() {

    override fun canUse(): Boolean {
        return (mob as? MobAccessor)?.let { mob.entityData.get(MobAccessor.Data.DISORIENTED) } == true
    }

    override fun tick() {
        mob.lookControl.setLookAt(
            mob.x + mob.random.nextGaussian(),
            mob.eyeY,
            mob.z + mob.random.nextGaussian()
        )
        if (mob.navigation.isInProgress) {
            mob.navigation.stop()
        }

        mob.yRot += (mob.random.nextFloat() - 0.5f) * 30f
        mob.yBodyRot = mob.yRot
    }
}
