package dev.sterner.witchery.entity.goal

import dev.sterner.witchery.entity.VampireEntity
import dev.sterner.witchery.platform.transformation.VampireChildrenHuntLevelAttachment
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.goal.Goal

class NightHuntGoal(val vampire: VampireEntity) : Goal() {

    override fun canUse(): Boolean {
        if (vampire.level().isNight && vampire.level() is ServerLevel) {
            val serverLevel = vampire.level() as ServerLevel
            val bl = (vampire.creationPos != null) || (vampire.coffinPos != null)
            if (bl && vampire.getOwnerUUID() != null) {
                VampireChildrenHuntLevelAttachment.tryStarHunt(serverLevel, vampire, vampire.getOwnerUUID()!!)
                return true
            }
        }

        return false
    }
}