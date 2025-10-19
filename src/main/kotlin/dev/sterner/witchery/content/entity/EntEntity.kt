package dev.sterner.witchery.content.entity

import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.ByIdMap
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal
import net.minecraft.world.entity.ai.goal.RandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.Level
import java.util.function.IntFunction
import kotlin.math.max

@Suppress("DEPRECATION")
class EntEntity(level: Level) : Monster(WitcheryEntityTypes.ENT.get(), level) {

    private var attackAnimationTick = 0

    init {
        this.setPersistenceRequired()
    }

    override fun registerGoals() {
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, false))

        goalSelector.addGoal(5, RandomStrollGoal(this, 0.8))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 3.0f, 1.0f))
        goalSelector.addGoal(4, LookAtPlayerGoal(this, Mob::class.java, 8.0f))
        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(
            2, NearestAttackableTargetGoal(
                this,
                Player::class.java, true
            )
        )
        targetSelector.addGoal(3, NearestAttackableTargetGoal(this, Villager::class.java, true))

        super.registerGoals()
    }

    override fun aiStep() {
        super.aiStep()
        if (this.attackAnimationTick > 0) {
            --this.attackAnimationTick
        }
    }

    override fun handleEntityEvent(id: Byte) {
        if (id.toInt() == 4) {
            this.attackAnimationTick = 10
        } else {
            super.handleEntityEvent(id)
        }
    }

    private fun getAttackDamage(): Float {
        return getAttributeValue(Attributes.ATTACK_DAMAGE).toFloat()
    }

    override fun doHurtTarget(target: Entity): Boolean {
        this.attackAnimationTick = 10
        level().broadcastEntityEvent(this, 4.toByte())
        val damage: Float = this.getAttackDamage()
        val randomDamage = if (damage.toInt() > 0) damage / 2.0f + random.nextInt(damage.toInt()).toFloat() else damage
        val damageSource = damageSources().mobAttack(this)
        val bl = target.hurt(damageSource, randomDamage)
        if (bl) {
            val knockback: Double = if (target is LivingEntity) {
                target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)
            } else {
                0.0
            }

            val e = max(0.0, 1.0 - knockback)
            target.deltaMovement = target.deltaMovement.add(0.0, 0.4 * e, 0.0)
            val level = this.level()
            if (level is ServerLevel) {
                EnchantmentHelper.doPostAttackEffects(level, target, damageSource)
            }
        }
        return bl
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

    fun getAttackAnimationTick(): Int {
        return this.attackAnimationTick
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }

        val DATA_TYPE_ID: EntityDataAccessor<Int> =
            SynchedEntityData.defineId(EntEntity::class.java, EntityDataSerializers.INT)
    }


    enum class Type(val id: Int, private val inName: String) : StringRepresentable {
        ROWAN(0, "rowan"),
        ALDER(1, "alder"),
        HAWTHORN(2, "hawthorn");

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