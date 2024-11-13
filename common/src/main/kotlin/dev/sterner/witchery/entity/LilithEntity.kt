package dev.sterner.witchery.entity

import dev.sterner.witchery.entity.NightmareEntity.Companion.INTANGIBLE
import dev.sterner.witchery.entity.NightmareEntity.Companion.NIGHTMARE_TARGET
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.level.Level
import java.util.*

class LilithEntity(level: Level) : Monster(WitcheryEntityTypes.LILITH.get(), level) {

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(IS_DEFEATED, true)
        super.defineSynchedData(builder)
    }

    override fun save(compound: CompoundTag): Boolean {
        compound.putBoolean("IsDefeated", entityData.get(IS_DEFEATED))
        return super.save(compound)
    }

    override fun load(compound: CompoundTag) {
        entityData.set(IS_DEFEATED, compound.getBoolean("IsDefeated"))
        super.load(compound)
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 200.0)
                .add(Attributes.MOVEMENT_SPEED, 0.4)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }

        val IS_DEFEATED: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            LilithEntity::class.java, EntityDataSerializers.BOOLEAN
        )
    }
}