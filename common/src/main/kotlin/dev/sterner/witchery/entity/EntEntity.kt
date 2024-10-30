package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.RandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.Fox
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import java.util.*
import java.util.function.IntFunction

class EntEntity(level: Level) : Monster(WitcheryEntityTypes.ENT.get(), level) {

    init{
        this.setPersistenceRequired()
    }

    override fun registerGoals() {
        goalSelector.addGoal(5, RandomStrollGoal(this, 0.8))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 3.0f, 1.0f))
        goalSelector.addGoal(4, LookAtPlayerGoal(this, Mob::class.java, 8.0f))
        goalSelector.addGoal(4, MeleeAttackGoal(this, 1.0, false))
        targetSelector.addGoal(5, NearestAttackableTargetGoal(this, Player::class.java, true))
        super.registerGoals()
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(DATA_TYPE_ID, 0)
    }

    fun getVariant(): Type {
        return Type.byId((entityData.get(DATA_TYPE_ID)))
    }

    fun setVariant(variant: Type) {
        entityData.set(DATA_TYPE_ID, variant.id)
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        compound.putString("Type", getVariant().serializedName)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)
        this.setVariant(Type.byName(compound.getString("Type")))
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }

        val DATA_TYPE_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(EntEntity::class.java, EntityDataSerializers.INT);
    }


    enum class Type(val id: Int, val inName: String) : StringRepresentable {
        ROWAN(0, "rowan"),
        ALDER(1, "alder"),
        HAWTHORN(1, "hawthorn");

        override fun getSerializedName(): String {
            return this.inName
        }

        companion object {
            val CODEC: StringRepresentable.EnumCodec<Type> = StringRepresentable.fromEnum { entries.toTypedArray() }
            private val BY_ID: IntFunction<Type> =
                ByIdMap.continuous({ obj: Type -> obj.id }, entries.toTypedArray(), ByIdMap.OutOfBoundsStrategy.ZERO)

            fun byName(name: String?): Type {
                return CODEC.byName(name, ROWAN) as Type
            }

            fun byId(index: Int): Type {
                return BY_ID.apply(index) as Type
            }
        }
    }
}