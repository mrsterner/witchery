package dev.sterner.witchery.item

import dev.sterner.witchery.platform.SleepingPlayerLevelAttachment
import dev.sterner.witchery.platform.TeleportQueueLevelAttachment
import dev.sterner.witchery.platform.TeleportRequest
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ItemUtils
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level

class IcyNeedleItem(properties: Properties) : Item(properties) {

    override fun finishUsingItem(stack: ItemStack, level: Level, livingEntity: LivingEntity): ItemStack {
        super.finishUsingItem(stack, level, livingEntity)

        if (livingEntity is ServerPlayer && (level.dimension() == WitcheryWorldgenKeys.DREAM || level.dimension() == WitcheryWorldgenKeys.NIGHTMARE)) {
            val overworld = level.server!!.overworld()

            val sleepingData = SleepingPlayerLevelAttachment.getPlayerFromSleeping(livingEntity.uuid, overworld)

            if (sleepingData != null) {
                val chunkPos = ChunkPos(sleepingData.pos)
                overworld.setChunkForced(chunkPos.x, chunkPos.z, true)

                TeleportQueueLevelAttachment.addRequest(
                    overworld,
                    TeleportRequest(
                        player = livingEntity.uuid,
                        pos = sleepingData.pos,
                        chunkPos = chunkPos
                    )
                )
                return stack
            } else {
                val pos = livingEntity.respawnPosition ?: overworld.sharedSpawnPos
                if (pos != null) {
                    livingEntity.teleportTo(overworld, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, setOf(),
                        livingEntity.yRot,
                        livingEntity.xRot
                    )
                }
            }
        }

        return stack
    }

    override fun getUseDuration(stack: ItemStack, entity: LivingEntity): Int {
        return 40
    }

    override fun getUseAnimation(stack: ItemStack): UseAnim {
        return UseAnim.BOW
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        return ItemUtils.startUsingInstantly(level, player, usedHand)
    }
}