package dev.sterner.witchery.entity

import dev.sterner.witchery.api.EntityChainInterface
import dev.sterner.witchery.handler.ChainManager
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

// ChainEntity.kt
class ChainEntity(level: Level) : Entity(WitcheryEntityTypes.CHAIN.get(), level) {

    private var life = 0
    private var targetEntityId: Int = -1
    private var targetEntity: Entity? = null
    private var locked = false
    private var lockedPos: Vec3? = null

    // Synced data indices
    companion object {
        private val TARGET_ENTITY = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.INT)
        private val LOCKED = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.BOOLEAN)
        private val LOCKED_POS_X = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.FLOAT)
        private val LOCKED_POS_Y = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.FLOAT)
        private val LOCKED_POS_Z = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.FLOAT)
    }

    override fun tick() {
        super.tick()

        // Get target entity if not already tracked
        if (targetEntity == null && targetEntityId != -1) {
            targetEntity = level().getEntity(targetEntityId)
        }

        // Apply chain restraint to target entity
        targetEntity?.let { target ->
            if (target is LivingEntity && target is EntityChainInterface) {
                (target as EntityChainInterface).`witchery$restrainMovement`(this)
            }
        }

        // Update life
        if (life > 0) {
            life--
            if (life <= 0) {
                targetEntity?.let { target ->
                    ChainManager.releaseEntity(target)
                }
                discard()
            }
        }
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(TARGET_ENTITY, -1)
        builder.define(LOCKED, false)
        builder.define(LOCKED_POS_X, 0f)
        builder.define(LOCKED_POS_Y, 0f)
        builder.define(LOCKED_POS_Z, 0f)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        this.life = compound.getShort("life").toInt()
        this.targetEntityId = compound.getInt("targetEntityId")
        this.locked = compound.getBoolean("locked")

        if (locked) {
            val x = compound.getDouble("lockedPosX")
            val y = compound.getDouble("lockedPosY")
            val z = compound.getDouble("lockedPosZ")
            this.lockedPos = Vec3(x, y, z)
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putShort("life", life.toShort())
        compound.putInt("targetEntityId", targetEntityId)
        compound.putBoolean("locked", locked)

        lockedPos?.let { pos ->
            compound.putDouble("lockedPosX", pos.x)
            compound.putDouble("lockedPosY", pos.y)
            compound.putDouble("lockedPosZ", pos.z)
        }
    }

    fun setTargetEntity(entity: Entity) {
        this.targetEntity = entity
        this.targetEntityId = entity.id
        entityData.set(TARGET_ENTITY, entity.id)
    }


    fun getTargetEntity(): Entity? {
        if (targetEntity != null) {
            return targetEntity
        }
        val id = entityData.get(TARGET_ENTITY)
        if (id != -1) {
            targetEntity = level().getEntity(id)
            return targetEntity
        }

        return null
    }

    fun lockPosition(pos: Vec3) {
        this.locked = true
        this.lockedPos = pos
        entityData.set(LOCKED, true)
        entityData.set(LOCKED_POS_X, pos.x.toFloat())
        entityData.set(LOCKED_POS_Y, pos.y.toFloat())
        entityData.set(LOCKED_POS_Z, pos.z.toFloat())
    }

    fun lockPosition() {
        lockPosition(position())
    }

    fun getLockedPosition(): Vec3? {
        val isLocked = entityData.get(LOCKED)

        if (!isLocked) {
            return null
        }

        if (lockedPos == null) {
            val x = entityData.get(LOCKED_POS_X)
            val y = entityData.get(LOCKED_POS_Y)
            val z = entityData.get(LOCKED_POS_Z)
            lockedPos = Vec3(x.toDouble(), y.toDouble(), z.toDouble())
        }

        return lockedPos
    }

    fun setLife(ticks: Int) {
        this.life = ticks
    }
}