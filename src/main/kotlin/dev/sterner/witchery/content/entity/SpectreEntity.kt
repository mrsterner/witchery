package dev.sterner.witchery.content.entity

import dev.sterner.witchery.content.entity.goal.RandomFloatAroundGoal
import dev.sterner.witchery.content.entity.move_control.GhostMoveControl
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.DamageTypeTags
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.target.TargetGoal
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.*

class SpectreEntity(level: Level) : AbstractSpectralEntity(WitcheryEntityTypes.SPECTRE.get(), level) {

    val ignoredUUIDs: MutableSet<UUID> = mutableSetOf()
    var summonPos: Vec3 = Vec3.ZERO
    var attackCount: Int = 0

    var attackTicksRemaining = 0

    init {
        this.moveControl = GhostMoveControl(this)
    }

    override fun registerGoals() {
        goalSelector.addGoal(1, RandomFloatAroundGoal(this))
        targetSelector.addGoal(1, TargetNearbyPlayersGoal(this, 32.0))
        goalSelector.addGoal(2, AttackAndReturnGoal(this))

    }

    companion object {
        fun summonSpectre(
            level: ServerLevel,
            pos: BlockPos,
            ignoredUUIDs: Set<UUID> = emptySet()
        ): SpectreEntity {
            val spectre = SpectreEntity(level)

            spectre.setPos(
                pos.x + 0.5,
                pos.y + 1.0,
                pos.z + 0.5
            )

            spectre.ignoredUUIDs.addAll(ignoredUUIDs)
            spectre.summonPos = spectre.position()

            level.addFreshEntity(spectre)

            return spectre
        }

        fun createAttributes(): AttributeSupplier.Builder {

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

    class TargetNearbyPlayersGoal(
        private val spectre: SpectreEntity,
        private val range: Double
    ) : TargetGoal(spectre, false) {

        override fun canUse(): Boolean {
            val level = spectre.level()

            val potentialTargets = level.players().filter { player ->
                !player.isSpectator &&
                        player.uuid !in spectre.ignoredUUIDs &&
                        player.distanceToSqr(spectre) <= range * range
            }

            if (potentialTargets.isNotEmpty()) {
                spectre.target = potentialTargets.random()
                return true
            }

            return false
        }
    }

    class AttackAndReturnGoal(private val spectre: SpectreEntity) : Goal() {

        private var returning = false
        private val maxAttacks = 5

        init {
            this.flags = EnumSet.of(Flag.MOVE, Flag.LOOK)
        }

        override fun canUse(): Boolean {
            return spectre.target != null || returning
        }

        override fun canContinueToUse(): Boolean {
            return !returning || spectre.distanceToSqr(spectre.summonPos) > 1.0
        }

        override fun tick() {
            val target = spectre.target

            if (!returning && target != null && target.isAlive && spectre.attackCount < maxAttacks) {
                spectre.lookControl.setLookAt(target, 30f, 30f)
                spectre.moveControl.setWantedPosition(target.x, target.y, target.z, 1.1)

                if (spectre.boundingBox.inflate(1.2).intersects(target.boundingBox)) {
                    if (spectre.attackTicksRemaining <= 0) {
                        spectre.doHurtTarget(target)
                        spectre.attackCount++
                        spectre.attackTicksRemaining = 20
                    }
                }
            } else {
                returning = true
                spectre.target = null
                spectre.moveControl.setWantedPosition(
                    spectre.summonPos.x, spectre.summonPos.y, spectre.summonPos.z, 1.0
                )

                if (spectre.distanceToSqr(spectre.summonPos) < 1.0) {
                    spectre.discard()
                }
            }

            if (spectre.attackTicksRemaining > 0) spectre.attackTicksRemaining--
        }

        override fun start() {
            returning = false
            spectre.attackCount = 0
            spectre.attackTicksRemaining = 0
        }
    }
}