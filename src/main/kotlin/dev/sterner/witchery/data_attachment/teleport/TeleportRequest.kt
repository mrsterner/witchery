package dev.sterner.witchery.data_attachment.teleport

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import java.util.*

data class TeleportRequest(
    val player: UUID,                         // UUID of the player to teleport
    val pos: BlockPos,                        // Target position to teleport to
    val chunkPos: ChunkPos,                   // Chunk position to ensure is loaded
    val createdGameTime: Long,                // Game time when request was created
    var attempts: Int = 0,                    // Number of processing attempts made
    val sourceDimension: ResourceKey<Level>? = null // Source dimension, if cross-dimensional
) {
    companion object {
        /**
         * Codec for serializing/deserializing TeleportRequest objects
         */
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