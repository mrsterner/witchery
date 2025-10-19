package dev.sterner.witchery.features.brewing.brew

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.entity.PlayerShellEntity
import dev.sterner.witchery.content.entity.player_shell.SleepingPlayerEntity
import dev.sterner.witchery.features.misc.AccessoryHandler
import dev.sterner.witchery.features.misc.SleepingPlayerHandler
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.core.registry.WitcheryItems
import dev.sterner.witchery.registry.WitcheryTags
import dev.sterner.witchery.content.worldgen.WitcheryWorldgenKeys
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.levelgen.Heightmap

@Suppress("DEPRECATION")
class BrewOfSleepingItem(color: Int, properties: Properties) : BrewItem(color, properties) {

    companion object {
        private const val MAX_NEARBY_BLOCKS = 4
        private const val SEARCH_RADIUS = 6


        /**
         * Handles player respawn after death or returning from the dream world
         */
        fun respawnPlayer(newServerPlayer: Player?) {
            if (newServerPlayer?.level() !is ServerLevel) return

            val serverLevel = newServerPlayer.level() as ServerLevel
            val hasSleeping = SleepingPlayerHandler.getPlayerFromSleeping(newServerPlayer.uuid, serverLevel)

            if (hasSleeping != null) {
                val chunk = ChunkPos(hasSleeping.pos)
                serverLevel.setChunkForced(chunk.x, chunk.z, true)

                val sleepEntity: SleepingPlayerEntity? =
                    serverLevel.getEntity(hasSleeping.uuid) as SleepingPlayerEntity?

                if (sleepEntity != null) {
                    SleepingPlayerEntity.replaceWithPlayer(newServerPlayer, sleepEntity)
                } else {
                    Witchery.LOGGER.warn("Could not find sleeping entity for player ${newServerPlayer.uuid}")
                }
            }
        }

        /**
         * Count nearby blocks of a specific type within a radius
         * @return Number of matching blocks found, up to MAX_NEARBY_BLOCKS
         */
        private fun countNearbyBlocks(player: Player, blockToCheck: Block, radius: Int = SEARCH_RADIUS): Int {
            val level = player.level()
            val pos = BlockPos.containing(player.x, player.y, player.z)
            var count = 0

            for (x in -radius..radius) {
                for (y in -radius..radius) {
                    for (z in -radius..radius) {
                        val currentPos = pos.offset(x, y, z)
                        if (level.getBlockState(currentPos).block == blockToCheck) {
                            count++
                            if (count >= MAX_NEARBY_BLOCKS) return count
                        }
                    }
                }
            }
            return count
        }
    }

    override fun applyEffectOnSelf(player: Player, hasFrog: Boolean) {
        if (player.level().dimension() != Level.OVERWORLD) {
            player.sendSystemMessage(Component.translatable("witchery.message.cant_sleep_here"))
            return
        }

        val (itemsToKeep, armorToKeep) = savePlayerItems(player, hasFrog)

        val sleepingPlayer = createSleepingEntity(player)
        player.inventory.clearContent()
        restoreKeptItems(player, itemsToKeep, armorToKeep)

        player.level().addFreshEntity(sleepingPlayer)

        forceLoadPlayerChunk(player)

        val destinationKey = calculateDreamDestination(player, hasFrog)

        teleportToDreamDimension(player, destinationKey)

        super.applyEffectOnSelf(player, hasFrog)
    }

    /**
     * Save items that should be kept during sleep
     */
    private fun savePlayerItems(
        player: Player,
        hasFrog: Boolean
    ): Pair<MutableList<ItemStack>, MutableList<ItemStack>> {
        val itemsToKeep = mutableListOf<ItemStack>()
        val armorToKeep = mutableListOf<ItemStack>()

        val charmStack: ItemStack? = AccessoryHandler.checkNoConsume(player, WitcheryItems.DREAMWEAVER_CHARM.get())

        if (charmStack != null) {
            for (armor in player.armorSlots) {
                if (!armor.isEmpty) {
                    armorToKeep.add(armor.copy())
                    player.inventory.removeItem(armor)
                }
            }
        }

        for (i in 0 until player.inventory.containerSize) {
            val itemStack = player.inventory.getItem(i)
            if (!itemStack.isEmpty && itemStack.item.builtInRegistryHolder()
                    .`is`(WitcheryTags.TO_SPIRIT_WORLD_TRANSFERABLE)
            ) {
                itemsToKeep.add(player.inventory.removeItem(i, itemStack.count))
            }
        }

        return Pair(itemsToKeep, armorToKeep)
    }


    /**
     * Create sleeping entity from player
     */
    private fun createSleepingEntity(player: Player): SleepingPlayerEntity {
        return PlayerShellEntity.createSleepFromPlayer(player)
    }

    /**
     * Restore items that were kept during sleep
     */
    private fun restoreKeptItems(player: Player, itemsToKeep: List<ItemStack>, armorToKeep: List<ItemStack>) {
        for (keep in itemsToKeep) {
            player.inventory.add(keep.copy())
        }

        for (armor in armorToKeep) {
            val slot = player.getEquipmentSlotForItem(armor)
            player.setItemSlot(slot, armor.copy())
        }
    }

    /**
     * Force load the chunk where the sleeping player is
     */
    private fun forceLoadPlayerChunk(player: Player) {
        if (player.level() is ServerLevel) {
            val serverLevel = player.level() as ServerLevel
            val chunk = ChunkPos(player.onPos)
            serverLevel.setChunkForced(chunk.x, chunk.z, true)
        }
    }

    /**
     * Calculate dream quality and determine destination dimension
     */
    private fun calculateDreamDestination(player: Player, hasFrog: Boolean): ResourceKey<Level> {
        val maxDreamweavers = if (hasFrog) 3 else 4
        val maxFlowingSpirits = 4
        val maxWispyCotton = if (hasFrog) 3 else 4

        val dreamweaverCount = countNearbyBlocks(player, WitcheryBlocks.DREAM_WEAVER_OF_NIGHTMARES.get())
        val flowingSpiritCount = countNearbyBlocks(player, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get())
        val wispyCount = countNearbyBlocks(player, WitcheryBlocks.WISPY_COTTON.get())

        val maxEffectCount = (maxDreamweavers + maxFlowingSpirits + maxWispyCotton).toDouble()
        val effectiveCount = (
                dreamweaverCount.coerceAtMost(maxDreamweavers) +
                        flowingSpiritCount.coerceAtMost(maxFlowingSpirits) +
                        wispyCount.coerceAtMost(maxWispyCotton)
                ).toDouble()

        val goodDreamChance = 0.05 + 0.85 * (effectiveCount / maxEffectCount)

        return if (player.level().random.nextDouble() < goodDreamChance) WitcheryWorldgenKeys.DREAM else WitcheryWorldgenKeys.NIGHTMARE
    }

    /**
     * Teleport player to dream dimension
     */
    private fun teleportToDreamDimension(player: Player, destinationKey: ResourceKey<Level>) {
        val destination = player.level().server?.getLevel(destinationKey) ?: return

        val targetX = player.x
        val targetY = player.y
        val targetZ = player.z

        val blockPos = BlockPos.containing(targetX, targetY, targetZ)
        val solidY = if (!destination.getBlockState(blockPos).isSolid) {
            blockPos.y
        } else {
            destination.getHeight(Heightmap.Types.MOTION_BLOCKING, blockPos.x, blockPos.z)
        }

        if (player is ServerPlayer) {
            player.teleportTo(destination, targetX, solidY + 1.0, targetZ, player.yRot, player.xRot)
        }
    }
}