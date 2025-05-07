package dev.sterner.witchery.handler

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.sleeping_player.SleepingPlayerEntity
import dev.sterner.witchery.platform.TeleportQueueLevelAttachment
import dev.sterner.witchery.platform.TeleportRequest
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Containers
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.phys.AABB
import kotlin.math.abs

object TeleportQueueHandler {
    /**
     * Process all pending teleport requests across all server levels
     */
    fun processQueue(server: MinecraftServer) {
        // Only process every 10 ticks (every half second)
        if (server.tickCount % 10 != 0) return

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
            // Skip if request is too new (wait time 5 ticks)
            if (currentTime - request.createdGameTime < 5) return@forEach

            // Get player if online
            val player = level.server.playerList.getPlayer(request.player)

            if (player != null) {
                // Player is online, perform teleport
                val success = handleTeleport(player, request, level)

                if (success) {
                    // If teleport succeeded, add to removal list
                    toRemove.add(request)

                    // Unforce chunk if it was forced
                    level.setChunkForced(request.chunkPos.x, request.chunkPos.z, false)
                } else {
                    // Failed teleport, increment attempts
                    request.attempts++

                    // Remove after 5 failed attempts
                    if (request.attempts >= 5) {
                        toRemove.add(request)
                        level.setChunkForced(request.chunkPos.x, request.chunkPos.z, false)
                        Witchery.LOGGER.warn("Failed to teleport player ${request.player} after ${request.attempts} attempts")
                    }
                }
            } else {
                // Player is offline

                // Remove old requests (after 5 minutes)
                if (currentTime - request.createdGameTime > 6000) {
                    toRemove.add(request)
                    level.setChunkForced(request.chunkPos.x, request.chunkPos.z, false)
                    Witchery.LOGGER.debug("Removed stale teleport request for offline player ${request.player}")
                }
            }
        }

        // Remove processed requests
        if (toRemove.isNotEmpty()) {
            val updatedRequests = data.pendingTeleports.toMutableList()
            updatedRequests.removeAll(toRemove)
            TeleportQueueLevelAttachment.setData(level, TeleportQueueLevelAttachment.Data(updatedRequests))
        }
    }

    /**
     * Handle actual teleport operation for a player
     */
    private fun handleTeleport(player: ServerPlayer, request: TeleportRequest, level: ServerLevel): Boolean {
        return try {
            // Determine target level - overworld by default
            val targetLevel = if (request.sourceDimension != null) {
                level.server.getLevel(request.sourceDimension)
            } else {
                level.server.overworld()
            } ?: level.server.overworld()

            // Check if position is safe
            val pos = request.pos
            val targetPos = BlockPos.containing(pos.x + 0.5, pos.y + 0.0, pos.z + 0.5)

            // If target position is occupied, find a safe spot nearby
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

            // Check for sleeping player entity at location
            val sleepingEntity = findSleepingEntityAt(targetLevel, targetPos)
            if (sleepingEntity != null) {
                // Replace player with sleeping entity's data
                SleepingPlayerEntity.replaceWithPlayer(player, sleepingEntity)
            }

            true
        } catch (e: Exception) {
            Witchery.LOGGER.error("Failed to process teleport request", e)
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
        for (y in -1..2) {
            for (r in 0..radius) {
                for (x in -r..r) {
                    for (z in -r..r) {
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

        // If no safe position found, return original (better than nothing)
        return pos
    }

    /**
     * Check if a position is safe for teleporting
     */
    private fun isSafeTeleportPos(level: ServerLevel, pos: BlockPos): Boolean {
        val state = level.getBlockState(pos)
        val stateAbove = level.getBlockState(pos.above())
        val stateBelow = level.getBlockState(pos.below())

        return !state.isCollisionShapeFullBlock(level, pos) &&
                !stateAbove.isCollisionShapeFullBlock(level, pos.above()) &&
                stateBelow.isCollisionShapeFullBlock(level, pos.below())
    }

    /**
     * Find a sleeping player entity at the given position
     */
    private fun findSleepingEntityAt(level: ServerLevel, pos: BlockPos): SleepingPlayerEntity? {
        val box = AABB(
            pos.x - 1.0, pos.y - 1.0, pos.z - 1.0,
            pos.x + 1.0, pos.y + 1.0, pos.z + 1.0
        )

        val entities = level.getEntitiesOfClass(SleepingPlayerEntity::class.java, box)
        return entities.firstOrNull()
    }

    /**
     * Add a teleport request to the queue
     */
    fun addRequest(level: ServerLevel, request: TeleportRequest) {
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
    }
}