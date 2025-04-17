package dev.sterner.witchery.entity

import dev.sterner.witchery.entity.goal.InterruptWaterAvoidingRandomStrollGoal
import dev.sterner.witchery.entity.goal.LookAtPosGoal
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*

class CovenWitchEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.COVEN_WITCH.get(), level) {

    var lastRitualPos = Optional.empty<BlockPos>()

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(1, InterruptWaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(1, LookAtPosGoal(this))

        goalSelector.addGoal(2, FloatGoal(this))
        goalSelector.addGoal(
            3, LookAtPlayerGoal(
                this,
                Player::class.java, 8.0f
            )
        )
        goalSelector.addGoal(3, RandomLookAroundGoal(this))
    }


    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(LAST_RITUAL_POS, Optional.empty())
    }

    fun getLastRitualPos(): Optional<BlockPos> {
        return entityData.get(LAST_RITUAL_POS)
    }

    fun setLastRitualPos(ritualPos: BlockPos) {
        entityData.set(LAST_RITUAL_POS, Optional.of(ritualPos))
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        if (this.lastRitualPos.isPresent) {
            val tag = NbtUtils.writeBlockPos(this.lastRitualPos.get())
            compound.put("LastRitualPos", tag)
        }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)

        if (compound.contains("LastRitualPos")) {
            lastRitualPos = NbtUtils.readBlockPos(compound, "LastRitualPos")
        }
    }

    companion object {
        val LAST_RITUAL_POS: EntityDataAccessor<Optional<BlockPos>> = SynchedEntityData.defineId(
            CovenWitchEntity::class.java, EntityDataSerializers.OPTIONAL_BLOCK_POS
        )

        fun createAttributes(): AttributeSupplier.Builder {
            return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
        }
    }
}