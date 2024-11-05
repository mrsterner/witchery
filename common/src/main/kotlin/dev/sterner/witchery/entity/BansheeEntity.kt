package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.tags.DamageTypeTags
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.*

class BansheeEntity(level: Level) : Monster(WitcheryEntityTypes.BANSHEE.get(), level) {

    init {
        this.moveControl = GhostMoveControl(this)
    }

    override fun registerGoals() {
        goalSelector.addGoal(1, RandomFloatAroundGoal(this))
    }

    companion object {
        fun createAttributes(): AttributeSupplier.Builder? {
            return createMobAttributes()
                .add(Attributes.FLYING_SPEED, 0.35)
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
        }
    }

    override fun isInvulnerableTo(source: DamageSource): Boolean {
        return source.`is`(DamageTypeTags.IS_PROJECTILE) || super.isInvulnerableTo(source)
    }

    class GhostMoveControl(private val ghost: BansheeEntity) : MoveControl(
        ghost
    ) {
        private var floatDuration = 0

        override fun tick() {
            if (this.operation == Operation.MOVE_TO) {
                if (floatDuration-- <= 0) {
                    this.floatDuration = this.floatDuration + ghost.random.nextInt(5) + 2
                    var vec3 = Vec3(
                        this.wantedX - ghost.x,
                        this.wantedY - ghost.y,
                        this.wantedZ - ghost.z
                    )
                    val d = vec3.length()
                    vec3 = vec3.normalize()
                    if (this.canReach(vec3, Mth.ceil(d))) {
                        ghost.deltaMovement = ghost.deltaMovement.add(vec3.scale(0.1))
                    } else {
                        this.operation = Operation.WAIT
                    }
                }
            }
        }

        private fun canReach(pos: Vec3, length: Int): Boolean {
            var aABB = ghost.boundingBox

            for (i in 1 until length) {
                aABB = aABB.move(pos)
                if (!ghost.level().noCollision(this.ghost, aABB)) {
                    return false
                }
            }

            return true
        }
    }

    class RandomFloatAroundGoal(private val bansheeEntity: BansheeEntity) : Goal() {
        init {
            this.flags = EnumSet.of(Flag.MOVE)
        }

        override fun canUse(): Boolean {
            val moveControl = bansheeEntity.moveControl
            if (!moveControl.hasWanted()) {
                return true
            } else {
                val d = moveControl.wantedX - bansheeEntity.x
                val e = moveControl.wantedY - bansheeEntity.y
                val f = moveControl.wantedZ - bansheeEntity.z
                val g = d * d + e * e + f * f
                return g < 1.0 || g > 3600.0
            }
        }

        override fun canContinueToUse(): Boolean {
            return false
        }

        override fun start() {
            val randomSource = bansheeEntity.random
            val d = bansheeEntity.x + ((randomSource.nextFloat() * 2.0f - 1.0f) * 16.0f).toDouble()
            val e = bansheeEntity.y + ((randomSource.nextFloat() * 2.0f - 1.0f) * 16.0f).toDouble()
            val f = bansheeEntity.z + ((randomSource.nextFloat() * 2.0f - 1.0f) * 16.0f).toDouble()
            bansheeEntity.moveControl.setWantedPosition(d, e, f, 1.0)
        }
    }
}