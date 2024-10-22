package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d

enum class InfusionType : StringRepresentable {
    NONE,
    LIGHT {
        override fun onHoldRightClick(player: Player): Boolean {
            val data = LightInfusionDataAttachment.isInvisible(player)

            if (PlayerInfusionDataAttachment.getInfusionCharge(player) >= 200 && !data.isInvisible) {
                LightInfusionDataAttachment.setInvisible(player, true, 10)
                PlayerInfusionDataAttachment.decreaseInfusionCharge(player, 200)
                return true
            } else if (PlayerInfusionDataAttachment.getInfusionCharge(player) > 2 && data.invisibleTimer > 4) {
                LightInfusionDataAttachment.setInvisible(player, true, 6)
                PlayerInfusionDataAttachment.decreaseInfusionCharge(player, 2)
                return true
            }
            return false
        }
    },
    OVERWORLD {
        override fun onReleaseRightClick(player: Player): Boolean {
            return false
        }
    },
    INFERNAL {
        override fun onReleaseRightClick(player: Player): Boolean {
            return false
        }
    },
    OTHERWHERE {
        override fun onReleaseRightClick(player: Player): Boolean {
            if (PlayerInfusionDataAttachment.getInfusionCharge(player) >= 500){
                val target = raytraceForTeleport(player)
                if (target != null) {
                    PlayerInfusionDataAttachment.decreaseInfusionCharge(player, 500)
                    player.teleportTo(target.x, target.y, target.z)
                    return true
                }
            }
            return false
        }
    };

    open fun onHoldRightClick(player: Player): Boolean {
        return false
    }

    open fun onReleaseRightClick(player: Player): Boolean {
        return false
    }

    open fun leftClickEntity(player: Player, entity: Entity?, entityHitResult: EntityHitResult?): Boolean {
        return false
    }

    open fun leftClickBlock(player: Player, blockPos: BlockPos?, direction: Direction?): Boolean {
        return false
    }

    open fun onReleaseRightClickShift(player: Player): Boolean {
        return false
    }

    open fun leftClickEntityShift(player: Player, entity: Entity?, entityHitResult: EntityHitResult?): Boolean {
        return false
    }

    open fun leftClickBlockShift(player: Player, blockPos: BlockPos?, direction: Direction?): Boolean {
        return false
    }

    override fun getSerializedName(): String {
        return name.lowercase()
    }

    companion object {
        val CODEC: Codec<InfusionType> = StringRepresentable.fromEnum(::values)

        private fun isPosClear(level: Level, pos: BlockPos): Boolean {
            return (level.isEmptyBlock(pos) || level.getBlockState(pos).getCollisionShape(level, pos).isEmpty)
                    && (level.isEmptyBlock(pos.above()) || level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty)
        }

        fun raytraceForTeleport(player: Player): Vec3? {
            val level = player.level()

            val eyePos = player.getEyePosition(0f)
            val dir = player.getViewVector(0f)
            val rayEnd = eyePos.add(dir.x * 32, dir.y * 32, dir.z * 32)
            val result: BlockHitResult = level.clip(ClipContext(eyePos, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player))

            var targetPos = when (result.direction) {
                Direction.DOWN -> result.blockPos.below(2)
                else -> result.blockPos.relative(result.direction)
            }

            var posIsFree = isPosClear(level, targetPos)
            while (!posIsFree) {
                targetPos = targetPos.below()
                posIsFree = isPosClear(level, targetPos) && level.clip(ClipContext(eyePos, Vec3.atCenterOf(targetPos.above()), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)).type == HitResult.Type.MISS
                if (targetPos.y <= 0) {
                    break
                }
            }
            return if (posIsFree) Vec3.atCenterOf(targetPos) else null
        }
    }
}