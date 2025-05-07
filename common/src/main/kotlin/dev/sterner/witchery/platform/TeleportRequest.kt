package dev.sterner.witchery.platform

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.sleeping_player.SleepingPlayerEntity
import dev.sterner.witchery.entity.sleeping_player.SleepingPlayerEntity.Companion.replaceWithPlayer
import dev.sterner.witchery.handler.SleepingPlayerHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import java.util.*

data class TeleportRequest(
    val player: UUID,                // UUID of the player to teleport
    val pos: BlockPos,               // Target position to teleport to
    val chunkPos: ChunkPos,          // Chunk position to ensure is loaded
    val createdGameTime: Long,       // Game time when request was created
    var attempts: Int = 0,           // Number of processing attempts made
    val sourceDimension: ResourceKey<Level>? = null // Source dimension, if cross-dimensional
) {
    /**
     * Check if the request has timed out based on game ticks or attempts
     */
    fun hasTimedOut(currentGameTime: Long): Boolean {
        // Timeout after 12000 game ticks (10 minutes at 20tps) or 200 attempts
        return (currentGameTime - createdGameTime > 12000) || attempts > 200
    }

    fun execute(minecraftServer: MinecraftServer): Boolean {
        attempts++
        val serverPlayer = minecraftServer.playerList.getPlayer(player)
        if (serverPlayer != null) {
            val overworld = minecraftServer.overworld()
            try {
                serverPlayer.teleportTo(
                    overworld,
                    pos.x + 0.5,
                    pos.y + 0.5,
                    pos.z + 0.5,
                    setOf(),
                    serverPlayer.yRot,
                    serverPlayer.xRot
                )
                val sleepingData = SleepingPlayerHandler.getPlayerFromSleeping(serverPlayer.uuid, overworld)
                if (sleepingData != null) {
                    val sleepingPlayer = overworld.getEntity(sleepingData.uuid)
                    if (sleepingPlayer is SleepingPlayerEntity) {
                        replaceWithPlayer(serverPlayer, sleepingPlayer)
                    } else {
                        Witchery.LOGGER.warn("Failed to find sleeping player entity for UUID: ${sleepingData.uuid}")
                    }
                }
                return true
            } catch (e: Exception) {
                Witchery.LOGGER.error("Failed to execute teleport request for player ${serverPlayer.name.string}", e)
                return false
            }
        }
        return hasTimedOut(minecraftServer.overworld().gameTime)
    }

    companion object {
        val CODEC: Codec<TeleportRequest> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codecs.UUID.fieldOf("playerUUID").forGetter { it.player },
                BlockPos.CODEC.fieldOf("pos").forGetter { it.pos },
                Codec.LONG.fieldOf("chunkPos").forGetter { it.chunkPos.toLong() },
                Codec.LONG.fieldOf("createdGameTime").forGetter { it.createdGameTime },
                Codec.INT.fieldOf("attempts").forGetter { it.attempts },
                ResourceLocation.CODEC.optionalFieldOf("sourceDimension")
                    .xmap(
                        { optional -> optional.map { ResourceKey.create(Registries.DIMENSION, it) }.orElse(null) },
                        { key -> Optional.ofNullable(key?.location()) }
                    ).forGetter { it.sourceDimension }
            ).apply(instance) { playerUUID, pos, chunkPos, createdGameTime, attempts, sourceDimension ->
                TeleportRequest(playerUUID, pos, ChunkPos(chunkPos), createdGameTime, attempts, sourceDimension)
            }
        }
    }
}