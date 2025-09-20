package dev.sterner.witchery.entity

import dev.sterner.witchery.registry.WitcheryEntityTypes
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

        val REVEALED: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            SpectreEntity::class.java, EntityDataSerializers.BOOLEAN
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

    class GhostMoveControl(private val ghost: SpectreEntity) : MoveControl(
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

                    if (d > 0.1) {
                        val targetYaw = (Mth.atan2(vec3.z, vec3.x) * (180 / Math.PI)).toFloat() - 90.0f
                        ghost.yRot = this.rotateTowards(ghost.yRot, targetYaw, 25.0f)
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

        private fun rotateTowards(currentYaw: Float, targetYaw: Float, maxTurn: Float): Float {
            val deltaYaw = Mth.wrapDegrees(targetYaw - currentYaw)
            return currentYaw + Mth.clamp(deltaYaw, -maxTurn, maxTurn)
        }
    }

    class RandomFloatAroundGoal(private val spectreEntity: SpectreEntity) : Goal() {

        init {
            this.flags = EnumSet.of(Flag.MOVE)
        }

        override fun canUse(): Boolean {
            val moveControl = spectreEntity.moveControl
            if (!moveControl.hasWanted()) {
                return true
            } else {
                val d = moveControl.wantedX - spectreEntity.x
                val e = moveControl.wantedY - spectreEntity.y
                val f = moveControl.wantedZ - spectreEntity.z
                val g = d * d + e * e + f * f
                return g < 1.0 || g > 3600.0
            }
        }

        override fun canContinueToUse(): Boolean {
            return false
        }

        override fun start() {
            val randomSource = spectreEntity.random
            val d = spectreEntity.x + ((randomSource.nextFloat() * 2.0f - 1.0f) * 16.0f).toDouble()

            val e = if (randomSource.nextFloat() < 0.6) {
                spectreEntity.y - (randomSource.nextFloat() * 8.0f).toDouble()
            } else {
                spectreEntity.y + (randomSource.nextFloat() * 8.0f).toDouble()
            }

            val f = spectreEntity.z + ((randomSource.nextFloat() * 2.0f - 1.0f) * 16.0f).toDouble()
            spectreEntity.moveControl.setWantedPosition(d, e, f, 1.0)
        }
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
                // Done attacking, return
                returning = true
                spectre.target = null
                spectre.moveControl.setWantedPosition(
                    spectre.summonPos.x, spectre.summonPos.y, spectre.summonPos.z, 1.0
                )

                if (spectre.distanceToSqr(spectre.summonPos) < 1.0) {
                    spectre.discard() // Despawn
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