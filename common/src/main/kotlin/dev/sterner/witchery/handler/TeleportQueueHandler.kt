package dev.sterner.witchery.handler

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.sleeping_player.SleepingPlayerEntity
import dev.sterner.witchery.platform.teleport.TeleportQueueLevelAttachment
import dev.sterner.witchery.platform.teleport.TeleportRequest
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.phys.AABB
import kotlin.math.abs

// 1. Improved TeleportQueueHandler - Better efficiency and error handling

object TeleportQueueHandler {
    private const val PROCESS_INTERVAL = 10 // Process every 10 ticks
    private const val MIN_WAIT_TICKS = 5 // Wait at least 5 ticks before processing
    private const val MAX_ATTEMPTS = 10 // Maximum teleport attempts
    private const val STALE_REQUEST_TIME = 6000L // 5 minutes in ticks

    /**
     * Process all pending teleport requests across all server levels
     */
    fun processQueue(server: MinecraftServer) {
        // Only process every PROCESS_INTERVAL ticks (every half second)
        if (server.tickCount % PROCESS_INTERVAL != 0) return

        // Get all levels and process each one's teleport queue
        server.allLevels.forEach { level ->
            if (level is ServerLevel) {
                processLevelQueue(level)
            }
        }
    }

    /**
     * Process teleport requests for a specific level
     */
    private fun processLevelQueue(level: ServerLevel) {
        val data = TeleportQueueLevelAttachment.getData(level)
        val currentTime = level.gameTime
        val toRemove = mutableListOf<TeleportRequest>()

        // Process each teleport request
        data.pendingTeleports.forEach { request ->
            try {
                // Skip if request is too new
                if (currentTime - request.createdGameTime < MIN_WAIT_TICKS) return@forEach

                // Get player if online
                val player = level.server.playerList.getPlayer(request.player)

                if (player != null) {
                    // Player is online, perform teleport
                    val success = handleTeleport(player, request, level)

                    if (success) {
                        // If teleport succeeded, add to removal list
                        toRemove.add(request)
                        safelyUnforceChunk(level, request.chunkPos)
                    } else {
                        // Failed teleport, increment attempts
                        request.attempts++

                        // Remove after MAX_ATTEMPTS failed attempts
                        if (request.attempts >= MAX_ATTEMPTS) {
                            toRemove.add(request)
                            safelyUnforceChunk(level, request.chunkPos)
                            Witchery.LOGGER.warn("Failed to teleport player ${request.player} after ${request.attempts} attempts")
                        }
                    }
                } else {
                    // Player is offline - Remove old requests
                    if (currentTime - request.createdGameTime > STALE_REQUEST_TIME) {
                        toRemove.add(request)
                        safelyUnforceChunk(level, request.chunkPos)
                        Witchery.LOGGER.debug("Removed stale teleport request for offline player ${request.player}")
                    }
                }
            } catch (e: Exception) {
                // Add request to removal list on exception to prevent infinite retries
                toRemove.add(request)
                safelyUnforceChunk(level, request.chunkPos)
                Witchery.LOGGER.error("Exception while processing teleport request for ${request.player}", e)
            }
        }

        // Remove processed requests
        if (toRemove.isNotEmpty()) {
            updatePendingTeleports(level, data, toRemove)
        }
    }

    /**
     * Safely unforce a chunk, catching any exceptions
     */
    private fun safelyUnforceChunk(level: ServerLevel, chunkPos: ChunkPos) {
        try {
            level.setChunkForced(chunkPos.x, chunkPos.z, false)
        } catch (e: Exception) {
            Witchery.LOGGER.error("Failed to unforce chunk at $chunkPos", e)
        }
    }

    /**
     * Update the pending teleports list, removing processed requests
     */
    private fun updatePendingTeleports(level: ServerLevel, data: TeleportQueueLevelAttachment.Data, toRemove: List<TeleportRequest>) {
        try {
            val updatedRequests = data.pendingTeleports.toMutableList()
            updatedRequests.removeAll(toRemove)
            TeleportQueueLevelAttachment.setData(level, TeleportQueueLevelAttachment.Data(updatedRequests))
        } catch (e: Exception) {
            Witchery.LOGGER.error("Failed to update teleport queue data", e)
        }
    }

