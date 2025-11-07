package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.content.entity.ImpEntity
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.npc.AbstractVillager
import net.minecraft.world.entity.player.Player

class LookAtTradingPlayerGoal(private val imp: ImpEntity) :
    LookAtPlayerGoal(imp, Player::class.java, 8.0f) {
    override fun canUse(): Boolean {
        if (this.imp.tradingPlayer != null) {
            this.lookAt = this.imp.tradingPlayer
            return true
        } else {
            return false
        }
    }
}
