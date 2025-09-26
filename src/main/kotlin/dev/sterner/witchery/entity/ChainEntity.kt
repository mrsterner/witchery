package dev.sterner.witchery.entity

import dev.sterner.witchery.api.event.ChainEvent
import dev.sterner.witchery.api.interfaces.EntityChainInterface
import dev.sterner.witchery.handler.chain.ChainManager
import dev.sterner.witchery.payload.SyncChainS2CPayload
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerEntity
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.network.PacketDistributor
import java.util.*

class ChainEntity(level: Level) : Entity(WitcheryEntityTypes.CHAIN.get(), level) {

    enum class ChainState {
        EXTENDING,
        CONNECTED,
        RETRACTING,
        FINISHED
    }

    private var life = 0
    private var targetEntityId: Optional<UUID> = Optional.empty<UUID>()
    private var targetEntity: Entity? = null

    private var chainState = ChainState.EXTENDING
    private var chainProgress = 0f
    private var retractProgress = 0f
    private var extensionSpeed = 0.05f
    private var retractionSpeed = 0.03f
    private var pullStrength = 0f
    private var maxLinks = 0

    private var initialDistance = 0.0

    private var headPosition = 0f

    companion object {
        private val TARGET_ENTITY =
            SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.OPTIONAL_UUID)
        private val CHAIN_STATE = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.INT)
        private val CHAIN_PROGRESS = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.FLOAT)
        private val RETRACT_PROGRESS = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.FLOAT)
        private val HEAD_POSITION = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.FLOAT)
        val TYPE = SynchedEntityData.defineId(ChainEntity::class.java, EntityDataSerializers.INT)
    }

    init {
        noPhysics = true
    }

    override fun tick() {
        super.tick()
        if (level().isClientSide) {
            return
        }

        if (targetEntity == null && targetEntityId.isPresent && level() is ServerLevel) {
            targetEntity = (level() as ServerLevel).getEntity(targetEntityId.get())
            targetEntity?.let { sync(it) }
        }

        targetEntity?.let { target ->
            val currentDistance = position().distanceTo(target.position())
            val effectiveLinkLength = 0.35f * 1.5 - 0.15f * 1.5

            if (currentDistance > initialDistance * 1.1) {
                initialDistance = currentDistance
                maxLinks = kotlin.math.ceil(currentDistance / effectiveLinkLength).toInt()
            }
        }

        when (getChainState()) {
            ChainState.EXTENDING -> {
                val headPos = entityData.get(HEAD_POSITION) + extensionSpeed
                entityData.set(HEAD_POSITION, headPos.coerceAtMost(1.0f))

                val chainGrowthSpeed = extensionSpeed * 0.8f
                val progress = entityData.get(CHAIN_PROGRESS) + chainGrowthSpeed
                entityData.set(CHAIN_PROGRESS, progress.coerceAtMost(1.0f))

                targetEntity?.let { target ->
                    val currentDistance = position().distanceTo(target.position())
                    if (currentDistance > initialDistance * 1.5 && headPos > 0.7f) {
                        entityData.set(HEAD_POSITION, (headPos - 0.01f).coerceAtLeast(0.7f))
                    }
                }

                if (headPos >= 1.0f) {
                    setChainState(ChainState.CONNECTED)
                }
            }

            ChainState.CONNECTED -> {
                targetEntity?.let { target ->
                    if (target is LivingEntity && target is EntityChainInterface) {
                        val lockPos = pullStrength == 0f
                        (target as EntityChainInterface).`witchery$restrainMovement`(this, lockPos)
                    }
                }

                if (pullStrength > 0) {
                    setChainState(ChainState.RETRACTING)
                }
            }

            ChainState.RETRACTING -> {
                targetEntity?.let { target ->
                    if (target is LivingEntity) {
                        val origin = position()
                        val targetPos = target.position()
                        val toOrigin = origin.subtract(targetPos)
                        val distance = toOrigin.length()

                        if (this.boundingBox.inflate(.25).intersects(target.boundingBox)) {
                            setChainState(ChainState.FINISHED)
                            return@let
                        }

                        val progressIncrement = if (distance > 2.0) {
                            retractionSpeed * 0.5f
                        } else {
                            retractionSpeed * 0.8f
                        }

                        val progress = entityData.get(RETRACT_PROGRESS) + progressIncrement
                        entityData.set(RETRACT_PROGRESS, progress.coerceAtMost(1.0f))

                        if (progress <= 0.5f) {
                            entityData.set(HEAD_POSITION, 1.0f)
                        } else {
                            val targetDistanceFactor = (distance / (distance + 2.0)).coerceIn(0.0, 1.0)
                            val headPos = targetDistanceFactor.toFloat()
                            entityData.set(HEAD_POSITION, headPos)
                        }

                        val adjusted = Vec3(toOrigin.x, toOrigin.y, toOrigin.z)
                        val maxPullPerTick = 0.75
                        val clamped = adjusted.length().coerceAtMost(maxPullPerTick)
                        val pullVector = adjusted.normalize().scale(clamped * pullStrength * 1.2f)

                        target.deltaMovement = target.deltaMovement.add(pullVector)
                        target.fallDistance = 0f
                    }
                }
            }


            ChainState.FINISHED -> {
                runDiscard(targetEntity)
            }
        }

        if (life > 0) {
            life--
            if (life <= 0) {
                runDiscard(targetEntity)
            }
        }
    }

    private fun runDiscard(targetEntity: Entity?) {
        targetEntity?.let { it.deltaMovement = Vec3.ZERO }
        targetEntity?.let { ChainManager.tryReleaseEntity(this, it) }
        NeoForge.EVENT_BUS.post(ChainEvent.Discard(targetEntity, this))
        discard()
    }

    override fun defineSynchedData(builder: SynchedEntityData.Builder) {
        builder.define(TARGET_ENTITY, Optional.empty())
        builder.define(CHAIN_STATE, ChainState.EXTENDING.ordinal)
        builder.define(CHAIN_PROGRESS, 0f)
        builder.define(RETRACT_PROGRESS, 0f)
        builder.define(HEAD_POSITION, 0f)
        builder.define(TYPE, 0)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        if (compound.hasUUID("TargetEntity")) {
            targetEntityId = Optional.of(compound.getUUID("TargetEntity"))
        }

        life = compound.getInt("Life")
        chainState = ChainState.entries.toTypedArray()[compound.getInt("ChainState")]
        chainProgress = compound.getFloat("ChainProgress")
        retractProgress = compound.getFloat("RetractProgress")
        headPosition = compound.getFloat("HeadPosition")
        extensionSpeed = compound.getFloat("ExtensionSpeed")
        retractionSpeed = compound.getFloat("RetractionSpeed")
        pullStrength = compound.getFloat("PullStrength")
        maxLinks = compound.getInt("MaxLinks")
        initialDistance = compound.getDouble("InitialDistance")
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        targetEntityId.ifPresent { uuid -> compound.putUUID("TargetEntity", uuid) }

        compound.putInt("Life", life)
        compound.putInt("ChainState", chainState.ordinal)
        compound.putFloat("ChainProgress", chainProgress)
        compound.putFloat("RetractProgress", retractProgress)
        compound.putFloat("HeadPosition", headPosition)
        compound.putFloat("ExtensionSpeed", extensionSpeed)
        compound.putFloat("RetractionSpeed", retractionSpeed)
        compound.putFloat("PullStrength", pullStrength)
        compound.putInt("MaxLinks", maxLinks)
        compound.putDouble("InitialDistance", initialDistance)
    }

    override fun getAddEntityPacket(entity: ServerEntity): Packet<ClientGamePacketListener> {
        return ClientboundAddEntityPacket(this, entity)
    }

    fun setTargetEntity(entity: Entity) {
        this.targetEntity = entity
        this.targetEntityId = Optional.of(entity.uuid)
        entityData.set(TARGET_ENTITY, Optional.of(entity.uuid))
        sync(entity)

        initialDistance = position().distanceTo(entity.position())
        val effectiveLinkLength = 0.35f * 1.5 - 0.15f * 1.5
        maxLinks = kotlin.math.ceil(initialDistance / effectiveLinkLength).toInt()
    }

    fun sync(entity: Entity) {
        if (entity.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(
                entity.level() as ServerLevel,
                ChunkPos(entity.blockPosition()),
                SyncChainS2CPayload(this, entity)
            )
        }
    }

    fun getTargetEntity(): Entity? {
        if (targetEntity != null) {
            return targetEntity
        }
        val id = entityData.get(TARGET_ENTITY)
        if (id.isPresent && level() is ServerLevel) {
            targetEntity = (level() as ServerLevel).getEntity(id.get())
            return targetEntity
        }

        return null
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {
        return false
    }

    override fun isPushable(): Boolean {
        return false
    }

    override fun push(dx: Double, dy: Double, dz: Double) {

    }

    override fun push(entity: Entity) {

    }

    override fun canBeCollidedWith(): Boolean {
        return false
    }

    override fun onSyncedDataUpdated(key: EntityDataAccessor<*>) {
        super.onSyncedDataUpdated(key)

        if (key == TARGET_ENTITY) {
            val uuid = entityData.get(TARGET_ENTITY)
            if (uuid.isPresent) {
                targetEntityId = uuid
            }
        }
    }

    fun setLife(ticks: Int) {
        this.life = ticks
    }

    fun setExtensionSpeed(speed: Float) {
        this.extensionSpeed = speed
    }

    fun setRetractionSpeed(speed: Float) {
        this.retractionSpeed = speed
    }

    fun setPullStrength(strength: Float) {
        this.pullStrength = strength
    }

    fun getChainState(): ChainState {
        return ChainState.entries[entityData.get(CHAIN_STATE)]
    }

    fun getChainProgress(): Float {
        return entityData.get(CHAIN_PROGRESS)
    }

    fun getRetractProgress(): Float {
        return entityData.get(RETRACT_PROGRESS)
    }

    fun getHeadPosition(): Float {
        return entityData.get(HEAD_POSITION)
    }

    fun getRawLinkCount(): Float {
        val chainState = getChainState()

        targetEntity?.let { target ->
            val currentDistance = position().distanceTo(target.position())
            val effectiveLinkLength = 0.35f * 1.5 - 0.15f * 1.5
            if (chainState == ChainState.EXTENDING || chainState == ChainState.CONNECTED) {
                val neededLinks = kotlin.math.ceil(currentDistance / effectiveLinkLength).toInt()
                if (neededLinks > maxLinks) {
                    maxLinks = neededLinks
                }
            }
        }

        return when (chainState) {
            ChainState.EXTENDING -> {
                (maxLinks * getChainProgress()).coerceAtLeast(0.1f)
            }

            ChainState.CONNECTED -> maxLinks.toFloat()
            ChainState.RETRACTING -> {
                val baseRetraction = getRetractProgress()
                ((1.0f - baseRetraction) * maxLinks).coerceAtLeast(0.1f)
            }

            ChainState.FINISHED -> 0f
        }
    }

    fun startRetracting() {
        setChainState(ChainState.RETRACTING)
    }

    private fun setChainState(state: ChainState) {
        entityData.set(CHAIN_STATE, state.ordinal)

        when (state) {
            ChainState.EXTENDING -> {
                entityData.set(CHAIN_PROGRESS, 0f)
                entityData.set(HEAD_POSITION, 0f)
            }

            ChainState.RETRACTING -> {
                entityData.set(RETRACT_PROGRESS, 0f)
                entityData.set(HEAD_POSITION, 1.0f)
            }

            else -> {}
        }
    }
}