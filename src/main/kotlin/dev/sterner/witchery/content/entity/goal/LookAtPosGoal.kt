package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.content.entity.CovenWitchEntity
import net.minecraft.world.entity.ai.goal.Goal
import java.util.*

class LookAtPosGoal(private val witch: CovenWitchEntity) : Goal() {

    init {
        this.flags = EnumSet.of(Flag.MOVE, Flag.LOOK)
    }

    override fun canUse(): Boolean {
        return witch.getLastRitualPos().isPresent
    }

    override fun canContinueToUse(): Boolean {
        return witch.getLastRitualPos().isPresent
    }

    override fun requiresUpdateEveryTick(): Boolean {
        return true
    }

    override fun tick() {
        val pos = witch.getLastRitualPos()
        if (pos.isPresent) {
            witch.lookControl.setLookAt(pos.get().x + 0.5, pos.get().y + 0.5, pos.get().z + 0.5)
        }
    }
}