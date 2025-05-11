package dev.sterner.witchery.entity

import dev.sterner.witchery.entity.goal.DrinkBloodTargetingGoal
import dev.sterner.witchery.entity.goal.NightHuntGoal
import dev.sterner.witchery.entity.goal.VampireEscapeSunGoal
import dev.sterner.witchery.entity.goal.VampireHurtByTargetGoal
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.npc.AbstractVillager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class BabaYagaEntity( level: Level) : PathfinderMob(WitcheryEntityTypes.BABA_YAGA.get(), level) {

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, 0.5))
        goalSelector.addGoal(
            2, LookAtPlayerGoal(
                this,
                Player::class.java, 15.0f, 1.0f
            )
        )
        super.registerGoals()
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }
    }
}