package dev.sterner.witchery.content.entity

import dev.sterner.witchery.content.block.ritual.GoldenChalkBlock
import dev.sterner.witchery.core.registry.WitcheryBlocks
import dev.sterner.witchery.features.coven.CovenHandler
import dev.sterner.witchery.core.registry.WitcheryEntityTypes
import dev.sterner.witchery.core.registry.WitcheryItems
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
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
    private var homePos: BlockPos? = null

    init {
        setPersistenceRequired()
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(0, FindHomeGoal(this))
        goalSelector.addGoal(1, RitualAttendanceGoal(this, 1.0))
        goalSelector.addGoal(2, FloatGoal(this))
        goalSelector.addGoal(3, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(4, HomeWanderGoal(this, 1.0))
        goalSelector.addGoal(5, HomeWanderFarGoal(this, 0.8))
        goalSelector.addGoal(5, RandomLookAroundGoal(this))
    }

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val itemStack = player.getItemInHand(hand)

        if (itemStack.`is`(WitcheryItems.DEMON_HEART.get()) && !getHasDemonHeart()) {
            if (!level().isClientSide) {
                setHasDemonHeart(true)
                if (!player.isCreative) {
                    itemStack.shrink(1)
                }

                val serverLevel = level() as ServerLevel
                serverLevel.sendParticles(
                    ParticleTypes.HEART,
                    this.x,
                    this.y + this.bbHeight + 0.5,
                    this.z,
                    10,
                    0.4,
                    0.2,
                    0.4,
                    0.0
                )

                level().playSound(
                    null,
                    this.blockPosition(),
                    SoundEvents.WITCH_CELEBRATE,
                    SoundSource.NEUTRAL,
                    1.0f,
                    1.0f
                )
            }
            return InteractionResult.SUCCESS
        }

        return super.mobInteract(player, hand)
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
                    setLastRitualPos(Optional.empty())
                    ritualCompleted = true
                }
            }
        }

        if (getIsCoven() && !isPersistenceRequired) {
            if (ritualCompleted) {
                despawnTimer -= 2
            } else {
                despawnTimer--
            }

            if (despawnTimer <= 0) {
                if (ownerUuid != null) {
                    val owner = level().getPlayerByUUID(ownerUuid!!)
                    if (owner is ServerPlayer) {
                        CovenHandler.returnWitchToCoven(owner, this)
                    }
                }
                discard()
            }
        }

        super.aiStep()
    }

    override fun die(damageSource: DamageSource) {
        if (getIsCoven() && ownerUuid != null && !level().isClientSide) {
            val owner = level().getPlayerByUUID(ownerUuid!!)
            if (owner is ServerPlayer) {
                CovenHandler.handleWitchDeath(owner, this)
            }
        }

        super.die(damageSource)
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        val result = super.hurt(source, amount)

        if (result && getIsCoven() && ownerUuid != null && !level().isClientSide) {
            val owner = level().getPlayerByUUID(ownerUuid!!)
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
        builder.define(HAS_DEMON_HEART, false)
    }

    fun getLastRitualPos(): Optional<BlockPos> = entityData.get(LAST_RITUAL_POS)

    fun setLastRitualPos(ritualPos: Optional<BlockPos>) {
        entityData.set(LAST_RITUAL_POS, ritualPos)
        lastRitualPosInternal = ritualPos
    }

    fun getIsCoven(): Boolean = entityData.get(IS_COVEN)

    fun setIsCoven(isCoven: Boolean) {
        entityData.set(IS_COVEN, isCoven)
    }

    fun getHasDemonHeart(): Boolean = entityData.get(HAS_DEMON_HEART)

    fun setHasDemonHeart(hasDemonHeart: Boolean) {
        entityData.set(HAS_DEMON_HEART, hasDemonHeart)
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
        if (lastRitualPosInternal.isPresent) {
            compound.put("LastRitualPos", NbtUtils.writeBlockPos(lastRitualPosInternal.get()))
        }
        homePos?.let { compound.put("HomePos", NbtUtils.writeBlockPos(it)) }
        compound.putInt("DespawnTimer", despawnTimer)
        compound.putBoolean("IsCoven", getIsCoven())
        compound.putBoolean("HasDemonHeart", getHasDemonHeart())
        compound.putBoolean("RitualCompleted", ritualCompleted)
        ownerUuid?.let { compound.putUUID("OwnerUUID", it) }
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        super.readAdditionalSaveData(compound)

        if (compound.contains("LastRitualPos")) {
            lastRitualPosInternal = NbtUtils.readBlockPos(compound, "LastRitualPos")
            setLastRitualPos(lastRitualPosInternal)
        }
        if (compound.contains("HomePos")) {
            homePos = NbtUtils.readBlockPos(compound, "HomePos").orElse(null)
        }
        if (compound.contains("DespawnTimer")) {
            despawnTimer = compound.getInt("DespawnTimer")
        }
        if (compound.contains("IsCoven")) {
            setIsCoven(compound.getBoolean("IsCoven"))
        }
        if (compound.contains("HasDemonHeart")) {
            setHasDemonHeart(compound.getBoolean("HasDemonHeart"))
        }
        if (compound.contains("RitualCompleted")) {
            ritualCompleted = compound.getBoolean("RitualCompleted")
        }
        if (compound.hasUUID("OwnerUUID")) {
            ownerUuid = compound.getUUID("OwnerUUID")
        }
    }
    fun setHome(pos: BlockPos?) {
        homePos = pos
    }

    fun getHome(): BlockPos? = homePos

    companion object {
        val LAST_RITUAL_POS: EntityDataAccessor<Optional<BlockPos>> = SynchedEntityData.defineId(
            CovenWitchEntity::class.java, EntityDataSerializers.OPTIONAL_BLOCK_POS
        )
        val IS_COVEN: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            CovenWitchEntity::class.java, EntityDataSerializers.BOOLEAN
        )
        val HAS_DEMON_HEART: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            CovenWitchEntity::class.java, EntityDataSerializers.BOOLEAN
        )

        fun createAttributes(): AttributeSupplier.Builder {
            return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.MOVEMENT_SPEED, 0.25)
                .add(Attributes.FOLLOW_RANGE, 48.0)
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
        private val desiredDistance: Double = 4.0

        init {
            this.speed = speed
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK))
        }

        override fun canUse(): Boolean {
            if (!witch.getIsCoven()) return false

            val ritualPos = witch.getLastRitualPos()
            if (!ritualPos.isPresent) return false

            ritual = ritualPos.get()
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

    class HomeWanderGoal(
        private val witch: CovenWitchEntity,
        private val speed: Double
    ) : Goal() {
        private val pathFinder: PathNavigation = witch.navigation
        private val wanderRadius = 16.0
        private val maxDistanceFromHome = 24.0

        init {
            setFlags(EnumSet.of(Flag.MOVE))
        }

        override fun canUse(): Boolean {
            if (witch.random.nextFloat() > 0.02f) return false

            if (witch.getIsCoven() && witch.getLastRitualPos().isPresent) {
                return false
            }

            val home = witch.getHome() ?: return false

            if (!witch.level().getBlockState(home).`is`(WitcheryBlocks.POTTED_ROWAN_SAPLING.get())) {
                witch.setHome(null)
                return false
            }

            val distance = witch.position().distanceTo(Vec3.atCenterOf(home))
            return distance < maxDistanceFromHome
        }

        override fun start() {
            val home = witch.getHome() ?: return

            val angle = witch.random.nextDouble() * Math.PI * 2
            val distance = 4.0 + witch.random.nextDouble() * (wanderRadius - 4.0)
            val targetX = home.x + 0.5 + kotlin.math.cos(angle) * distance
            val targetZ = home.z + 0.5 + kotlin.math.sin(angle) * distance

            pathFinder.moveTo(targetX, home.y.toDouble(), targetZ, speed)
        }

        override fun canContinueToUse(): Boolean {
            return !pathFinder.isDone
        }

        override fun stop() {
            pathFinder.stop()
        }
    }


    class FindHomeGoal(
        private val witch: CovenWitchEntity
    ) : Goal() {
        private var searchCooldown = 0
        private val searchRadius = 32.0

        init {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK))
        }

        override fun canUse(): Boolean {
            if (witch.getHome() != null) return false

            searchCooldown--
            if (searchCooldown > 0) return false

            return true
        }

        override fun start() {
            findRowanSapling()
            searchCooldown = 200
        }

        private fun findRowanSapling() {
            val witchPos = witch.blockPosition()

            for (pos in BlockPos.betweenClosed(
                witchPos.offset(-searchRadius.toInt(), -8, -searchRadius.toInt()),
                witchPos.offset(searchRadius.toInt(), 8, searchRadius.toInt())
            )) {
                if (witch.level().getBlockState(pos).`is`(WitcheryBlocks.POTTED_ROWAN_SAPLING.get())) {
                    witch.setHome(pos.immutable())
                    return
                }
            }
        }
    }

    class HomeWanderFarGoal(
        private val witch: CovenWitchEntity,
        private val speed: Double
    ) : Goal() {
        private val pathFinder: PathNavigation = witch.navigation
        private val farWanderRadius = 40.0
        private val maxDistanceFromHome = 48.0

        init {
            setFlags(EnumSet.of(Flag.MOVE))
        }

        override fun canUse(): Boolean {
            if (witch.random.nextFloat() > 0.002f) return false

            if (witch.getIsCoven() && witch.getLastRitualPos().isPresent) {
                return false
            }

            val home = witch.getHome() ?: return false

            if (!witch.level().getBlockState(home).`is`(WitcheryBlocks.POTTED_ROWAN_SAPLING.get())) {
                witch.setHome(null)
                return false
            }

            val distance = witch.position().distanceTo(Vec3.atCenterOf(home))
            return distance < maxDistanceFromHome
        }

        override fun start() {
            val home = witch.getHome() ?: return

            val angle = witch.random.nextDouble() * Math.PI * 2
            val distance = 20.0 + witch.random.nextDouble() * (farWanderRadius - 20.0)
            val targetX = home.x + 0.5 + cos(angle) * distance
            val targetZ = home.z + 0.5 + sin(angle) * distance

            pathFinder.moveTo(targetX, home.y.toDouble(), targetZ, speed)
        }

        override fun canContinueToUse(): Boolean {
            return !pathFinder.isDone
        }

        override fun stop() {
            pathFinder.stop()
        }
    }
}