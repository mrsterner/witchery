package dev.sterner.witchery.content.entity

import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import net.minecraft.world.level.gameevent.GameEvent
import java.util.*
import kotlin.random.Random


class InsanityEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.INSANITY.get(), level) {

    private var oldSwell = 0
    private var swell = 0
    private var maxSwell = 30
    var hasPlayedAggroSound = false
    private var spawnTicks = 0

    override fun registerGoals() {
        this.goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, false))
        this.goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, 1.0))
        this.goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 16.0f))
        this.goalSelector.addGoal(4, RandomLookAroundGoal(this))
        this.goalSelector.addGoal(2, SwellGoal(this))

        this.targetSelector.addGoal(1, NearestAttackableTargetGoal(this, Player::class.java, true))
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(DATA_SWELL_DIR, -1)
        builder.define(DATA_MIMIC, "")
        builder.define(DATA_IS_AGGRESSIVE, false)
    }

    fun getSwelling(partialTicks: Float): Float {
        return Mth.lerp(partialTicks, this.oldSwell.toFloat(), this.swell.toFloat()) / (this.maxSwell - 2).toFloat()
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 0.0)
        }

        val DATA_SWELL_DIR: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            InsanityEntity::class.java, EntityDataSerializers.INT
        )
        val DATA_MIMIC: EntityDataAccessor<String> = SynchedEntityData.defineId(
            InsanityEntity::class.java, EntityDataSerializers.STRING
        )
        val DATA_IS_AGGRESSIVE: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            InsanityEntity::class.java, EntityDataSerializers.BOOLEAN
        )
    }

    override fun tick() {
        spawnTicks++

        if (spawnTicks == 2 && entityData.get(DATA_MIMIC) == InsanityMobType.ENDERMAN.name.lowercase()) {
            entityData.set(DATA_IS_AGGRESSIVE, true)
        }

        if (entityData.get(DATA_MIMIC) == InsanityMobType.ENDERMAN.name.lowercase()) {
            val hasTarget = target != null
            if (hasTarget && !entityData.get(DATA_IS_AGGRESSIVE)) {
                entityData.set(DATA_IS_AGGRESSIVE, true)
                if (!hasPlayedAggroSound) {
                    playSound(SoundEvents.ENDERMAN_SCREAM, 0.7f, 1.0f)
                    hasPlayedAggroSound = true
                }
            } else if (!hasTarget && entityData.get(DATA_IS_AGGRESSIVE) && spawnTicks > 20) {
                entityData.set(DATA_IS_AGGRESSIVE, false)
                hasPlayedAggroSound = false
            }
        }

        if (entityData.get(DATA_MIMIC) == InsanityMobType.CREEPER.name.lowercase()) {
            if (this.isAlive) {
                this.oldSwell = this.swell
                val i: Int = entityData.get(DATA_SWELL_DIR)
                if (i > 0 && this.swell == 0) {
                    this.playSound(SoundEvents.CREEPER_PRIMED, 1.0f, 0.5f)
                    this.gameEvent(GameEvent.PRIME_FUSE)
                }

                this.swell += i
                if (this.swell < 0) {
                    this.swell = 0
                }

                if (this.swell >= this.maxSwell) {
                    this.swell = this.maxSwell
                    level().addParticle(ParticleTypes.EXPLOSION, this.x, this.y + 1.0, this.z, 0.0, 0.0, 0.0)
                    playSound(SoundEvents.GENERIC_EXPLODE.value(), 1.0f, 1.0f)
                    this.remove(RemovalReason.DISCARDED)
                }
            }
        }

        if (entityData.get(DATA_MIMIC) == "zombie") {

            if (Random.nextInt(120) == 0) {
                playMobSound()
            }
        }


        if (Random.nextInt(200) == 0) {
            playMobSound()
        }

        super.tick()
    }

    private fun playMobSound() {
        when (entityData.get(DATA_MIMIC)) {
            "zombie" -> this.makeSound(SoundEvents.ZOMBIE_AMBIENT)
            "skeleton" -> this.makeSound(SoundEvents.SKELETON_AMBIENT)
            "enderman" -> this.makeSound(SoundEvents.ENDERMAN_AMBIENT)
        }
    }

    private fun playerDeathSound() {
        when (entityData.get(DATA_MIMIC)) {
            "zombie" -> makeSound(SoundEvents.ZOMBIE_DEATH)
            "skeleton" -> makeSound(SoundEvents.SKELETON_DEATH)
            "enderman" -> makeSound(SoundEvents.ENDERMAN_DEATH)
            "creeper" -> makeSound(SoundEvents.CREEPER_DEATH)
        }
    }

    override fun doHurtTarget(target: Entity): Boolean {
        return false
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putShort("Fuse", maxSwell.toShort())
        compound.putString("Mimic", entityData.get(DATA_MIMIC))
        compound.putBoolean("IsAggressive", entityData.get(DATA_IS_AGGRESSIVE))
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        if (compound.contains("Fuse", 99)) {
            this.maxSwell = compound.getShort("Fuse").toInt()
        }
        if (compound.contains("Mimic", 99)) {
            entityData.set(DATA_MIMIC, compound.getString("Mimic"))
        }
        if (compound.contains("IsAggressive")) {
            entityData.set(DATA_IS_AGGRESSIVE, compound.getBoolean("IsAggressive"))
        }
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        (0 until 20).forEach { i ->
            this.level().addParticle(
                ParticleTypes.POOF,
                this.x + (Random.nextDouble() - 0.5) * this.bbWidth,
                this.y + Random.nextDouble() * this.bbHeight,
                this.z + (Random.nextDouble() - 0.5) * this.bbWidth,
                0.0, 0.0, 0.0
            )
        }
        playerDeathSound()

        this.remove(RemovalReason.KILLED)
        return true
    }

    override fun isInvulnerableTo(source: DamageSource): Boolean {
        return source.entity !is Player || super.isInvulnerableTo(source)
    }

    override fun isAggressive(): Boolean {
        return entityData.get(DATA_IS_AGGRESSIVE)
    }

    enum class InsanityMobType {
        CREEPER,
        ZOMBIE,
        SKELETON,
        ENDERMAN
    }

    class SwellGoal(private val creeper: InsanityEntity) : Goal() {
        private var target: LivingEntity? = null

        init {
            this.flags = EnumSet.of(Flag.MOVE)
        }

        override fun canUse(): Boolean {
            val bl = creeper.entityData.get(DATA_MIMIC) == InsanityMobType.CREEPER.name.lowercase()
            val livingEntity = creeper.target
            return bl && (creeper.entityData.get(DATA_SWELL_DIR) > 0 || livingEntity != null && creeper.distanceToSqr(
                livingEntity
            ) < 9.0)
        }

        override fun start() {
            creeper.navigation.stop()
            this.target = creeper.target
        }

        override fun stop() {
            this.target = null
        }

        override fun requiresUpdateEveryTick(): Boolean {
            return true
        }

        override fun tick() {
            if (this.target == null) {
                creeper.entityData.set(DATA_SWELL_DIR, -1)
            } else if (creeper.distanceToSqr(this.target!!) > 49.0) {
                creeper.entityData.set(DATA_SWELL_DIR, -1)
            } else if (!creeper.sensing.hasLineOfSight(this.target!!)) {
                creeper.entityData.set(DATA_SWELL_DIR, -1)
            } else {
                creeper.entityData.set(DATA_SWELL_DIR, 1)
            }
        }
    }
}