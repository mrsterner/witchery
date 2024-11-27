package dev.sterner.witchery.entity

import dev.sterner.witchery.entity.goal.DrinkBloodTargetingGoal
import dev.sterner.witchery.entity.goal.NightHuntGoal
import dev.sterner.witchery.entity.goal.VampireEscapeSunGoal
import dev.sterner.witchery.entity.goal.VampireHurtByTargetGoal
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.registry.WitcheryDamageSources
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.players.OldUsersConverter
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
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
import net.minecraft.world.level.LevelReader
import java.util.*
import kotlin.math.min

class VampireEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.VAMPIRE.get(), level) {

    var lastHuntTimestamp: Long = 0L
    var huntedLastNight: Boolean = false
    var creationPos: BlockPos? = null
    var coffinPos: BlockPos? = null
    private var inSunTick = 0

    override fun getWalkTargetValue(pos: BlockPos, level: LevelReader): Float {
        return -level.getPathfindingCostFromLightLevels(pos)
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(0, VampireEscapeSunGoal(this, 1.4))
        goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, 0.5))
        goalSelector.addGoal(2, LookAtPlayerGoal(this,
            Player::class.java, 15.0f, 1.0f)
        )
        goalSelector.addGoal(5, NightHuntGoal(this))
        targetSelector.addGoal(3, DrinkBloodTargetingGoal(this, Mob::class.java, true))
        targetSelector.addGoal(0, VampireHurtByTargetGoal(this))
        targetSelector.addGoal(
            3, NearestAttackableTargetGoal(
                this,
                AbstractVillager::class.java, false
            )
        )
        super.registerGoals()
    }

    override fun doHurtTarget(target: Entity): Boolean {
        val bl = super.doHurtTarget(target)
        val pool = BloodPoolLivingEntityAttachment.getData(this)
        if (bl && target is LivingEntity && pool.bloodPool < pool.maxBlood) {
            val targetBlood = BloodPoolLivingEntityAttachment.getData(target)
            if (targetBlood.maxBlood > 0) {
                BloodPoolLivingEntityAttachment.increaseBlood(this, 10)
                BloodPoolLivingEntityAttachment.decreaseBlood(target, 10)
            }

        }
        return bl
    }

    override fun baseTick() {
        super.baseTick()

        val isInSunlight = this.level().canSeeSky(this.blockPosition()) && this.level().isDay
        val sunDamageSource = this.level().damageSources().source(WitcheryDamageSources.IN_SUN)
        if (isInSunlight) {
            inSunTick++
            inSunTick = min(inSunTick, 80)

            if (inSunTick >= 80) {
                if (this.tickCount % 20 == 0) {
                    this.hurt(sunDamageSource, 1f)
                    BloodPoolLivingEntityAttachment.decreaseBlood(this, 10)
                    this.remainingFireTicks = 20
                    this.level().playSound(null, this.x, this.y, this.z, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS , 0.5f, 1.0f)
                }
            }
        } else {
            inSunTick = 0
        }

        val bloodData = BloodPoolLivingEntityAttachment.getData(this)
        if (bloodData.bloodPool >= 75 && this.level().random.nextBoolean()) {
            if (this.health < this.maxHealth && this.health > 0) {
                BloodPoolLivingEntityAttachment.decreaseBlood(this, 75)
                this.heal(1f)
            }
        }
        val currentTime = level().dayTime
        if (currentTime - lastHuntTimestamp >= level().nightDuration()) {
            huntedLastNight = false
        }
    }

    private fun Level.nightDuration(): Long {
        return 23000 - 13000
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
        compound.putInt("InSun", this.inSunTick)
        if (this.coffinPos != null) {
            compound.put("CoffinPos", NbtUtils.writeBlockPos(this.coffinPos!!))
        }
        if (this.creationPos != null) {
            compound.put("CreationPos", NbtUtils.writeBlockPos(this.creationPos!!))
        }
        compound.putLong("HuntTimeStamp", lastHuntTimestamp)
        compound.putBoolean("HuntedLastNight", this.huntedLastNight)
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
        inSunTick = compound.getInt("InSun")

        this.coffinPos = NbtUtils.readBlockPos(compound, "CoffinPos").orElse(null)
        this.creationPos = NbtUtils.readBlockPos(compound, "CreationPos").orElse(null)
        this.huntedLastNight = compound.getBoolean("HuntedLastNight")
        this.lastHuntTimestamp = compound.getLong("LastHuntTimestamp")
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
}