    /**
     * Handle actual teleport operation for a player
     */
    private fun handleTeleport(player: ServerPlayer, request: TeleportRequest, level: ServerLevel): Boolean {
        return try {
            // Determine target level - overworld by default
            val targetLevel = request.sourceDimension?.let { level.server.getLevel(it) }
                ?: level.server.overworld()

            // Check if position is safe
            val pos = request.pos
            val targetPos = BlockPos.containing(pos.x + 0.5, pos.y + 0.0, pos.z + 0.5)

            // Find a safe spot nearby if needed
            val teleportPos = findSafeSpot(targetLevel, targetPos)

            // Perform teleport
            player.teleportTo(
                targetLevel,
                teleportPos.x + 0.5,
                teleportPos.y + 0.1,
                teleportPos.z + 0.5,
                player.yRot,
                player.xRot
            )

            // Check for sleeping player entity at location and replace if found
            findSleepingEntityAt(targetLevel, targetPos)?.let { sleepingEntity ->
                SleepingPlayerEntity.replaceWithPlayer(player, sleepingEntity)
            }

            true
        } catch (e: Exception) {
            Witchery.LOGGER.error("Failed to process teleport request for ${player.name.string}", e)
            false
        }
    }

    /**
     * Find a safe location to teleport to near the target position
     */
    private fun findSafeSpot(level: ServerLevel, pos: BlockPos): BlockPos {
        // First check if the original position is safe
        if (isSafeTeleportPos(level, pos)) {
            return pos
        }

        // Check a spiral pattern around the original position
        val radius = 5
        // First try at the same Y level, then above, then below
        val yOffsets = listOf(0, 1, 2, -1)

        for (y in yOffsets) {
            for (r in 0..radius) {
                for (x in -r..r) {
                    for (z in -r..r) {
                        // Only check positions on the edge of the current radius
                        if (abs(x) == r || abs(z) == r) {
                            val testPos = pos.offset(x, y, z)
                            if (isSafeTeleportPos(level, testPos)) {
                                return testPos
                            }
                        }
                    }
                }
            }
        }

        // If no safe position found, return original
        return pos
    }

    /**
     * Check if a position is safe for teleporting
     */
    private fun isSafeTeleportPos(level: ServerLevel, pos: BlockPos): Boolean {
        // Check if the player can stand at this position
        val state = level.getBlockState(pos)
        val stateAbove = level.getBlockState(pos.above())
        val stateAbove2 = level.getBlockState(pos.above(2)) // Check two blocks above for tall players
        val stateBelow = level.getBlockState(pos.below())

        // A safe position needs space for player (not colliding) and solid ground beneath
        return !state.isCollisionShapeFullBlock(level, pos) &&
                !stateAbove.isCollisionShapeFullBlock(level, pos.above()) &&
                !stateAbove2.isCollisionShapeFullBlock(level, pos.above(2)) &&
                stateBelow.isCollisionShapeFullBlock(level, pos.below()) &&
                !state.liquid() // Avoid liquids
    }

    /**
     * Find a sleeping player entity at the given position
     */
    private fun findSleepingEntityAt(level: ServerLevel, pos: BlockPos): SleepingPlayerEntity? {
        val box = AABB(
            pos.x - 1.0, pos.y - 1.0, pos.z - 1.0,
            pos.x + 1.0, pos.y + 1.0, pos.z + 1.0
        )

        return level.getEntitiesOfClass(SleepingPlayerEntity::class.java, box).firstOrNull()
    }

    /**
     * Add a teleport request to the queue
     */
    fun addRequest(level: ServerLevel, request: TeleportRequest) {
        try {
            val data = TeleportQueueLevelAttachment.getData(level)
            val requests = data.pendingTeleports.toMutableList()

            // Remove any existing requests for the same player
            requests.removeIf { it.player == request.player }

            // Add the new request
            requests.add(request)

            // Force the chunk to ensure it stays loaded
            level.setChunkForced(request.chunkPos.x, request.chunkPos.z, true)

            // Update the level data
            TeleportQueueLevelAttachment.setData(level, TeleportQueueLevelAttachment.Data(requests))

            Witchery.LOGGER.debug("Added teleport request for player ${request.player} to position ${request.pos}")
        } catch (e: Exception) {
            Witchery.LOGGER.error("Failed to add teleport request for ${request.player}", e)
        }
    }
}