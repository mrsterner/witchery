package dev.sterner.witchery.content.entity

import dev.sterner.witchery.entity.goal.DemonFireBreathGoal
import dev.sterner.witchery.entity.goal.DemonLeapAttackGoal
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageTypes
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.IronGolem
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.pathfinder.PathType

class DemonEntity(level: Level) : Monster(WitcheryEntityTypes.DEMON.get(), level) {

    init {
        this.setPersistenceRequired()
        this.setPathfindingMalus(PathType.WATER, -1.0f)
        this.setPathfindingMalus(PathType.LAVA, 8.0f)
        this.setPathfindingMalus(PathType.DANGER_FIRE, 0.0f)
        this.setPathfindingMalus(PathType.DAMAGE_FIRE, 0.0f)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(BREATHING_FIRE, false)
        builder.define(ENRAGED, false)
    }

    fun isBreathingFire(): Boolean = entityData.get(BREATHING_FIRE)
    fun setBreathing(breathing: Boolean) = entityData.set(BREATHING_FIRE, breathing)

    fun isEnraged(): Boolean = entityData.get(ENRAGED)
    fun setEnraged(enraged: Boolean) = entityData.set(ENRAGED, enraged)

    override fun isOnFire(): Boolean = false

    override fun fireImmune(): Boolean = true

    override fun registerGoals() {
        goalSelector.addGoal(1, DemonFireBreathGoal(this))
        goalSelector.addGoal(2, DemonLeapAttackGoal(this))
        goalSelector.addGoal(3, MeleeAttackGoal(this, 1.2, false))
        goalSelector.addGoal(4, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(5, RandomStrollGoal(this, 0.8))
        goalSelector.addGoal(6, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(7, RandomLookAroundGoal(this))

        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(2, NearestAttackableTargetGoal(this, Player::class.java, true))
        targetSelector.addGoal(3, NearestAttackableTargetGoal(this, Villager::class.java, true))
        targetSelector.addGoal(4, NearestAttackableTargetGoal(this, IronGolem::class.java, true))

        super.registerGoals()
    }

    override fun aiStep() {
        super.aiStep()

        if (health / maxHealth < 0.3f && !isEnraged()) {
            setEnraged(true)
            if (level() is ServerLevel) {
                (level() as ServerLevel).sendParticles(
                    ParticleTypes.LAVA,
                    x, y + 1, z,
                    30, 0.5, 0.5, 0.5, 0.1
                )
            }
        }

        if (level().isClientSide && random.nextFloat() < 0.1f) {
            level().addParticle(
                ParticleTypes.FLAME,
                x + (random.nextDouble() - 0.5) * bbWidth,
                y + random.nextDouble() * bbHeight,
                z + (random.nextDouble() - 0.5) * bbWidth,
                0.0, 0.05, 0.0
            )
        }
    }

    override fun doHurtTarget(target: Entity): Boolean {
        val result = super.doHurtTarget(target)
        if (result && target is LivingEntity) {
            target.igniteForSeconds(5f)

            if (isEnraged() && random.nextFloat() < 0.3f) {
                target.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 100, 0))
            }
        }
        return result
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (source.`is`(DamageTypeTags.IS_FIRE)) {
            return false
        }

        if (source.`is`(DamageTypes.DROWN)) {
            return super.hurt(source, amount * 1.5f)
        }

        return super.hurt(source, amount)
    }

    override fun getAmbientSound(): SoundEvent {
        return SoundEvents.BLAZE_AMBIENT
    }

    override fun getHurtSound(damageSource: DamageSource): SoundEvent {
        return SoundEvents.BLAZE_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.BLAZE_DEATH
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putBoolean("Enraged", isEnraged())
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        setEnraged(compound.getBoolean("Enraged"))
    }

    companion object {
        private val BREATHING_FIRE: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(DemonEntity::class.java, EntityDataSerializers.BOOLEAN)

        private val ENRAGED: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(DemonEntity::class.java, EntityDataSerializers.BOOLEAN)

        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
        }
    }
}