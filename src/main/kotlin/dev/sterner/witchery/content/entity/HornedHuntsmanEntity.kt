package dev.sterner.witchery.content.entity

import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.Mth
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.*
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor
import java.util.*
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class HornedHuntsmanEntity(entityType: EntityType<out HornedHuntsmanEntity>, level: Level) :
    Monster(entityType, level) {

    var attackCooldown = 0
        private set
    var spearThrowCooldown = 0
        private set
    var strafingBackwards = false
    var strafingTime = 0

    companion object {
        private val ATTACKING =
            SynchedEntityData.defineId(HornedHuntsmanEntity::class.java, EntityDataSerializers.BOOLEAN)
        private val HAS_SPEAR =
            SynchedEntityData.defineId(HornedHuntsmanEntity::class.java, EntityDataSerializers.BOOLEAN)

        private const val RANGED_ATTACK_INTERVAL = 60
        private const val MELEE_ATTACK_INTERVAL = 20
        private const val SPEAR_RECOVERY_TIME = 100

        fun createAttributes(): AttributeSupplier.Builder {
            return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 150.0)
                .add(Attributes.ATTACK_DAMAGE, 12.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6)
                .add(Attributes.ARMOR, 8.0)
        }
    }

    constructor(level: Level) : this(WitcheryEntityTypes.HORNED_HUNTSMAN.get(), level)

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(ATTACKING, false)
        builder.define(HAS_SPEAR, true)
    }

    override fun registerGoals() {
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, HuntsmanRangedAttackGoal(this))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, true))
        goalSelector.addGoal(5, WaterAvoidingRandomStrollGoal(this, 0.8))
        goalSelector.addGoal(6, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(7, RandomLookAroundGoal(this))

        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(2, NearestAttackableTargetGoal(this, Player::class.java, true))
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        spawnType: MobSpawnType,
        spawnGroupData: SpawnGroupData?
    ): SpawnGroupData? {
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(WitcheryItems.HUNTSMAN_SPEAR.get()))
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0f)
        this.setHasSpear(true)

        return super.finalizeSpawn(level, difficulty, spawnType, spawnGroupData)
    }

    override fun getAmbientSound(): SoundEvent {
        return SoundEvents.RAVAGER_AMBIENT
    }

    override fun getHurtSound(damageSource: DamageSource): SoundEvent {
        return SoundEvents.RAVAGER_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.RAVAGER_DEATH
    }

    override fun tick() {
        super.tick()

        if (attackCooldown > 0) {
            attackCooldown--
        }

        if (spearThrowCooldown > 0) {
            spearThrowCooldown--
        }

        if (!hasSpear() && spearThrowCooldown <= 0 && !level().isClientSide) {
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(WitcheryItems.HUNTSMAN_SPEAR.get()))
            this.setHasSpear(true)
        }
    }

    override fun customServerAiStep() {
        super.customServerAiStep()

        if (isAttacking() && target == null) {
            setAttacking(false)
        }
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        if (source.directEntity is LivingEntity) {
            val attacker = source.directEntity as LivingEntity
            val angleToAttacker = Mth.degreesDifferenceAbs(this.yRot, Mth.wrapDegrees(attacker.yRot))

            if (angleToAttacker > 120) {
                return super.hurt(source, amount * 1.5f)
            }
        }

        return super.hurt(source, amount)
    }

    override fun doHurtTarget(target: Entity): Boolean {
        if (target is LivingEntity && attackCooldown <= 0) {
            attackCooldown = MELEE_ATTACK_INTERVAL

            val result = super.doHurtTarget(target)

            if (result && random.nextFloat() < 0.2f) {
                val effect = when (random.nextInt(3)) {
                    0 -> MobEffects.WEAKNESS
                    1 -> MobEffects.MOVEMENT_SLOWDOWN
                    else -> MobEffects.POISON
                }
                target.addEffect(MobEffectInstance(effect, 100, 1))
            }

            return result
        }
        return false
    }

    fun performRangedAttack(target: LivingEntity) {
        if (!hasSpear() || spearThrowCooldown > 0) return

        val spearEntity = HuntsmanSpearEntity(level(), this, ItemStack(WitcheryItems.HUNTSMAN_SPEAR.get()))

        spearEntity.setPos(this.x, this.getEyeY() - 0.1, this.z)

        val dx = target.x - this.x
        val dy = target.getEyeY() - spearEntity.y
        val dz = target.z - this.z

        val horizontalDistance = sqrt(dx * dx + dz * dz)
        val velocity = 1.6f

        spearEntity.shoot(dx, dy + horizontalDistance * 0.2, dz, velocity, 1.0f)

        this.playSound(SoundEvents.TRIDENT_THROW.value(), 1.0f, 1.0f)

        if (!level().isClientSide) {
            level().addFreshEntity(spearEntity)

            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY)
            this.setHasSpear(false)

            spearThrowCooldown = SPEAR_RECOVERY_TIME
        }
    }

    override fun dropCustomDeathLoot(level: ServerLevel, damageSource: DamageSource, recentlyHit: Boolean) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit)

        this.spawnAtLocation(WitcheryItems.HUNTSMAN_SPEAR.get())
    }

    fun isAttacking(): Boolean {
        return entityData.get(ATTACKING)
    }

    fun setAttacking(attacking: Boolean) {
        entityData.set(ATTACKING, attacking)
    }

    fun hasSpear(): Boolean {
        return entityData.get(HAS_SPEAR)
    }

    fun setHasSpear(hasSpear: Boolean) {
        entityData.set(HAS_SPEAR, hasSpear)
    }

    private class HuntsmanRangedAttackGoal(private val huntsman: HornedHuntsmanEntity) : Goal() {

        private var attackTime = -1
        private var seeTime = 0
        private var targetX = 0.0
        private var targetY = 0.0
        private var targetZ = 0.0

        init {
            this.flags = EnumSet.of(Flag.MOVE, Flag.LOOK)
        }

        override fun canUse(): Boolean {
            val target = huntsman.target
            if (target == null || !target.isAlive) {
                return false
            }

            if (!huntsman.hasSpear() || huntsman.spearThrowCooldown > 0) {
                return false
            }

            val distanceToTarget = huntsman.distanceToSqr(target)
            return distanceToTarget > 16.0 && distanceToTarget < 400.0
        }

        override fun canContinueToUse(): Boolean {
            val target = huntsman.target
            if (target == null || !target.isAlive) {
                return false
            }

            if (!huntsman.hasSpear()) {
                return false
            }

            val distanceToTarget = huntsman.distanceToSqr(target)
            return distanceToTarget > 9.0 && distanceToTarget < 400.0 || !huntsman.navigation.isDone
        }

        override fun start() {
            super.start()
            huntsman.setAttacking(true)
            attackTime = 0
            seeTime = 0
        }

        override fun stop() {
            super.stop()
            huntsman.setAttacking(false)
            attackTime = -1
            seeTime = 0

            huntsman.strafingBackwards = false
            huntsman.strafingTime = 0
        }

        override fun requiresUpdateEveryTick(): Boolean {
            return true
        }

        override fun tick() {
            val target = huntsman.target ?: return

            val distanceToTarget = huntsman.distanceToSqr(target)
            val hasLineOfSight = huntsman.hasLineOfSight(target)

            if (hasLineOfSight) {
                ++seeTime
            } else {
                seeTime = 0
            }

            if (distanceToTarget <= 64.0 && distanceToTarget > 16.0) {
                if (++huntsman.strafingTime >= 20) {
                    huntsman.strafingBackwards = !huntsman.strafingBackwards
                    huntsman.strafingTime = 0
                }
            } else {
                huntsman.strafingBackwards = false
                huntsman.strafingTime = 0
            }

            if (distanceToTarget > 225.0) {
                huntsman.navigation.moveTo(target, 1.0)
            } else if (distanceToTarget < 36.0 && seeTime > 0) {
                val dx = huntsman.x - target.x
                val dz = huntsman.z - target.z
                val norm = sqrt(dx * dx + dz * dz)

                if (norm > 0.01) {
                    targetX = huntsman.x + (dx / norm) * 8.0
                    targetZ = huntsman.z + (dz / norm) * 8.0
                    targetY = huntsman.y
                    huntsman.navigation.moveTo(targetX, targetY, targetZ, 1.0)
                }
            } else if (distanceToTarget >= 36.0 && distanceToTarget <= 64.0 && huntsman.strafingTime > 0) {
                val dx = target.x - huntsman.x
                val dz = target.z - huntsman.z
                val angle = atan2(dz, dx) + (if (huntsman.strafingBackwards) Math.PI / 2 else -Math.PI / 2)

                targetX = huntsman.x + cos(angle) * 6.0
                targetZ = huntsman.z + sin(angle) * 6.0
                targetY = huntsman.y

                huntsman.navigation.moveTo(targetX, targetY, targetZ, 0.9)
            }

            huntsman.lookControl.setLookAt(target, 30.0f, 30.0f)

            if (seeTime > 5) {
                if (attackTime == -1) {
                    attackTime = 0
                }

                if (++attackTime >= RANGED_ATTACK_INTERVAL) {
                    if (distanceToTarget <= 256.0 && huntsman.hasSpear()) {
                        huntsman.performRangedAttack(target)
                        attackTime = 0
                    }
                }
            } else {
                attackTime = -1
            }
        }
    }
}