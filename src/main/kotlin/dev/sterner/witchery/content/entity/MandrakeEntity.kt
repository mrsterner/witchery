package dev.sterner.witchery.content.entity

import dev.sterner.witchery.content.entity.goal.WitcheryPanicGoal
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.Level

class MandrakeEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.MANDRAKE.get(), level) {

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(0, WitcheryPanicGoal(this, 2.0))
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5.0)
                .add(Attributes.MOVEMENT_SPEED, 0.2)
        }
    }
}