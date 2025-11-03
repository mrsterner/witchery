package dev.sterner.witchery.content.entity

import dev.sterner.witchery.content.entity.goal.RandomFloatAroundGoal
import dev.sterner.witchery.content.entity.move_control.GhostMoveControl
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.Level


class SpiritEntity(level: Level) : AbstractSpectralEntity(WitcheryEntityTypes.SPIRIT.get(), level) {

    init {
        this.moveControl = GhostMoveControl(this)
    }

    override fun registerGoals() {
        goalSelector.addGoal(1, RandomFloatAroundGoal(this))
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {

            return createMobAttributes()
                .add(Attributes.FLYING_SPEED, 0.35)
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }

        val REVEALED: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            SpiritEntity::class.java, EntityDataSerializers.BOOLEAN
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

    override fun isInvulnerableTo(source: DamageSource): Boolean {
        return source.`is`(DamageTypeTags.IS_PROJECTILE) || super.isInvulnerableTo(source)
    }
}