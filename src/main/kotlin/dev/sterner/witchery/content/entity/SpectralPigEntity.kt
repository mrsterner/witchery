package dev.sterner.witchery.content.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.animal.Pig
import net.minecraft.world.level.Level

class SpectralPigEntity(level: Level) : Pig(WitcheryEntityTypes.SPECTRAL_PIG.get(), level) {

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

    override fun isInvulnerableTo(source: DamageSource): Boolean {
        return source.`is`(DamageTypeTags.IS_PROJECTILE) || super.isInvulnerableTo(source)
    }

    companion object {
        val REVEALED: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            SpectralPigEntity::class.java, EntityDataSerializers.BOOLEAN
        )
    }
}