package dev.sterner.witchery.item.brew

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.SleepingPlayerData
import dev.sterner.witchery.entity.SleepingPlayerEntity
import dev.sterner.witchery.platform.SleepingPlayerLevelAttachment
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryItems
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
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

    override fun applyEffectOnSelf(player: Player, frog: Boolean) {

        if (player.level().dimension() != Level.OVERWORLD) {
            return
        }

        val sleepingPlayer = SleepingPlayerEntity.createFromPlayer(player, SleepingPlayerData.fromPlayer(player))
        val itemsToKeep = mutableListOf<ItemStack>()

        for (i in 0 until player.inventory.containerSize) {
            val itemStack = player.inventory.getItem(i)
            if (itemStack.item == WitcheryItems.ICY_NEEDLE.get()) {
                itemsToKeep.add(player.inventory.removeItem(i, itemStack.count))
            }
        }

        player.inventory.clearContent()

        for (keep in itemsToKeep) {
            player.inventory.add(keep.copy())
        }

        player.level().addFreshEntity(sleepingPlayer)
        if (player.level() is ServerLevel) {
            val serverLevel = player.level() as ServerLevel
            val chunk = ChunkPos(player.onPos)
            serverLevel.setChunkForced(chunk.x, chunk.z, true)
        }
        val maxDreamweavers = 4
        val maxFlowingSpirits = 4
        val maxWispyCotton = 4
        val dreamweaverCount = countNearbyBlocks(player, WitcheryBlocks.DREAM_WEAVER_OF_NIGHTMARES.get())
        val flowingSpiritCount = countNearbyBlocks(player, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get())
        val wispyCount = countNearbyBlocks(player, WitcheryBlocks.FLOWING_SPIRIT_BLOCK.get())

        val maxEffectCount = (maxDreamweavers + maxFlowingSpirits + maxWispyCotton).toDouble()
        val effectiveCount =
            (dreamweaverCount.coerceAtMost(maxDreamweavers) + flowingSpiritCount.coerceAtMost(maxFlowingSpirits)
                    + wispyCount.coerceAtMost(maxWispyCotton)).toDouble()
        val goodDreamChance = 0.05 + 0.85 * (effectiveCount / maxEffectCount) // Scale up to 90% with max blocks

        val key = ResourceKey.create(Registries.DIMENSION, Witchery.id("dream_world"))
        val nightmareKey = ResourceKey.create(Registries.DIMENSION, Witchery.id("nightmare_world"))

        val destinationKey = if (player.level().random.nextDouble() < goodDreamChance) key else nightmareKey

        val destination = player.level().server?.getLevel(destinationKey)
        if (destination != null) {
            val targetX = player.x
            val targetY = player.y
            val targetZ = player.z

            val blockPos = BlockPos.containing(targetX, targetY, targetZ)
            val solidY: Int = if (!destination.getBlockState(blockPos).isSolid) blockPos.y else destination.getHeight(
                Heightmap.Types.MOTION_BLOCKING,
                blockPos.x,
                blockPos.z
            )

            if (player is ServerPlayer) {
                player.teleportTo(destination, targetX, solidY + 1.0, targetZ, player.yRot, player.xRot)
            }

        }

        super.applyEffectOnSelf(player, frog)
    }

    companion object {
        fun respawnPlayer(oldPlayer: ServerPlayer?, newServerPlayer: ServerPlayer?, wonGame: Boolean) {
            if (newServerPlayer?.level() is ServerLevel) {
                val serverLevel = newServerPlayer.level() as ServerLevel
                val hasSleeping = SleepingPlayerLevelAttachment.getPlayerFromSleeping(newServerPlayer.uuid, serverLevel)
                if (hasSleeping != null) {
                    val chunk = ChunkPos(hasSleeping.pos)
                    serverLevel.setChunkForced(chunk.x, chunk.z, true)
                    val sleepEntity: SleepingPlayerEntity? = serverLevel.getEntity(hasSleeping.uuid) as SleepingPlayerEntity?
                    if (sleepEntity != null) {
                        SleepingPlayerEntity.replaceWithPlayer(newServerPlayer, sleepEntity)
                    }
                }
            }
        }

        private fun countNearbyBlocks(player: Player, blockToCheck: Block, radius: Int = 6): Int {
            val level = player.level()
            val pos = BlockPos.containing(player.x, player.y, player.z)
            var count = 0

            for (x in -radius..radius) {
                for (y in -radius..radius) {
                    for (z in -radius..radius) {
                        val currentPos = pos.offset(x, y, z)
                        if (level.getBlockState(currentPos).block == blockToCheck) {
                            count++
                            if (count >= 4) return count
                        }
                    }
                }
            }
            return count
        }
    }
}