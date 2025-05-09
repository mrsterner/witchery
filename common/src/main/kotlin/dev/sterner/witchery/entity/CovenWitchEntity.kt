package dev.sterner.witchery.entity

import dev.sterner.witchery.block.ritual.GoldenChalkBlock
import dev.sterner.witchery.entity.goal.InterruptWaterAvoidingRandomStrollGoal
import dev.sterner.witchery.entity.goal.LookAtPosGoal
import dev.sterner.witchery.handler.CovenHandler
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class CovenWitchEntity(level: Level) : PathfinderMob(WitcheryEntityTypes.COVEN_WITCH.get(), level) {

    private var lastRitualPosInternal = Optional.empty<BlockPos>()
    private var despawnTimer = 20 * 60 * 20
    private var ritualCompleted = false
    private var ownerUuid: UUID? = null

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(1, RitualAttendanceGoal(this, 1.0))
        goalSelector.addGoal(2, FloatGoal(this))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(4, RandomStrollGoal(this, 0.8))
        goalSelector.addGoal(5, RandomLookAroundGoal(this))
    }

    override fun aiStep() {
        if (level().isClientSide) {
            super.aiStep()
            return
        }

        if (level().gameTime % 200 == 0L) {
            if (lastRitualPosInternal.isPresent) {
                val pos = lastRitualPosInternal.get()
                if (level().getBlockState(pos).block !is GoldenChalkBlock) {
                    setLastRitualPos(Optional.empty<BlockPos>())
                    ritualCompleted = true
                }
            }
        }

        if (getIsCoven()) {
            if (ritualCompleted) {
                despawnTimer -= 2
            } else {
                despawnTimer--
            }

            if (despawnTimer <= 0) {
                discard()
            }
        }

        super.aiStep()
    }

    override fun die(damageSource: DamageSource) {
        if (getIsCoven() && ownerUuid != null) {
            val owner = level().getPlayerByUUID(ownerUuid)
            if (owner is ServerPlayer) {
                CovenHandler.handleWitchDeath(owner, this)
            }
        }

        super.die(damageSource)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val result = super.hurt(source, amount)

        if (result && getIsCoven() && ownerUuid != null && !level().isClientSide) {
            val owner = level().getPlayerByUUID(ownerUuid)
            if (owner is ServerPlayer) {
                CovenHandler.updateWitchHealth(owner, this)
            }
        }

        return result
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        super.defineSynchedData(builder)
        builder.define(LAST_RITUAL_POS, Optional.empty())
        builder.define(IS_COVEN, false)
    }

    fun getLastRitualPos(): Optional<BlockPos> {
        return entityData.get(LAST_RITUAL_POS)
    }

    fun setLastRitualPos(ritualPos: Optional<BlockPos>) {
        entityData.set(LAST_RITUAL_POS, ritualPos)
        lastRitualPosInternal = ritualPos
    }

    fun getIsCoven(): Boolean {
        return entityData.get(IS_COVEN)
    }

    fun setIsCoven(isCoven: Boolean) {
        entityData.set(IS_COVEN, isCoven)
    }

    fun setRitualCompleted(completed: Boolean) {
        ritualCompleted = completed
    }

    fun setOwner(uuid: UUID?) {
        ownerUuid = uuid
    }

    fun resetDespawnTimer() {
        despawnTimer = 20 * 60 * 20
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        super.addAdditionalSaveData(compound)
        if (this.lastRitualPosInternal.isPresent) {
            val tag = NbtUtils.writeBlockPos(this.lastRitualPosInternal.get())
            compound.put("LastRitualPos", tag)
        }
        compound.putInt("DespawnTimer", despawnTimer)
        compound.putBoolean("IsCoven", getIsCoven())
        compound.putBoolean("RitualCompleted", ritualCompleted)
        ownerUuid?.let { compound.putUUID("OwnerUUID", it) }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)

        if (compound.contains("LastRitualPos")) {
            lastRitualPosInternal = NbtUtils.readBlockPos(compound, "LastRitualPos")
            setLastRitualPos(lastRitualPosInternal)
        }
        if (compound.contains("DespawnTimer")) {
            despawnTimer = compound.getInt("DespawnTimer")
        }
        if (compound.contains("IsCoven")) {
            setIsCoven(compound.getBoolean("IsCoven"))
        }
        if (compound.contains("RitualCompleted")) {
            ritualCompleted = compound.getBoolean("RitualCompleted")
        }
        if (compound.hasUUID("OwnerUUID")) {
            ownerUuid = compound.getUUID("OwnerUUID")
        }
    }

    companion object {
        val LAST_RITUAL_POS: EntityDataAccessor<Optional<BlockPos>> = SynchedEntityData.defineId(
            CovenWitchEntity::class.java, EntityDataSerializers.OPTIONAL_BLOCK_POS
        )
        val IS_COVEN: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            CovenWitchEntity::class.java, EntityDataSerializers.BOOLEAN
        )

        fun createAttributes(): AttributeSupplier.Builder {
            return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
        }
    }

    /**
     * Goal that makes witches attend rituals
     */
    class RitualAttendanceGoal(
        private val witch: CovenWitchEntity,
        speed: Double
    ) : Goal() {
        private val pathFinder: PathNavigation = witch.navigation
        private val speed: Double
        private var timeoutCounter: Int = 0
        private var ritual: BlockPos? = null
        private var desiredDistance: Double = 3.5

        init {
            this.speed = speed
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK))
        }

        override fun canUse(): Boolean {
            if (!witch.getIsCoven()) return false

            val ritualPos = witch.getLastRitualPos()
            if (!ritualPos.isPresent) return false

            this.ritual = ritualPos.get()
            return true
        }

        override fun start() {
            timeoutCounter = 0
        }

        override fun stop() {
            pathFinder.stop()
            ritual = null
        }

        override fun tick() {
            val ritualPos = ritual ?: return

            val dx = ritualPos.x + 0.5 - witch.x
            val dy = ritualPos.y - witch.y
            val dz = ritualPos.z + 0.5 - witch.z
            val distanceSq = dx * dx + dy * dy + dz * dz

            if (distanceSq > desiredDistance * desiredDistance) {
                timeoutCounter--
                if (timeoutCounter <= 0) {
                    timeoutCounter = 10 + witch.random.nextInt(40)

                    val angle = witch.random.nextDouble() * Math.PI * 2
                    val targetX = ritualPos.x + 0.5 + cos(angle) * desiredDistance
                    val targetZ = ritualPos.z + 0.5 + sin(angle) * desiredDistance

                    pathFinder.moveTo(targetX, ritualPos.y.toDouble(), targetZ, speed)
                }
            } else {
                witch.lookAt(
                    EntityAnchorArgument.Anchor.EYES,
                    Vec3(ritualPos.x + 0.5, ritualPos.y + 0.5, ritualPos.z + 0.5)
                )
            }
        }
    }
}