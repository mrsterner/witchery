package dev.sterner.witchery.core.data_attachment.infusion

import com.mojang.serialization.Codec
import dev.sterner.witchery.handler.NecroHandler
import dev.sterner.witchery.features.infusion.InfusionHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.StringRepresentable
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.ProjectileUtil
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import kotlin.math.min

enum class InfusionType : StringRepresentable {
    NONE,
    LIGHT {
        override fun onHoldRightClick(player: Player): Boolean {
            val data = LightInfusionPlayerAttachment.isInvisible(player)

            if (InfusionPlayerAttachment.getInfusionCharge(player) >= 200 && !data.isInvisible) {
                LightInfusionPlayerAttachment.setInvisible(player, true, 10)
                InfusionHandler.decreaseInfusionCharge(player, 200)
                return true
            } else if (InfusionPlayerAttachment.getInfusionCharge(player) > 2 && data.invisibleTimer > 4) {
                LightInfusionPlayerAttachment.setInvisible(player, true, 6)
                InfusionHandler.decreaseInfusionCharge(player, 2)
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
            val data = InfernalInfusionPlayerAttachment.getData(player)

            val hit = ProjectileUtil.getHitResultOnViewVector(
                player,
                { entity: Entity -> !entity.isSpectator && entity.isPickable }, player.blockInteractionRange()
            )

            val bl = data.currentCreature.usePower(player, player.level(), player.lookAngle, hit)
            if (bl) {
                InfusionHandler.decreaseInfusionCharge(player, data.currentCreature.getCost())
            }

            return false
        }
    },
    OTHERWHERE {
        override fun onReleaseRightClick(player: Player): Boolean {
            if (InfusionPlayerAttachment.getInfusionCharge(player) >= 500) {

                val data = OtherwhereInfusionPlayerAttachment.getInfusion(player)

                val target = raytraceForTeleport(player, data.teleportHoldTicks)
                if (target != null) {
                    InfusionHandler.decreaseInfusionCharge(player, 500)
                    player.teleportTo(target.x, target.y, target.z)
                    val old = OtherwhereInfusionPlayerAttachment.getInfusion(player)
                    OtherwhereInfusionPlayerAttachment.setInfusion(player, 0, old.teleportCooldown)
                    return true
                }
            }

            val old = OtherwhereInfusionPlayerAttachment.getInfusion(player)
            OtherwhereInfusionPlayerAttachment.setInfusion(player, 0, old.teleportCooldown)

            return false
        }

        override fun onHoldRightClick(player: Player): Boolean {
            val old = OtherwhereInfusionPlayerAttachment.getInfusion(player)
            val n = old.teleportHoldTicks + 1
            OtherwhereInfusionPlayerAttachment.setInfusion(player, min(n, 20 * 4), old.teleportCooldown)

            if (n % 40 == 0) {
                player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.5f, 1f)
            }

            return super.onHoldRightClick(player)
        }
    },
    NECRO {

        override fun onReleaseRightClick(player: Player): Boolean {
            if (InfusionPlayerAttachment.getInfusionCharge(player) >= 500) {
                val hit = ProjectileUtil.getHitResultOnViewVector(
                    player,
                    { entity: Entity -> !entity.isSpectator && entity.isPickable }, player.blockInteractionRange() * 2
                )
                if (player.level() is ServerLevel) {
                    NecroHandler.summonNecroAroundPos(
                        player.level() as ServerLevel,
                        player,
                        BlockPos.containing(hit.location),
                        8
                    )
                }
                InfusionHandler.decreaseInfusionCharge(player, 500)
                return true
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

    open fun leftClickEntity(player: Player, entity: Entity?): Boolean {
        return false
    }

    open fun leftClickBlock(player: Player, blockPos: BlockPos?): Boolean {
        return false
    }

    open fun onReleaseRightClickShift(player: Player): Boolean {
        return false
    }

    open fun leftClickEntityShift(player: Player, entity: Entity?): Boolean {
        return false
    }

    open fun leftClickBlockShift(player: Player, blockPos: BlockPos?): Boolean {
        return false
    }

    override fun getSerializedName(): String {
        return name.lowercase()
    }

    companion object {
        val CODEC: Codec<InfusionType> = StringRepresentable.fromEnum(::values)

        private fun isPosClear(level: Level, pos: BlockPos): Boolean {
            return (level.isEmptyBlock(pos) || level.getBlockState(pos).getCollisionShape(level, pos).isEmpty)
                    && (level.isEmptyBlock(pos.above()) || level.getBlockState(pos.above())
                .getCollisionShape(level, pos.above()).isEmpty)
        }

        fun raytraceForTeleport(player: Player, teleportHoldTicks: Int): Vec3? {
            val level = player.level()

            val bonusDistance = (teleportHoldTicks / 20) * 16
            val eyePos = player.getEyePosition(0f)
            val dir = player.getViewVector(0f)

            val maxDistance = 16.0 + bonusDistance
            val rayEnd = eyePos.add(dir.scale(maxDistance))

            val result: BlockHitResult = level.clip(
                ClipContext(eyePos, rayEnd, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player)
            )

            var targetPos = when (result.direction) {
                Direction.DOWN -> result.blockPos.below(2)
                Direction.UP -> result.blockPos.above()
                else -> result.blockPos.relative(result.direction)
            }

            var posIsFree = isPosClear(level, targetPos)

            while (!posIsFree) {
                targetPos = targetPos.below()
                posIsFree = isPosClear(level, targetPos) && level.clip(
                    ClipContext(
                        eyePos,
                        Vec3.atCenterOf(targetPos.above()),
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE, player
                    )
                ).type == HitResult.Type.MISS

                if (targetPos.y <= level.minBuildHeight)
                    break
            }

            return if (posIsFree) Vec3.atCenterOf(targetPos) else null
        }
    }
}