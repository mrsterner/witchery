package dev.sterner.witchery.content.entity

import dev.sterner.witchery.content.entity.goal.WerewolfBiteGoal
import dev.sterner.witchery.content.entity.goal.WerewolfHowlGoal
import dev.sterner.witchery.features.affliction.werewolf.WerewolfSpecificEventHandler
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.features.affliction.villager_afflictions.VillagerWerewolfHandler
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.Sheep
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.monster.piglin.AbstractPiglin
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class WerewolfEntity(level: Level) : Monster(WitcheryEntityTypes.WEREWOLF.get(), level) {

    private var howlCooldown = 0
    private var biteCooldown = 0

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, WerewolfHowlGoal(this))
        goalSelector.addGoal(2, WerewolfBiteGoal(this))
        goalSelector.addGoal(4, MeleeAttackGoal(this, 1.1, false))
        goalSelector.addGoal(5, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(6, RandomStrollGoal(this, 0.8))
        goalSelector.addGoal(7, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))

        targetSelector.addGoal(1, HurtByTargetGoal(this))

        targetSelector.addGoal(2, object : NearestAttackableTargetGoal<Player>(
            this, Player::class.java, true
        ) {})

        targetSelector.addGoal(3, object : NearestAttackableTargetGoal<Villager>(
            this, Villager::class.java, true
        ) {
            override fun canUse(): Boolean {
                return !isFromVillager() && super.canUse()
            }
        })

        targetSelector.addGoal(4, NearestAttackableTargetGoal(this, Sheep::class.java, true))
        targetSelector.addGoal(5, NearestAttackableTargetGoal(this, AbstractPiglin::class.java, true))
    }

    override fun aiStep() {
        super.aiStep()

        if (!level().isClientSide) {
            if (howlCooldown > 0) howlCooldown--
            if (biteCooldown > 0) biteCooldown--

            if (isFromVillager() && VillagerWerewolfHandler.shouldTransformToVillager(this)) {
                VillagerWerewolfHandler.transformToVillager(this)
            }
        }
    }

    override fun doHurtTarget(target: Entity): Boolean {
        val success = super.doHurtTarget(target)

        if (success && target is ServerPlayer && level() is ServerLevel) {
            if (entityData.get(CAN_INFECT)) {
                WerewolfSpecificEventHandler.infectPlayer(target)
            }
        }

        if (success && target is Villager && level() is ServerLevel) {
            if (entityData.get(CAN_INFECT)) {
                VillagerWerewolfHandler.infectVillager(target, null)
            }
        }

        return success
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (source.`is`(DamageTypes.FALL)) {
            return super.hurt(source, amount * 0.25f)
        }

        return super.hurt(source, amount * 0.75f)
    }

    fun setHowling(howling: Boolean) {
        entityData.set(IS_HOWLING, howling)
    }

    fun isHowling(): Boolean {
        return entityData.get(IS_HOWLING)
    }

    fun canHowl(): Boolean = howlCooldown <= 0
    fun canBite(): Boolean = biteCooldown <= 0

    fun setHowlCooldown(ticks: Int) {
        howlCooldown = ticks
    }

    fun setBiteCooldown(ticks: Int) {
        biteCooldown = ticks
    }

    override fun removeWhenFarAway(distanceToClosestPlayer: Double): Boolean {
        return !isFromVillager() && super.removeWhenFarAway(distanceToClosestPlayer)
    }

    fun setVillagerData(nbt: CompoundTag) {
        entityData.set(VILLAGER_DATA, nbt)
    }

    fun getVillagerData(): CompoundTag? {
        return entityData.get(VILLAGER_DATA).takeIf { !it.isEmpty }
    }

    fun setFromVillager(fromVillager: Boolean) {
        entityData.set(FROM_VILLAGER, fromVillager)
    }

    fun isFromVillager(): Boolean {
        return entityData.get(FROM_VILLAGER)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(CAN_INFECT, false)
        builder.define(FROM_VILLAGER, false)
        builder.define(VILLAGER_DATA, CompoundTag())
        builder.define(IS_HOWLING, false)
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.putBoolean("CanInfect", entityData.get(CAN_INFECT))
        tag.putBoolean("FromVillager", entityData.get(FROM_VILLAGER))

        val villagerData = getVillagerData()
        if (villagerData != null) {
            tag.put("VillagerData", villagerData)
        }

        tag.putInt("HowlCooldown", howlCooldown)
        tag.putInt("BiteCooldown", biteCooldown)
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        entityData.set(CAN_INFECT, tag.getBoolean("CanInfect"))
        entityData.set(FROM_VILLAGER, tag.getBoolean("FromVillager"))

        if (tag.contains("VillagerData")) {
            setVillagerData(tag.getCompound("VillagerData"))
        }

        howlCooldown = tag.getInt("HowlCooldown")
        biteCooldown = tag.getInt("BiteCooldown")
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3)
        }

        val CAN_INFECT: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            WerewolfEntity::class.java, EntityDataSerializers.BOOLEAN
        )

        val FROM_VILLAGER: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            WerewolfEntity::class.java, EntityDataSerializers.BOOLEAN
        )

        val VILLAGER_DATA: EntityDataAccessor<CompoundTag> = SynchedEntityData.defineId(
            WerewolfEntity::class.java, EntityDataSerializers.COMPOUND_TAG
        )

        val IS_HOWLING: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            WerewolfEntity::class.java, EntityDataSerializers.BOOLEAN
        )
    }
}