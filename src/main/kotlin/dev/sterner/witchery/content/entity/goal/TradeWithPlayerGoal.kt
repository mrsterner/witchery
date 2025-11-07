package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.content.entity.ImpEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.npc.AbstractVillager
import java.util.*


class TradeWithPlayerGoal(private val mob: ImpEntity) : Goal() {
    init {
        this.setFlags(EnumSet.of<Flag?>(Flag.JUMP, Flag.MOVE))
    }

    override fun canUse(): Boolean {
        if (!this.mob.isAlive) {
            return false
        } else if (this.mob.isInWater) {
            return false
        } else if (this.mob.hurtMarked) {
            return false
        } else {
            val player = this.mob.tradingPlayer
            if (player == null) {
                return false
            } else {
                return if (this.mob.distanceToSqr(player) > 16.0) false else player.containerMenu != null
            }
        }
    }

    override fun start() {
        this.mob.getNavigation().stop()
    }

    override fun stop() {
        this.mob.tradingPlayer = null
    }
}
