package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.players.OldUsersConverter
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.pathfinder.PathType
import java.util.*

class ElleEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.ELLE.get(), level) {

    private var conversionTime: Int? = null

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }

        val DATA_OWNERUUID_ID: EntityDataAccessor<Optional<UUID>> = SynchedEntityData.defineId(
            ElleEntity::class.java, EntityDataSerializers.OPTIONAL_UUID
        )
    }

    override fun fireImmune(): Boolean {
        return true
    }

    override fun baseTick() {
        super.baseTick()

        if (isInNether()) {
            if (conversionTime == null) {
                conversionTime = 300
            } else {
                conversionTime = conversionTime?.minus(1)
                if (conversionTime == 0) {
                    transformToLilith()
                }
            }
        } else {
            conversionTime = null
        }

        if (!entityData.get(DATA_OWNERUUID_ID).isPresent) {
            val player = level().getNearestPlayer(this, 10.0)
            if (player != null) {
                setOwnerUUID(player.uuid)
            }
        }
    }

    private fun transformToLilith(){
        val lilith = WitcheryEntityTypes.LILITH.get().create(level())
        lilith!!.moveTo(position())
        level().addFreshEntity(lilith)
        discard()
    }

    private fun isInNether(): Boolean {
        return level().dimension() == Level.NETHER
    }

    override fun registerGoals() {
        this.goalSelector.addGoal(1, ElleFollowOwnerGoal(this, 1.0, 5.0f))
        this.goalSelector.addGoal(2, LookAtPlayerGoal(this,
            Player::class.java, 15.0f, 1.0f))
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
        if (conversionTime != null) {
            compound.putInt("ConversionTime", conversionTime!!)
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

        if (compound.contains("ConversionTime")) {
            conversionTime = compound.getInt("ConversionTime")
        }
    }

    class ElleFollowOwnerGoal(
        private val elle: ElleEntity,
        private val speedModifier: Double,
        private val startDistance: Float
    ) :
        Goal() {
        private var owner: LivingEntity? = null
        private val navigation: PathNavigation = elle.navigation
        private var timeToRecalcPath = 0
        private var oldWaterCost = 0f

        init {
            this.flags = EnumSet.of(Flag.MOVE, Flag.LOOK)
        }

        override fun canUse(): Boolean {
            val uuid = elle.entityData.get(DATA_OWNERUUID_ID)
            if (uuid.isPresent) {
                val livingEntity = elle.level().getPlayerByUUID(uuid.get())
                if (livingEntity == null) {
                    return false
                } else if (elle.distanceToSqr(livingEntity) < (this.startDistance * this.startDistance).toDouble()) {
                    return false
                } else {
                    this.owner = livingEntity
                    return true
                }
            }

           return false
        }

        override fun canContinueToUse(): Boolean {
            return !navigation.isDone
        }

        override fun start() {
            this.timeToRecalcPath = 0
            this.oldWaterCost = elle.getPathfindingMalus(PathType.WATER)
            elle.setPathfindingMalus(PathType.WATER, 0.0f)
        }

        override fun stop() {
            this.owner = null
            navigation.stop()
            elle.setPathfindingMalus(PathType.WATER, this.oldWaterCost)
        }

        override fun tick() {
            elle.lookControl.setLookAt(this.owner, 10.0f, elle.maxHeadXRot.toFloat())

            if (--this.timeToRecalcPath <= 0) {
                this.timeToRecalcPath = this.adjustedTickDelay(10)
                navigation.moveTo(this.owner, this.speedModifier)
            }
        }
    }
}