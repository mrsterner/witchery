package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.pathfinder.PathType

class DemonEntity(level: Level) : Monster(WitcheryEntityTypes.DEMON.get(), level) {

    init{
        this.setPersistenceRequired()
        this.setPathfindingMalus(PathType.WATER, -1.0f)
        this.setPathfindingMalus(PathType.LAVA, 8.0f)
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0f)
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0f)
    }

    override fun isOnFire(): Boolean {
        return false
    }

    override fun fireImmune(): Boolean {
        return true
    }

    override fun registerGoals() {
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, false))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))

        goalSelector.addGoal(5, RandomStrollGoal(this, 0.8))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 3.0f, 1.0f))
        goalSelector.addGoal(4, LookAtPlayerGoal(this, Mob::class.java, 8.0f))
        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(
            2, NearestAttackableTargetGoal(
                this,
                Player::class.java, true
            )
        )
        targetSelector.addGoal(3, NearestAttackableTargetGoal(this, Villager::class.java, true))

        super.registerGoals()
    }



    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }
    }
}