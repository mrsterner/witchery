package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.players.OldUsersConverter
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.goal.target.TargetGoal
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.npc.AbstractVillager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*

class VampireEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.VAMPIRE.get(), level) {

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, 0.5))
        goalSelector.addGoal(2, LookAtPlayerGoal(this,
            Player::class.java, 15.0f, 1.0f)
        )
        targetSelector.addGoal(0, VampireHurtByTargetGoal(this))
        targetSelector.addGoal(
            3, NearestAttackableTargetGoal(
                this,
                AbstractVillager::class.java, false
            )
        )
        super.registerGoals()
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(DATA_OWNERUUID_ID, Optional.empty())
    }

    fun getOwnerUUID(): UUID? {
        return entityData.get(DATA_OWNERUUID_ID).orElse(null)
    }

    fun setOwnerUUID(uuid: UUID?) {
        entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid))
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        if (this.getOwnerUUID() != null) {
            compound.putUUID("Owner", this.getOwnerUUID()!!)
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        val uUID: UUID?
        if (compound.hasUUID("Owner")) {
            uUID = compound.getUUID("Owner")
        } else {
            val string = compound.getString("Owner")
            uUID = OldUsersConverter.convertMobOwnerIfNecessary(this.server, string)
        }

        if (uUID != null) {
            this.setOwnerUUID(uUID)
        }
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }

        val DATA_OWNERUUID_ID: EntityDataAccessor<Optional<UUID>> = SynchedEntityData.defineId(
            VampireEntity::class.java, EntityDataSerializers.OPTIONAL_UUID
        )
    }

    open class VampireHurtByTargetGoal(val vampire: VampireEntity) : TargetGoal(vampire, true) {
        private var timestamp = 0

        init {
            this.flags = EnumSet.of(Flag.TARGET)
        }

        override fun canUse(): Boolean {
            val i = vampire.lastHurtByMobTimestamp
            val livingEntity = vampire.lastHurtByMob
            return if (i != this.timestamp && livingEntity != null && vampire.getOwnerUUID() != livingEntity.uuid) {
                this.canAttack(livingEntity, HURT_BY_TARGETING)
            } else {
                false
            }
        }

        override fun start() {
            mob.target = mob.lastHurtByMob
            this.targetMob = mob.target
            this.timestamp = mob.lastHurtByMobTimestamp
            this.unseenMemoryTicks = 300

            super.start()
        }

        companion object {
            private val HURT_BY_TARGETING: TargetingConditions =
                TargetingConditions.forCombat().ignoreLineOfSight().ignoreInvisibilityTesting()
        }
    }
}