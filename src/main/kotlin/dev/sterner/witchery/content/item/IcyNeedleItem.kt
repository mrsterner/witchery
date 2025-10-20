package dev.sterner.witchery.content.item

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.data_attachment.ManifestationPlayerAttachment
import dev.sterner.witchery.core.api.TeleportRequest
import dev.sterner.witchery.features.misc.AccessoryHandler
import dev.sterner.witchery.features.misc.SleepingPlayerHandler
import dev.sterner.witchery.features.misc.TeleportQueueHandler
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.core.registry.WitcheryTags
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

        if (livingEntity is ServerPlayer && WitcheryApi.isInSpiritWorld(livingEntity)) {
            handleSpiritWorldReturn(livingEntity, level)
            return stack
        }

        if (livingEntity is Player &&
            livingEntity.level().dimension() == Level.OVERWORLD &&
            ManifestationPlayerAttachment.getData(livingEntity).manifestationTimer > 0
        ) {

            handleManifestationEnd(livingEntity, level)

            if (!livingEntity.isCreative) {
                stack.shrink(1)
            }
        }

        return stack
    }

    /**
     * Handle returning a player from the spirit world
     */
    private fun handleSpiritWorldReturn(player: ServerPlayer, level: Level) {
        val overworld = level.server!!.overworld()
        val sleepingData = SleepingPlayerHandler.getPlayerFromSleeping(player.uuid, overworld)

        if (sleepingData != null) {
            val chunkPos = ChunkPos(sleepingData.pos)
            overworld.setChunkForced(chunkPos.x, chunkPos.z, true)

            TeleportQueueHandler.addRequest(
                overworld,
                TeleportRequest(
                    player = player.uuid,
                    pos = sleepingData.pos,
                    chunkPos = chunkPos,
                    level.gameTime
                )
            )
        } else {
            val pos = player.respawnPosition ?: overworld.sharedSpawnPos
            if (pos != null) {
                playerHasNoBodyClearInv(player)
                player.teleportTo(
                    overworld,
                    pos.x + 0.5,
                    pos.y + 0.5,
                    pos.z + 0.5,
                    setOf(),
                    player.yRot,
                    player.xRot
                )
            }
        }
    }

    /**
     * Handle ending a player's manifestation in the overworld
     */
    private fun handleManifestationEnd(player: Player, level: Level) {
        if (player.level() !is ServerLevel) return

        val serverLevel = player.level() as ServerLevel
        val sleepingData = SleepingPlayerHandler.getPlayerFromSleeping(player.uuid, serverLevel)

        player.inventory.dropAll()

        val oldData = ManifestationPlayerAttachment.getData(player)
        ManifestationPlayerAttachment.setData(
            player,
            ManifestationPlayerAttachment.Data(oldData.hasRiteOfManifestation, 0)
        )

        if (sleepingData != null) {
            val chunkPos = ChunkPos(sleepingData.pos)
            serverLevel.setChunkForced(chunkPos.x, chunkPos.z, true)

            TeleportQueueHandler.addRequest(
                serverLevel,
                TeleportRequest(
                    player = player.uuid,
                    pos = sleepingData.pos,
                    chunkPos = chunkPos,
                    level.gameTime
                )
            )
        }
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

    /**
     * Clear inventory but keep special items when player has no body to return to
     */
    fun playerHasNoBodyClearInv(player: Player) {
        val itemsToKeep = mutableListOf<ItemStack>()
        val armorToKeep = mutableListOf<ItemStack>()

        try {
            val charmStack: ItemStack? = AccessoryHandler.checkNoConsume(player, WitcheryItems.DREAMWEAVER_CHARM.get())
            if (charmStack != null) {
                for (armor in player.armorSlots) {
                    if (!armor.isEmpty) {
                        armorToKeep.add(armor.copy())
                    }
                }
            }

            for (i in 0 until player.inventory.containerSize) {
                val itemStack = player.inventory.getItem(i)
                if (!itemStack.isEmpty && itemStack.`is`(WitcheryTags.FROM_SPIRIT_WORLD_TRANSFERABLE)) {
                    itemsToKeep.add(itemStack.copy())
                }
            }

            player.inventory.clearContent()

            for (item in itemsToKeep) {
                player.inventory.add(item)
            }

            for (armor in armorToKeep) {
                val slot = player.getEquipmentSlotForItem(armor)
                player.setItemSlot(slot, armor)
            }
        } catch (e: Exception) {
            Witchery.LOGGER.error("Error clearing inventory for player with no body", e)
        }
    }
}