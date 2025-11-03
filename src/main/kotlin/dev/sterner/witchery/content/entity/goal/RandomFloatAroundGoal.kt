package dev.sterner.witchery.content.entity.goal

import dev.sterner.witchery.content.entity.AbstractSpectralEntity
import dev.sterner.witchery.content.entity.BansheeEntity
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.Goal.Flag
import java.util.EnumSet

class RandomFloatAroundGoal(private val bansheeEntity: AbstractSpectralEntity) : Goal() {

    init {
        this.flags = EnumSet.of(Flag.MOVE)
    }

    override fun canUse(): Boolean {
        val moveControl = bansheeEntity.moveControl
        if (!moveControl.hasWanted()) {
            return true
        } else {
            val d = moveControl.wantedX - bansheeEntity.x
            val e = moveControl.wantedY - bansheeEntity.y
            val f = moveControl.wantedZ - bansheeEntity.z
            val g = d * d + e * e + f * f
            return g < 1.0 || g > 3600.0
        }
    }

    override fun canContinueToUse(): Boolean {
        return false
    }

    override fun start() {
        val randomSource = bansheeEntity.random
        val d = bansheeEntity.x + ((randomSource.nextFloat() * 2.0f - 1.0f) * 8.0f).toDouble()

        val e = if (randomSource.nextFloat() < 0.6) {
            bansheeEntity.y - (randomSource.nextFloat() * 8.0f).toDouble()
        } else {
            bansheeEntity.y + (randomSource.nextFloat() * 8.0f).toDouble()
        }

        val f = bansheeEntity.z + ((randomSource.nextFloat() * 2.0f - 1.0f) * 8.0f).toDouble()
        bansheeEntity.moveControl.setWantedPosition(d, e, f, 1.0)
    }
}