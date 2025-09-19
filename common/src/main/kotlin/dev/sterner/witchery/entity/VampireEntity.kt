package dev.sterner.witchery.entity

import dev.sterner.witchery.entity.goal.DrinkBloodTargetingGoal
import dev.sterner.witchery.entity.goal.NightHuntGoal
import dev.sterner.witchery.entity.goal.VampireEscapeSunGoal
import dev.sterner.witchery.entity.goal.VampireHurtByTargetGoal
import dev.sterner.witchery.handler.BloodPoolHandler
import dev.sterner.witchery.handler.affliction.VampireChildrenHuntHandler
import dev.sterner.witchery.mixin.DamageSourcesInvoker
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.registry.WitcheryDamageSources
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.block.blood_crucible.BloodCrucibleBlockEntity
import dev.sterner.witchery.handler.affliction.AfflictionTypes
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
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
    var altarPos: BlockPos? = null
    var collectedBlood: Int = 0
    private var inSunTick = 0
    private var masterPlayer: Player? = null
    private var hasMaster = false

    override fun getWalkTargetValue(pos: BlockPos, level: LevelReader): Float {
        return -level.getPathfindingCostFromLightLevels(pos)
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(0, VampireEscapeSunGoal(this, 1.4))
        goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, 0.5))
        goalSelector.addGoal(
            2, LookAtPlayerGoal(
                this,
                Player::class.java, 15.0f, 1.0f
            )
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
                val bloodAmount = 10
                BloodPoolHandler.increaseBlood(this, bloodAmount)
                BloodPoolHandler.decreaseBlood(target, bloodAmount)
                collectedBlood += bloodAmount
            }
        }
        return bl
    }

    override fun baseTick() {
        super.baseTick()

        handleSunlightDamage()

        handleBloodHealing()

        updateHuntStatus()

        tryDepositBloodAtAltar()

        if (hasMaster && level() is ServerLevel) {
            updateMasterPlayerReference()
        }
    }

    private fun handleSunlightDamage() {
        val canSeeSky = this.level().canSeeSky(this.blockPosition())
        val isDay = this.level().isDay
        val isInSunlight = canSeeSky && isDay

        if (isInSunlight) {
            inSunTick++
            inSunTick = min(inSunTick, 80)

            if (inSunTick >= 80) {
                if (this.tickCount % 20 == 0) {
                    val sunDamageSource = (this.level().damageSources() as DamageSourcesInvoker)
                        .invokeSource(WitcheryDamageSources.IN_SUN)

                    this.hurt(sunDamageSource, 1f)
                    BloodPoolHandler.decreaseBlood(this, 10)
                    this.remainingFireTicks = 20

                    this.level().playSound(
                        null,
                        this.x,
                        this.y,
                        this.z,
                        SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.PLAYERS,
                        0.5f,
                        1.0f
                    )
                }
            }
        } else {
            inSunTick = 0
            if (this.remainingFireTicks > 0 && !isDay) {
                this.clearFire()
            }
        }
    }
    
    private fun handleBloodHealing() {
        val bloodData = BloodPoolLivingEntityAttachment.getData(this)
        if (bloodData.bloodPool >= 75 && this.level().random.nextBoolean()) {
            if (this.health < this.maxHealth && this.health > 0) {
                BloodPoolHandler.decreaseBlood(this, 75)
                this.heal(1f)
            }
        }
    }
    
    private fun updateHuntStatus() {
        val currentTime = level().dayTime
        if (currentTime - lastHuntTimestamp >= nightDuration()) {
            huntedLastNight = false
        }
    }
    
    private fun tryDepositBloodAtAltar() {
        if (level() is ServerLevel && collectedBlood > 0 && altarPos != null) {
            val blockEntity = level().getBlockEntity(altarPos!!)
            if (blockEntity is BloodCrucibleBlockEntity && this.distanceToSqr(altarPos!!.x.toDouble(), altarPos!!.y.toDouble(), altarPos!!.z.toDouble()) < 4.0) {
                if (!level().canSeeSky(altarPos!!) || !level().isDay) {
                    blockEntity.addBlood(collectedBlood)
                    if (hasMaster && masterPlayer != null) {
                        val masterBloodAmount = (collectedBlood * 0.2).toInt()
                        if (masterBloodAmount > 0) {
                            BloodPoolHandler.increaseBlood(masterPlayer!!, masterBloodAmount)
                        }
                    }
                    collectedBlood = 0
                }
            }
        }
    }

    private fun updateMasterPlayerReference() {
        val serverLevel = level() as ServerLevel
        val uuid = getOwnerUUID()
        if (uuid != null) {
            val player = serverLevel.server.playerList.getPlayer(uuid)
            if (player != null) {
                val vampireData = AfflictionPlayerAttachment.getData(player)
                if (vampireData.getLevel(AfflictionTypes.VAMPIRISM) >= 10) {
                    masterPlayer = player
                    hasMaster = true
                } else {
                    hasMaster = false
                    masterPlayer = null
                }
            }
        }
    }

    fun tryStartHunt() {
        if (level() is ServerLevel && !huntedLastNight && !level().isDay) {
            val serverLevel = level() as ServerLevel
            val ownerUUID = getOwnerUUID()
            
            if (ownerUUID != null) {
                VampireChildrenHuntHandler.tryStartHunt(serverLevel, this, ownerUUID)
                huntedLastNight = true
                lastHuntTimestamp = level().dayTime
            }
        }
    }

    fun returnFromHunt(bloodAmount: Int) {
        collectedBlood += bloodAmount
        huntedLastNight = true
    }

    private fun nightDuration(): Long {
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
        if (uuid != null && level() is ServerLevel) {
            val player = (level() as ServerLevel).server.playerList.getPlayer(uuid)
            if (player != null) {
                val vampireData = AfflictionPlayerAttachment.getData(player)
                if (vampireData.getVampireLevel() >= 10) {
                    masterPlayer = player
                    hasMaster = true
                }
            }
        }
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
        if (this.altarPos != null) {
            compound.put("AltarPos", NbtUtils.writeBlockPos(this.altarPos!!))
        }
        compound.putLong("HuntTimeStamp", lastHuntTimestamp)
        compound.putBoolean("HuntedLastNight", this.huntedLastNight)
        compound.putInt("CollectedBlood", this.collectedBlood)
        compound.putBoolean("HasMaster", this.hasMaster)
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
        this.altarPos = NbtUtils.readBlockPos(compound, "AltarPos").orElse(null)
        this.huntedLastNight = compound.getBoolean("HuntedLastNight")
        this.lastHuntTimestamp = compound.getLong("LastHuntTimestamp")
        this.collectedBlood = compound.getInt("CollectedBlood")
        this.hasMaster = compound.getBoolean("HasMaster")
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