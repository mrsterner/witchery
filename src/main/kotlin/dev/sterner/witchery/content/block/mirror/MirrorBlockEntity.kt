package dev.sterner.witchery.content.block.mirror

import dev.sterner.witchery.content.entity.EntEntity.Type
import dev.sterner.witchery.core.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import dev.sterner.witchery.features.mirror.MirrorRegistryAttachment
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.npc.AbstractVillager
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.portal.DimensionTransition
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.UUID
import kotlin.math.cos
import kotlin.math.sin

class MirrorBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.MIRROR.get(), MirrorBlock.STRUCTURE.get(), blockPos, blockState) {

    var hasDemon = false
    var isSmallMirror = false
    var pairId: UUID? = null
    var cachedLinkedMirror: GlobalPos? = null
    var mode: Mode = Mode.NONE

    private val stuckTimers = mutableMapOf<Int, Long>()

    fun resetStuckTimer(entity: Entity) {
        stuckTimers.remove(entity.id)
    }

    fun incrementStuckTimer(entity: Entity, amount: Int = 1): Int {
        val t = (stuckTimers[entity.id] ?: 0) + amount
        stuckTimers[entity.id] = t
        return t.toInt()
    }

    fun getStuckTimer(entity: Entity): Int {
        return stuckTimers[entity.id]?.toInt() ?: 0
    }

    private val entityCooldowns = mutableMapOf<Int, Long>()

    enum class Mode : StringRepresentable {
        NONE,
        DEMONIC,
        TELEPORT,
        POCKET_DIMENSION;

        override fun getSerializedName(): String {
            return name.lowercase()
        }

        companion object {
            val CODEC: StringRepresentable.EnumCodec<Mode> = StringRepresentable.fromEnum { Mode.entries.toTypedArray() }
        }
    }

    companion object {
        const val TELEPORT_COOLDOWN = 20
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putBoolean("HasDemon", hasDemon)
        tag.putBoolean("IsSmallMirror", isSmallMirror)
        tag.putString("Mode", mode.serializedName)

        pairId?.let {
            tag.put("PairId", UUIDUtil.CODEC.encodeStart(NbtOps.INSTANCE, it).result().orElse(null))
        }
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        hasDemon = tag.getBoolean("HasDemon")
        isSmallMirror = tag.getBoolean("IsSmallMirror")

        if (tag.contains("Mode")) {
            mode = Mode.CODEC.parse(NbtOps.INSTANCE, tag.get("Mode")).result().orElse(Mode.NONE)
        }

        if (tag.contains("PairId")) {
            pairId = UUIDUtil.CODEC.parse(NbtOps.INSTANCE, tag.get("PairId")).result().orElse(null)
        }
    }
    fun isOnCooldown(entity: Entity): Boolean {
        val t = level!!.gameTime
        val last = entityCooldowns[entity.id] ?: return false
        return t - last < TELEPORT_COOLDOWN
    }

    fun setCooldown(entity: Entity) {
        entityCooldowns[entity.id] = level!!.gameTime
    }
    override fun onLoad() {
        super.onLoad()

        if (!level!!.isClientSide && level is ServerLevel && pairId != null) {
            val globalPos = GlobalPos.of(level!!.dimension(), blockPos)
            MirrorRegistryAttachment.registerMirror(level as ServerLevel, pairId!!, globalPos)
        }
    }

    override fun setRemoved() {
        super.setRemoved()

        if (!level!!.isClientSide && level is ServerLevel && pairId != null) {
            val globalPos = GlobalPos.of(level!!.dimension(), blockPos)
            val serverLevel = level as ServerLevel

            val pairedMirrorPos = MirrorRegistryAttachment.findPairedMirror(serverLevel, pairId!!, globalPos)

            MirrorRegistryAttachment.unregisterMirror(serverLevel, globalPos)

            if (pairedMirrorPos != null) {
                val pairedLevel = level!!.server?.getLevel(pairedMirrorPos.dimension())
                if (pairedLevel != null) {
                    val pairedEntity = pairedLevel.getBlockEntity(pairedMirrorPos.pos())
                    if (pairedEntity is MirrorBlockEntity) {
                        pairedEntity.mode = Mode.DEMONIC
                        pairedEntity.hasDemon = true
                        pairedEntity.setChanged()
                    }
                }
            }
        }
    }

    fun putPairId(uuid: UUID) {
        this.pairId = uuid
        this.mode = Mode.TELEPORT
        this.cachedLinkedMirror = null
        setChanged()

        if (!level!!.isClientSide && level is ServerLevel) {
            val globalPos = GlobalPos.of(level!!.dimension(), blockPos)
            MirrorRegistryAttachment.registerMirror(level as ServerLevel, uuid, globalPos)
        }
    }

    private fun getLinkedMirror(): GlobalPos? {
        if (pairId == null) return null
        if (cachedLinkedMirror != null) return cachedLinkedMirror

        if (level is ServerLevel) {
            val globalPos = GlobalPos.of(level!!.dimension(), blockPos)
            cachedLinkedMirror = MirrorRegistryAttachment.findPairedMirror(level as ServerLevel, pairId!!, globalPos)
        }

        return cachedLinkedMirror
    }

    fun tryTeleportEntity(entity: Entity) {
        val serverLevel = level as? ServerLevel ?: return

        val targetPos = getLinkedMirror() ?: return
        val targetLevel = serverLevel.server.getLevel(targetPos.dimension()) ?: return

        val targetBe = targetLevel.getBlockEntity(targetPos.pos()) as? MirrorBlockEntity ?: return

        setCooldown(entity)
        targetBe.setCooldown(entity)

        val fromFacing = blockState.getValue(MirrorBlock.FACING)
        val toFacing = targetBe.blockState.getValue(MirrorBlock.FACING)

        val angleDelta = toFacing.toYRot() - fromFacing.toYRot()
        val rad = Math.toRadians(angleDelta.toDouble())

        val vel = entity.deltaMovement
        val rotated = Vec3(
            vel.x * cos(rad) - vel.z * sin(rad),
            vel.y,
            vel.x * sin(rad) + vel.z * cos(rad)
        )

        val nudge = Vec3(toFacing.normal.x.toDouble(), toFacing.normal.y.toDouble(), toFacing.normal.z.toDouble()).scale(0.1)
        val finalVel = rotated.add(nudge)

        val tpTarget = Vec3.atCenterOf(targetPos.pos())

        if (serverLevel.dimension() != targetPos.dimension()) {
            entity.changeDimension(
                DimensionTransition(
                    targetLevel,
                    tpTarget,
                    finalVel,
                    entity.yRot + angleDelta,
                    entity.xRot,
                    DimensionTransition.DO_NOTHING
                )
            )
        } else {
            entity.teleportTo(tpTarget.x, tpTarget.y, tpTarget.z)
            entity.deltaMovement = finalVel
            entity.hurtMarked = true
        }
    }

}