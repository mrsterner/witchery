package dev.sterner.witchery.handler

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.teleport.TeleportQueueLevelAttachment
import dev.sterner.witchery.data_attachment.teleport.TeleportRequest
import dev.sterner.witchery.entity.player_shell.SleepingPlayerEntity
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.phys.AABB
import kotlin.math.abs

object TeleportQueueHandler {
    private const val PROCESS_INTERVAL = 10
    private const val MIN_WAIT_TICKS = 5
    private const val MAX_ATTEMPTS = 10
    private const val STALE_REQUEST_TIME = 6000L

    fun clearQueue(server: MinecraftServer) {
        server.allLevels.forEach { level ->
            val data = TeleportQueueLevelAttachment.getData(level)
            data.pendingTeleports.forEach { request ->
                level.setChunkForced(request.chunkPos.x, request.chunkPos.z, false)
            }

            TeleportQueueLevelAttachment.setData(level, TeleportQueueLevelAttachment.Data(mutableListOf()))
        }
    }

    /**
     * Process all pending teleport requests across all server levels
     */
    fun processQueue(server: MinecraftServer) {
        if (server.tickCount % PROCESS_INTERVAL != 0) return

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

        data.pendingTeleports.forEach { request ->
            try {
                if (currentTime - request.createdGameTime < MIN_WAIT_TICKS) return@forEach

                val player = level.server.playerList.getPlayer(request.player)

                if (player != null) {
                    val success = handleTeleport(player, request, level)

                    if (success) {
                        toRemove.add(request)
                        safelyUnforceChunk(level, request.chunkPos)
                    } else {
                        request.attempts++

                        if (request.attempts >= MAX_ATTEMPTS) {
                            toRemove.add(request)
                            safelyUnforceChunk(level, request.chunkPos)
                        }
                    }
                } else {
                    if (currentTime - request.createdGameTime > STALE_REQUEST_TIME) {
                        toRemove.add(request)
                        safelyUnforceChunk(level, request.chunkPos)
                    }
                }
            } catch (e: Exception) {
                toRemove.add(request)
                safelyUnforceChunk(level, request.chunkPos)
            }
        }

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
    private fun updatePendingTeleports(
        level: ServerLevel,
        data: TeleportQueueLevelAttachment.Data,
        toRemove: List<TeleportRequest>
    ) {
        val updatedRequests = data.pendingTeleports.toMutableList()
        updatedRequests.removeAll(toRemove)
        TeleportQueueLevelAttachment.setData(level, TeleportQueueLevelAttachment.Data(updatedRequests))
    }

    /**
     * Handle actual teleport operation for a player
     */
    private fun handleTeleport(player: ServerPlayer, request: TeleportRequest, level: ServerLevel): Boolean {
        val targetLevel = request.sourceDimension?.let { level.server.getLevel(it) }
            ?: level.server.overworld()

        val pos = request.pos
        val targetPos = BlockPos.containing(pos.x + 0.5, pos.y + 0.0, pos.z + 0.5)

        val teleportPos = findSafeSpot(targetLevel, targetPos)

        player.teleportTo(
            targetLevel,
            teleportPos.x + 0.5,
            teleportPos.y + 0.1,
            teleportPos.z + 0.5,
            player.yRot,
            player.xRot
        )

        val sleep = findSleepingEntityAt(targetLevel, targetPos)
        if (sleep != null) {
            SleepingPlayerEntity.replaceWithPlayer(player, sleep)
            return true
        } else {
            return false
        }
    }

    /**
     * Find a safe location to teleport to near the target position
     */
    private fun findSafeSpot(level: ServerLevel, pos: BlockPos): BlockPos {
        if (isSafeTeleportPos(level, pos)) {
            return pos
        }

        val radius = 5
        val yOffsets = listOf(0, 1, 2, -1)

        for (y in yOffsets) {
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

        return pos
    }

    /**
     * Check if a position is safe for teleporting
     */
    private fun isSafeTeleportPos(level: ServerLevel, pos: BlockPos): Boolean {
        val state = level.getBlockState(pos)
        val stateAbove = level.getBlockState(pos.above())
        val stateAbove2 = level.getBlockState(pos.above(2))
        val stateBelow = level.getBlockState(pos.below())

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
        val data = TeleportQueueLevelAttachment.getData(level)
        val requests = data.pendingTeleports.toMutableList()

        requests.removeIf { it.player == request.player }

        requests.add(request)

        level.setChunkForced(request.chunkPos.x, request.chunkPos.z, true)

        TeleportQueueLevelAttachment.setData(level, TeleportQueueLevelAttachment.Data(requests))
    }
}