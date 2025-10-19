package dev.sterner.witchery.client

import dev.sterner.witchery.content.block.ancient_tablet.AncientTabletBlockEntity
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.features.infusion.InfusionType
import dev.sterner.witchery.network.ReadTabletC2SPayload
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.api.distmarker.OnlyIn
import net.neoforged.neoforge.network.PacketDistributor
import java.util.UUID

@OnlyIn(Dist.CLIENT)
object TabletGazeTracker {
    private var currentTablet: UUID? = null
    private var currentPos: BlockPos? = null
    private var gazeStartTime: Long = 0
    private const val REQUIRED_GAZE_TIME = 100L
    private const val MAX_TABLETS = 3

    fun tick() {
        val player = Minecraft.getInstance().player ?: return

        val infusion = InfusionPlayerAttachment.getPlayerInfusion(player)
        if (infusion.type != InfusionType.NECRO) {
            reset()
            return
        }

        val affliction = AfflictionPlayerAttachment.getData(player)
        if (affliction.lichData.readTablets.size >= MAX_TABLETS) {
            reset()
            return
        }

        val level = player.level()
        val lookingAt = findTabletInView(player, level)

        if (lookingAt != null) {
            val (pos, tablet) = lookingAt

            if (affliction.lichData.readTablets.contains(tablet.getTabletId())) {
                reset()
                return
            }

            if (tablet.getTabletId() == currentTablet && pos == currentPos) {
                gazeStartTime++

                if (gazeStartTime >= REQUIRED_GAZE_TIME) {
                    PacketDistributor.sendToServer(ReadTabletC2SPayload(tablet.getTabletId(), pos))
                    reset()
                }
            } else {
                currentTablet = tablet.getTabletId()
                currentPos = pos
                gazeStartTime = 0
            }
        } else {
            reset()
        }
    }

    private fun findTabletInView(player: Player, level: Level): Pair<BlockPos, AncientTabletBlockEntity>? {
        val eyePos = player.getEyePosition(1f)
        val lookVec = player.lookAngle
        val maxDistance = 10.0

        val playerPos = player.blockPosition()
        for (x in -10..10) {
            for (y in -10..10) {
                for (z in -10..10) {
                    val checkPos = playerPos.offset(x, y, z)
                    val blockEntity = level.getBlockEntity(checkPos)

                    if (blockEntity is AncientTabletBlockEntity) {
                        val distance = eyePos.distanceTo(Vec3.atCenterOf(checkPos))

                        if (distance <= maxDistance) {
                            val toTablet = Vec3.atCenterOf(checkPos).subtract(eyePos).normalize()
                            val dot = lookVec.dot(toTablet)

                            if (dot > 0.5) {
                                return Pair(checkPos, blockEntity)
                            }
                        }
                    }
                }
            }
        }

        return null
    }

    private fun reset() {
        currentTablet = null
        currentPos = null
        gazeStartTime = 0
    }
}