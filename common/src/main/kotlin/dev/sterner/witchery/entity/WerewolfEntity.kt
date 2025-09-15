package dev.sterner.witchery.entity

import dev.sterner.witchery.handler.affliction.WerewolfSpecificEventHandler
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
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

class WerewolfEntity(level: Level) : Monster(WitcheryEntityTypes.WEREWOLF.get(), level) {

    override fun registerGoals() {
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, false))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))

        goalSelector.addGoal(5, RandomStrollGoal(this, 1.0))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f, 1.0f))
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

    override fun removeWhenFarAway(distanceToClosestPlayer: Double): Boolean {
        return true
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }

        val CAN_INFECT: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            WerewolfEntity::class.java, EntityDataSerializers.BOOLEAN
        )
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(CAN_INFECT, false)
    }

    override fun doHurtTarget(target: Entity): Boolean {
        if (target is ServerPlayer && level() is ServerLevel) {
            if (entityData.get(CAN_INFECT)) {
                WerewolfSpecificEventHandler.infectPlayer(target)
            }
        }
        return super.doHurtTarget(target)
    }
}