package dev.sterner.witchery.content.block.mirror

import dev.sterner.witchery.WitcheryConfig
import dev.sterner.witchery.features.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionType
import dev.sterner.witchery.features.mirror.MirrorRegistryAttachment
import dev.sterner.witchery.core.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.portal.DimensionTransition
import net.minecraft.world.phys.Vec3
import java.util.UUID

class MirrorBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.MIRROR.get(), MirrorBlock.STRUCTURE.get(), blockPos, blockState) {

    var hasDemon = false
    var isSmallMirror = false
    var pairId: UUID? = null
    var cachedLinkedMirror: GlobalPos? = null
    private var cacheValidUntil: Long = 0
    var mode: Mode = Mode.TELEPORT

    private val entityCooldowns = mutableMapOf<Int, Long>()

    enum class Mode : StringRepresentable {
        NONE,
        DEMONIC,
        TELEPORT,
        POCKET_DIMENSION;

        override fun getSerializedName(): String = name.lowercase()
    }

    companion object {
        const val TELEPORT_COOLDOWN = 20
        const val CACHE_DURATION = 100L
    }

    override fun onLoad() {
        super.onLoad()
        registerInRegistry()
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putBoolean("HasDemon", hasDemon)
        tag.putBoolean("IsSmallMirror", isSmallMirror)
        tag.putString("Mode", mode.serializedName)
        pairId?.let { tag.putUUID("PairId", it) }
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        hasDemon = tag.getBoolean("HasDemon")
        isSmallMirror = tag.getBoolean("IsSmallMirror")
        if (tag.contains("Mode")) {
            mode = Mode.valueOf(tag.getString("Mode").uppercase())
        }
        if (tag.contains("PairId")) {
            pairId = tag.getUUID("PairId")
        }
    }

    fun isOnCooldown(entity: Entity): Boolean {
        val currentTime = level?.gameTime ?: return false
        val lastTeleport = entityCooldowns[entity.id] ?: return false
        return currentTime - lastTeleport < TELEPORT_COOLDOWN
    }

    private fun setCooldown(entity: Entity) {
        entityCooldowns[entity.id] = level?.gameTime ?: 0
    }

    override fun onBreak(player: Player) {
        super.onBreak(player)
        if (level?.isClientSide == false && level is ServerLevel && pairId != null) {
            val globalPos = GlobalPos.of(level!!.dimension(), blockPos)
            val serverLevel = level as ServerLevel

            val pairedMirrorPos = getLinkedMirror()
            MirrorRegistryAttachment.unregisterMirror(serverLevel, globalPos)

            pairedMirrorPos?.let { targetPos ->
                val pairedLevel = serverLevel.server.getLevel(targetPos.dimension())
                val pairedEntity = pairedLevel?.getBlockEntity(targetPos.pos()) as? MirrorBlockEntity
                pairedEntity?.apply {
                    mode = Mode.DEMONIC
                    hasDemon = true
                    pairId = null
                    cachedLinkedMirror = null
                    setChanged()
                }
            }
        }
    }

    fun putPairId(uuid: UUID) {
        this.pairId = uuid
        this.mode = Mode.TELEPORT
        this.cachedLinkedMirror = null
        this.cacheValidUntil = 0
        setChanged()
        registerInRegistry()
    }

    private fun registerInRegistry() {
        if (level is ServerLevel && pairId != null) {
            val globalPos = GlobalPos.of(level!!.dimension(), blockPos)
            MirrorRegistryAttachment.registerMirror(level as ServerLevel, pairId!!, globalPos)
        }
    }

    private fun getLinkedMirror(): GlobalPos? {
        if (pairId == null || mode != Mode.TELEPORT) return null

        val currentTime = level?.gameTime ?: 0

        if (cachedLinkedMirror != null && currentTime < cacheValidUntil) {
            return cachedLinkedMirror
        }

        if (level is ServerLevel) {
            val globalPos = GlobalPos.of(level!!.dimension(), blockPos)
            cachedLinkedMirror = MirrorRegistryAttachment.findPairedMirror(
                level as ServerLevel,
                pairId!!,
                globalPos
            )
            cacheValidUntil = currentTime + CACHE_DURATION
        }

        return cachedLinkedMirror
    }

    fun canEntityTeleport(entity: Entity): Boolean {
        if (entity is Player) {
            if (WitcheryConfig.REQUIRE_GHOST_OF_LIGHT_INFUSION.get()) {
                val infusion = InfusionPlayerAttachment.getData(entity)
                return infusion.type == InfusionType.LIGHT
            }
        }
        return true
    }

    fun tryTeleportEntity(entity: Entity) {
        if (mode != Mode.TELEPORT) return
        if (!canEntityTeleport(entity)) return

        val serverLevel = level as? ServerLevel ?: return

        pairId?.let { id ->
            val globalPos = GlobalPos.of(serverLevel.dimension(), blockPos)
            val data = MirrorRegistryAttachment.getData(serverLevel)
            if (data.entries.none { it.pos == globalPos }) {
                MirrorRegistryAttachment.registerMirror(serverLevel, id, globalPos)
            }
        }

        val targetPos = getLinkedMirror() ?: return
        val targetLevel = serverLevel.server.getLevel(targetPos.dimension()) ?: return
        val targetBe = targetLevel.getBlockEntity(targetPos.pos()) as? MirrorBlockEntity ?: return

        if (targetBe.pairId != this.pairId) {
            cachedLinkedMirror = null
            cacheValidUntil = 0
            return
        }

        setCooldown(entity)
        targetBe.setCooldown(entity)

        val fromFacing = blockState.getValue(MirrorBlock.FACING)
        val toFacing = targetBe.blockState.getValue(MirrorBlock.FACING)

        val angleDelta = toFacing.toYRot() - fromFacing.toYRot() + 180f
        val newYRot = (entity.yRot + angleDelta) % 360f

        val exitFacingVec = Vec3(
            toFacing.normal.x.toDouble(),
            toFacing.normal.y.toDouble(),
            toFacing.normal.z.toDouble()
        )

        val tpTarget = Vec3.atCenterOf(targetPos.pos()).add(exitFacingVec.scale(0.05))

        val launchSpeed = if (entity is ItemEntity) 0.5 else 0.3
        val launchVel = exitFacingVec.multiply(launchSpeed, 0.0, launchSpeed)

        if (serverLevel.dimension() != targetPos.dimension()) {
            entity.changeDimension(
                DimensionTransition(
                    targetLevel,
                    tpTarget,
                    launchVel,
                    newYRot,
                    entity.xRot,
                    DimensionTransition.DO_NOTHING
                )
            )
        } else {
            entity.teleportTo(tpTarget.x, tpTarget.y, tpTarget.z)
            entity.yRot = newYRot
            entity.deltaMovement = launchVel
            entity.hurtMarked = true

            if (entity is ServerPlayer) {
                entity.fallDistance = 0f
                entity.connection.teleport(tpTarget.x, tpTarget.y, tpTarget.z, newYRot, entity.xRot)
            }
        }
    }
}