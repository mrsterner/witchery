package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.entity.CovenWitchEntity
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal

class InterruptWaterAvoidingRandomStrollGoal(var witch: CovenWitchEntity, speedModifier: Double) :
    WaterAvoidingRandomStrollGoal(witch, speedModifier) {

    override fun canContinueToUse(): Boolean {
        if (witch.getLastRitualPos().isEmpty) {
            return super.canContinueToUse()
        }
        return false
    }

    override fun canUse(): Boolean {
        if (witch.getLastRitualPos().isEmpty) {
            return super.canUse()
        }
        return false
    }
}