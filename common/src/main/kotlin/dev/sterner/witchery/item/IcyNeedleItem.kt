package dev.sterner.witchery.item

import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.platform.ManifestationPlayerAttachment
import dev.sterner.witchery.platform.SleepingLevelAttachment
import dev.sterner.witchery.platform.TeleportQueueLevelAttachment
import dev.sterner.witchery.platform.TeleportRequest
import net.minecraft.server.level.ServerLevel
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

        if (livingEntity is ServerPlayer && (WitcheryApi.isInSpiritWorld(livingEntity))) {
            val overworld = level.server!!.overworld()

            val sleepingData = SleepingLevelAttachment.getPlayerFromSleeping(livingEntity.uuid, overworld)

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

        if (livingEntity is Player && livingEntity.level().dimension() == Level.OVERWORLD && ManifestationPlayerAttachment.getData(livingEntity).manifestationTimer > 0) {
            if (livingEntity.level() is ServerLevel) {
                val serverLevel = livingEntity.level() as ServerLevel
                val sleepingData = SleepingLevelAttachment.getPlayerFromSleeping(livingEntity.uuid, serverLevel)
                livingEntity.inventory.dropAll()
                val oldData = ManifestationPlayerAttachment.getData(livingEntity)
                ManifestationPlayerAttachment.setData(livingEntity, ManifestationPlayerAttachment.Data(oldData.hasRiteOfManifestation, 0))
                if (sleepingData != null) {
                    val chunkPos = ChunkPos(sleepingData.pos)
                    serverLevel.setChunkForced(chunkPos.x, chunkPos.z, true)

                    TeleportQueueLevelAttachment.addRequest(
                        serverLevel,
                        TeleportRequest(
                            player = livingEntity.uuid,
                            pos = sleepingData.pos,
                            chunkPos = chunkPos
                        )
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