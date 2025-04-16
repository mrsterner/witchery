package dev.sterner.witchery.entity

import dev.sterner.witchery.handler.werewolf.WerewolfEventHandler
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.Level

class WerewolfEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.WEREWOLF.get(), level) {

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }

        val CAN_INFECT: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            WerewolfEntity::class.java, EntityDataSerializers.BOOLEAN
        )
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(CAN_INFECT, false)
    }

    override fun doHurtTarget(target: Entity): Boolean {
        if (target is ServerPlayer && level() is ServerLevel) {
            if (entityData.get(CAN_INFECT)) {
                WerewolfEventHandler.infectPlayer(target)
            }
        }
        return super.doHurtTarget(target)
    }
}