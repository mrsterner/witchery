package dev.sterner.witchery.entity

import dev.sterner.witchery.api.EntityChainInterface
import dev.sterner.witchery.handler.ChainManager
import dev.sterner.witchery.payload.SyncBarkS2CPacket
import dev.sterner.witchery.payload.SyncChainS2CPayload
import dev.sterner.witchery.registry.WitcheryEntityTypes
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import java.util.*

class ChainEntity(level: Level) : Entity(WitcheryEntityTypes.CHAIN.get(), level) {

    private var life = 0
    private var targetEntityId: Optional<UUID> = Optional.empty<UUID>()
    private var targetEntity: Entity? = null

    companion object {
        private val TARGET_ENTITY = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.OPTIONAL_UUID)
    }

    init {
        noPhysics = true
    }

    override fun tick() {
        super.tick()

        if (targetEntity == null && targetEntityId.isPresent && level() is ServerLevel) {
            targetEntity = (level() as ServerLevel).getEntity(targetEntityId.get())
            targetEntity?.let { sync(it) }
        }

        targetEntity?.let { target ->
            if (target is LivingEntity && target is EntityChainInterface) {
                (target as EntityChainInterface).`witchery$restrainMovement`(this)
            }
            targetEntity?.let { sync(it) }
        }

        if (life > 0) {
            life--
            if (life <= 0) {
                targetEntity?.let { target ->
                    ChainManager.tryReleaseEntity(this, target)
                }
                discard()
            }
        }
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(TARGET_ENTITY, Optional.empty())
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        this.life = compound.getShort("life").toInt()
        if (compound.contains("targetEntityId")) {
            this.targetEntityId = Optional.of(compound.getUUID("targetEntityId"))
        }
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putShort("life", life.toShort())
        if (targetEntityId.isPresent) {
            compound.putUUID("targetEntityId", targetEntityId.get())
        }

    }

    fun setTargetEntity(entity: Entity) {
        this.targetEntity = entity
        this.targetEntityId = Optional.of(entity.uuid)
        entityData.set(TARGET_ENTITY, Optional.of(entity.uuid))
        sync(entity)
    }

    fun sync(entity: Entity){
        if (entity.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(entity.level() as ServerLevel, entity.blockPosition(), SyncChainS2CPayload(this, entity))
        }
    }

    fun getTargetEntity(): Entity? {
        if (targetEntity != null) {
            return targetEntity
        }
        val id = entityData.get(TARGET_ENTITY)
        if (id.isPresent && level() is ServerLevel) {
            targetEntity = (level() as ServerLevel).getEntity(id.get())
            return targetEntity
        }

        return null
    }

    fun setLife(ticks: Int) {
        this.life = ticks
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        return false
    }

    override fun isPushable(): Boolean {
        return false
    }

    override fun push(dx: Double, dy: Double, dz: Double) {
        // No movement from pushing
    }

    override fun push(entity: Entity) {
        // No pushing interaction with other entities
    }

    // Don't collide with anything
    override fun canBeCollidedWith(): Boolean {
        return false
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)

        if (key == TARGET_ENTITY) {
            val uuid = entityData.get(TARGET_ENTITY)
            if (uuid.isPresent) {
                targetEntityId = uuid
            }
        }
    }
}