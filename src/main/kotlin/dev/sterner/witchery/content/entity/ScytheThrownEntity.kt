package dev.sterner.witchery.content.entity


import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.ThrowableProjectile
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import kotlin.math.cos
import kotlin.math.sin

class ScytheThrownEntity : ThrowableProjectile {

    companion object {
        private val DATA_ROTATION_YAW: EntityDataAccessor<Float> =
            SynchedEntityData.defineId(ScytheThrownEntity::class.java, EntityDataSerializers.FLOAT)
        private val DATA_RETURNING: EntityDataAccessor<Boolean> =
            SynchedEntityData.defineId(ScytheThrownEntity::class.java, EntityDataSerializers.BOOLEAN)
    }

    private var ownerUUID: java.util.UUID? = null
    private var ticksExisted = 0
    private val maxDistance = 32.0
    private val maxLifetime = 100
    private var lastHitEntities = mutableSetOf<Int>()

    constructor(level: Level) : super(WitcheryEntityTypes.SCYTHE_THROWN.get(), level)

    constructor(level: Level, owner: LivingEntity) : super(WitcheryEntityTypes.SCYTHE_THROWN.get(), owner, level) {
        this.ownerUUID = owner.uuid
        this.setPos(owner.x, owner.eyeY - 0.1, owner.z)
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(DATA_ROTATION_YAW, 0f)
        builder.define(DATA_RETURNING, false)
    }

    fun getRotationYaw(): Float = entityData.get(DATA_ROTATION_YAW)

    private fun setRotationYaw(yaw: Float) {
        entityData.set(DATA_ROTATION_YAW, yaw)
    }

    fun isReturning(): Boolean = entityData.get(DATA_RETURNING)

    private fun setReturning(returning: Boolean) {
        entityData.set(DATA_RETURNING, returning)
    }

    override fun tick() {
        super.tick()
        ticksExisted++

        setRotationYaw((getRotationYaw() + 30f) % 360f)

        if (level() is ServerLevel) {
            val serverLevel = level() as ServerLevel

            serverLevel.sendParticles(
                ParticleTypes.SOUL,
                x, y, z,
                1, 0.0, 0.0, 0.0, 0.0
            )

            if (random.nextFloat() < 0.3f) {
                serverLevel.sendParticles(
                    ParticleTypes.SMOKE,
                    x, y, z,
                    1, 0.0, 0.0, 0.0, 0.0
                )
            }
        }

        val owner = getOwner()

        if (!isReturning() && owner != null) {
            val distanceToOwner = distanceToSqr(owner)

            if (distanceToOwner > maxDistance * maxDistance || ticksExisted > maxLifetime / 2) {
                setReturning(true)
                lastHitEntities.clear()
            }
        }

        if (isReturning() && owner != null) {
            val toOwner = owner.position().add(0.0, owner.eyeY - y, 0.0).subtract(position())
            val distance = toOwner.length()

            if (distance < 1.0) {
                playSound(SoundEvents.ITEM_PICKUP, 1.0f, 1.0f)
                discard()
                return
            }

            val speed = 0.5 + (1.0 - (distance / maxDistance)) * 0.5
            val direction = toOwner.normalize().scale(speed)
            setDeltaMovement(direction)
        }

        checkEntityCollisions()

        if (ticksExisted > maxLifetime) {
            discard()
        }
    }

    private fun checkEntityCollisions() {
        val aabb = AABB(
            x - 1.0, y - 0.5, z - 1.0,
            x + 1.0, y + 0.5, z + 1.0
        )

        val entities = level().getEntities(this, aabb) { entity ->
            entity is LivingEntity &&
                    entity != getOwner() &&
                    entity.isAlive &&
                    !lastHitEntities.contains(entity.id)
        }

        for (entity in entities) {
            if (entity is LivingEntity) {
                onHitEntity(EntityHitResult(entity))
                lastHitEntities.add(entity.id)

                level().playSound(
                    null, x, y, z,
                    SoundEvents.PLAYER_ATTACK_SWEEP,
                    SoundSource.HOSTILE,
                    1.0f, 0.8f + random.nextFloat() * 0.4f
                )
            }
        }
    }

    override fun onHitEntity(result: EntityHitResult) {
        super.onHitEntity(result)

        if (level().isClientSide) return

        val entity = result.entity
        val owner = getOwner()

        if (entity is LivingEntity && entity != owner) {
            val baseDamage = if (owner is LivingEntity) {
                owner.maxHealth * 0.1f
            } else {
                15f
            }

            val damageSource = if (owner != null) {
                level().damageSources().mobAttack(owner as LivingEntity)
            } else {
                level().damageSources().magic()
            }

            entity.hurt(damageSource, baseDamage)

            val knockbackStrength = 0.5
            val angle = Math.atan2(entity.z - z, entity.x - x)
            entity.knockback(
                knockbackStrength,
                -cos(angle),
                -sin(angle)
            )

            if (level() is ServerLevel) {
                val serverLevel = level() as ServerLevel
                serverLevel.sendParticles(
                    ParticleTypes.SWEEP_ATTACK,
                    entity.x, entity.y + entity.bbHeight / 2, entity.z,
                    3, 0.5, 0.5, 0.5, 0.0
                )

                serverLevel.sendParticles(
                    ParticleTypes.DAMAGE_INDICATOR,
                    entity.x, entity.y + entity.bbHeight / 2, entity.z,
                    5, 0.3, 0.3, 0.3, 0.0
                )
            }
        }
    }

    override fun onHitBlock(result: BlockHitResult) {
        super.onHitBlock(result)

        if (!isReturning()) {
            setReturning(true)

            level().playSound(
                null, x, y, z,
                SoundEvents.ANVIL_LAND,
                SoundSource.HOSTILE,
                0.5f, 1.5f
            )

            if (level() is ServerLevel) {
                val serverLevel = level() as ServerLevel
                serverLevel.sendParticles(
                    ParticleTypes.CRIT,
                    x, y, z,
                    10, 0.2, 0.2, 0.2, 0.1
                )
            }
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putInt("TicksExisted", ticksExisted)
        compound.putBoolean("Returning", isReturning())
        ownerUUID?.let { compound.putUUID("OwnerUUID", it) }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        ticksExisted = compound.getInt("TicksExisted")
        setReturning(compound.getBoolean("Returning"))
        if (compound.hasUUID("OwnerUUID")) {
            ownerUUID = compound.getUUID("OwnerUUID")
        }
    }

    override fun getOwner(): Entity? {
        return ownerUUID?.let { uuid ->
            if (level() is ServerLevel) {
                (level() as ServerLevel).getEntity(uuid)
            } else {
                super.getOwner()
            }
        } ?: super.getOwner()
    }

    override fun isPickable(): Boolean = false

    override fun hurt(source: DamageSource, amount: Float): Boolean = false
}