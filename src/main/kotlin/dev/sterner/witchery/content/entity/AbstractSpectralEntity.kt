package dev.sterner.witchery.content.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.FlyingMob
import net.minecraft.world.entity.MoverType
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

abstract class AbstractSpectralEntity(entityType: EntityType<out FlyingMob>, level: Level) : FlyingMob(
    entityType,
    level
) {

    companion object {
        val REVEALED: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            AbstractSpectralEntity::class.java, EntityDataSerializers.BOOLEAN
        )
    }


    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(REVEALED, false)
        super.defineSynchedData(builder)
    }

    override fun save(compound: CompoundTag): Boolean {
        compound.putBoolean("Revealed", entityData.get(REVEALED))
        return super.save(compound)
    }

    override fun load(compound: CompoundTag) {
        entityData.set(REVEALED, compound.getBoolean("Revealed"))
        super.load(compound)
    }

    override fun move(type: MoverType, pos: Vec3) {
        super.move(type, pos)
        this.checkInsideBlocks()
    }

    override fun tick() {
        this.noPhysics = true
        super.tick()
        this.noPhysics = false

    }

    override fun shouldDespawnInPeaceful(): Boolean {
        return true
    }
}