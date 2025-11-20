package dev.sterner.witchery.content.block.mirror

import dev.sterner.witchery.core.api.multiblock.MultiBlockCoreEntity
import dev.sterner.witchery.core.registry.WitcheryBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
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

class MirrorBlockEntity(blockPos: BlockPos, blockState: BlockState) :
    MultiBlockCoreEntity(WitcheryBlockEntityTypes.MIRROR.get(), MirrorBlock.STRUCTURE.get(), blockPos, blockState) {

    var hasDemon = false
    var isSmallMirror = false
    var linkedMirror: GlobalPos? = null
    private var cooldown = 0
    var mode: Mode = Mode.NONE

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
            val CODEC = StringRepresentable.EnumCodec.STRING.xmap(Mode::valueOf, Mode::name)!!
        }
    }

    companion object {
        const val TELEPORT_COOLDOWN = 20
        const val ENTITY_COOLDOWN = 100
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putBoolean("HasDemon", hasDemon)
        tag.putBoolean("IsSmallMirror", isSmallMirror)
        tag.putString("Mode", mode.serializedName)

        linkedMirror?.let {
            val linkTag = CompoundTag()
            linkTag.putString("Dimension", it.dimension().location().toString())
            linkTag.put("Pos", NbtUtils.writeBlockPos(it.pos()))
            tag.put("LinkedMirror", linkTag)
        }
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        hasDemon = tag.getBoolean("HasDemon")
        isSmallMirror = tag.getBoolean("IsSmallMirror")
        mode = Mode.valueOf(tag.getString("Mode"))

        if (tag.contains("LinkedMirror")) {
            val linkTag = tag.getCompound("LinkedMirror")
            val dimLocation = linkTag.getString("Dimension")
            val pos = NbtUtils.readBlockPos(linkTag, "Pos").orElse(null)

            if (pos != null) {
                val dimension = ResourceKey.create(
                    net.minecraft.core.registries.Registries.DIMENSION,
                    net.minecraft.resources.ResourceLocation.parse(dimLocation)
                )
                linkedMirror = GlobalPos.of(dimension, pos)
            }
        }
    }

    fun linkToMirror(targetPos: GlobalPos) {
        this.linkedMirror = targetPos
        setChanged()

        val targetLevel = level?.server?.getLevel(targetPos.dimension())
        if (targetLevel != null) {
            val targetEntity = targetLevel.getBlockEntity(targetPos.pos())
            if (targetEntity is MirrorBlockEntity) {
                targetEntity.linkedMirror = GlobalPos.of(level!!.dimension(), blockPos)
                targetEntity.setChanged()
            }
        }
    }

    fun unlinkMirror() {
        linkedMirror?.let { target ->
            val targetLevel = level?.server?.getLevel(target.dimension())
            if (targetLevel != null) {
                val targetEntity = targetLevel.getBlockEntity(target.pos())
                if (targetEntity is MirrorBlockEntity) {
                    targetEntity.linkedMirror = null
                    targetEntity.setChanged()
                }
            }
        }

        this.linkedMirror = null
        setChanged()
    }

    override fun tickServer(serverLevel: ServerLevel) {

        if (mode == Mode.TELEPORT) {
            if (cooldown > 0) {
                cooldown--
                return
            }

            val currentTime = level!!.gameTime
            entityCooldowns.entries.removeIf { currentTime - it.value > ENTITY_COOLDOWN }

            if (linkedMirror == null) return

            val aabb = getTeleportationAABB()
            val entities = level!!.getEntitiesOfClass(Entity::class.java, aabb) { entity ->
                !entityCooldowns.containsKey(entity.id) && canTeleport(entity)
            }

            for (entity in entities) {
                if (isSmallMirror) {
                    if (entity is ItemEntity) {
                        teleportEntity(entity)
                    }
                } else {
                    teleportEntity(entity)
                }
            }
        }
    }

    private fun canTeleport(entity: Entity): Boolean {
        if (entityCooldowns.containsKey(entity.id)) return false

        if (isSmallMirror && entity !is ItemEntity) return false

        return true
    }

    private fun getTeleportationAABB(): AABB {
        return if (isSmallMirror) {
            AABB(blockPos).inflate(0.5)
        } else {
            AABB(blockPos).expandTowards(0.0, 1.0, 0.0).inflate(0.5)
        }
    }

    private fun teleportEntity(entity: Entity) {
        val target = linkedMirror ?: return
        val serverLevel = level as? ServerLevel ?: return

        val targetLevel = serverLevel.server.getLevel(target.dimension()) ?: return
        val targetEntity = targetLevel.getBlockEntity(target.pos()) as? MirrorBlockEntity ?: return

        entityCooldowns[entity.id] = serverLevel.gameTime
        targetEntity.entityCooldowns[entity.id] = targetLevel.gameTime

        val fromFacing = this.blockState.getValue(MirrorBlock.FACING)
        val toFacing = targetEntity.blockState.getValue(MirrorBlock.FACING)

        val targetPos = Vec3.atCenterOf(target.pos())

        val velocity = entity.deltaMovement

        val angleFrom = fromFacing.toYRot()
        val angleTo = toFacing.toYRot()

        val angleDeltaDeg = angleTo - angleFrom
        val angleDeltaRad = Math.toRadians(angleDeltaDeg.toDouble())

        val sin = kotlin.math.sin(angleDeltaRad)
        val cos = kotlin.math.cos(angleDeltaRad)

        val rotatedVel = Vec3(
            velocity.x * cos - velocity.z * sin,
            velocity.y,
            velocity.x * sin + velocity.z * cos
        )

        val nudgeAmount = 0.1
        val nudge = Vec3(
            toFacing.stepX.toDouble() * nudgeAmount,
            toFacing.stepY.toDouble() * nudgeAmount,
            toFacing.stepZ.toDouble() * nudgeAmount
        )

        val finalVelocity = rotatedVel.add(nudge)

        if (entity.level().dimension() != target.dimension()) {
            entity.changeDimension(
                DimensionTransition(
                    targetLevel,
                    targetPos,
                    finalVelocity,
                    entity.yRot + angleDeltaDeg,
                    entity.xRot,
                    DimensionTransition.DO_NOTHING
                )
            )
        } else {
            entity.teleportTo(targetPos.x, targetPos.y, targetPos.z)
            entity.deltaMovement = finalVelocity
            entity.hurtMarked = true
        }

        cooldown = TELEPORT_COOLDOWN
        targetEntity.cooldown = TELEPORT_COOLDOWN
    }

}