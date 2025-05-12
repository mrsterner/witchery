package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.FloatGoal
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class DeathEntity(level: Level) : Monster(WitcheryEntityTypes.DEATH.get(), level) {

    private var teleportCooldown = 0

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(1, MeleeAttackGoal(this, 1.0, true))
        goalSelector.addGoal(2, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f))

        targetSelector.addGoal(1, HurtByTargetGoal(this))
        targetSelector.addGoal(2, NearestAttackableTargetGoal(this, Player::class.java, true))
    }

    override fun tick() {
        super.tick()

        if (level().gameTime % 20 == 0L) {
            if (health < maxHealth) {
                heal(1f)
            }
        }

        if (teleportCooldown > 0) {
            teleportCooldown--
        } else {
            val target = target
            if (target is LivingEntity && target.isAlive && random.nextFloat() < 0.05f) {
                attemptTeleportNearTarget(target)
            }
        }
    }

    private fun attemptTeleportNearTarget(target: LivingEntity) {
        if (level() !is ServerLevel) return
        val random = RandomSource.create()
        for (i in 0 until 16) {
            val angle = random.nextDouble() * Math.PI * 2
            val distance = 2.0 + random.nextDouble() * 2.0
            val x = target.x + cos(angle) * distance
            val z = target.z + sin(angle) * distance

            val y = target.y + random.nextDouble() * 2.0 - 1.0
            
            if (teleportDeathTo(x, y, z)) {
                level().playSound(null, xo, yo, zo, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f)
                level().playSound(null, x, y, z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f)
                teleportCooldown = (20 * 10) + random.nextInt(20 * 10)
                break
            }
        }
    }

    private fun teleportDeathTo(x: Double, y: Double, z: Double): Boolean {
        val serverLevel = level() as? ServerLevel ?: return false

        val blockPos = blockPosition().offset(
            Mth.floor(x - this.x),
            Mth.floor(y - this.y),
            Mth.floor(z - this.z)
        )

        if (!serverLevel.isEmptyBlock(blockPos) || !serverLevel.isEmptyBlock(blockPos.above())) {
            return false
        }

        teleportTo(x, y, z)
        return true
    }

    override fun doHurtTarget(target: Entity): Boolean {
        if (target is LivingEntity) {
            val damage = target.maxHealth * 0.15f
            return target.hurt(level().damageSources().mobAttack(this), damage)
        }
        return super.doHurtTarget(target)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val cappedDamage = min(amount, 16.0f)
        return super.hurt(source, cappedDamage)
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder {
            return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, 20.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
        }
    }
}