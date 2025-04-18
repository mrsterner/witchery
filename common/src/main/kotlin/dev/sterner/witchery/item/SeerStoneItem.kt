package dev.sterner.witchery.item

import dev.sterner.witchery.block.ritual.GoldenChalkBlock
import dev.sterner.witchery.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.handler.CovenHandler
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

class SeerStoneItem(properties: Properties) : Item(properties) {

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        if (livingEntity is ServerPlayer && !level.isClientSide) {
            val covenSize = CovenHandler.getSummonableWitchCount(livingEntity)
            if (covenSize > 0) {
                val box = AABB(livingEntity.blockPosition()).inflate(16.0, 8.0, 16.0)
                val posStream = BlockPos.betweenClosedStream(box)

                for (pos in posStream) {
                    val state = level.getBlockState(pos)
                    if (state.block is GoldenChalkBlock) {
                        val be = level.getBlockEntity(pos)
                        if (be is GoldenChalkBlockEntity) {
                            val centerX = pos.x + 0.5
                            val centerY = pos.y + 1.0
                            val centerZ = pos.z + 0.5
                            val radius = 4.5
                            val angleIncrement = (2 * Math.PI) / covenSize

                            for (i in 0 .. covenSize) {
                                val angle = i * angleIncrement
                                val targetX = centerX + radius * cos(angle)
                                val targetZ = centerZ + radius * sin(angle)
                                val targetPos = BlockPos.containing(targetX, centerY, targetZ)

                                val spawnPos = findValidSpawnPosition(level, targetPos)
                                if (spawnPos != null) {
                                    val witch = CovenHandler.summonWitchFromCoven(livingEntity, i, Vec3(spawnPos.x + 0.5, spawnPos.y.toDouble(), spawnPos.z + 0.5))
                                    witch?.setLastRitualPos(Optional.of(pos))
                                }
                            }

                            break
                        }
                    }
                }
            }
        }

        return super.finishUsingItem(stack, level, livingEntity)
    }

    private fun findValidSpawnPosition(level: Level, origin: BlockPos, radius: Int = 2): BlockPos? {
        for (dy in -1..1) {
            for (dx in -radius..radius) {
                for (dz in -radius..radius) {
                    val checkPos = origin.offset(dx, dy, dz)
                    if (level.getBlockState(checkPos).canBeReplaced() && level.getBlockState(checkPos.above()).canBeReplaced()) {
                        return checkPos
                    }
                }
            }
        }
        return null
    }


    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.BLOCK
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 4 * 20
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val itemStack = player.getItemInHand(usedHand)
        player.startUsingItem(usedHand)
        return InteractionResultHolder.consume(itemStack)
    }
}