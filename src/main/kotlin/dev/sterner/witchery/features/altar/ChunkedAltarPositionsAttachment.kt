package dev.sterner.witchery.features.altar

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import kotlin.math.ceil

object ChunkedAltarPositionsAttachment {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.CHUNKED_ALTAR_POSITIONS_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.CHUNKED_ALTAR_POSITIONS_ATTACHMENT, data)
    }

    @JvmStatic
    fun registerAltar(level: ServerLevel, pos: BlockPos) {
        val data = getData(level)
        val chunkPosLong = ChunkPos.asLong(pos)

        val currentPositions = data.chunkMap[chunkPosLong] ?: emptyList()
        if (!currentPositions.contains(pos)) {
            val updatedMap = data.chunkMap.toMutableMap()
            updatedMap[chunkPosLong] = currentPositions + pos
            setData(level, Data(updatedMap))
        }
    }

    @JvmStatic
    fun unregisterAltar(level: ServerLevel, pos: BlockPos) {
        val data = getData(level)
        val chunkPosLong = ChunkPos.asLong(pos)

        val currentPositions = data.chunkMap[chunkPosLong] ?: return
        if (currentPositions.contains(pos)) {
            val updatedMap = data.chunkMap.toMutableMap()
            val newPositions = currentPositions - pos
            if (newPositions.isEmpty()) {
                updatedMap.remove(chunkPosLong)
            } else {
                updatedMap[chunkPosLong] = newPositions
            }
            setData(level, Data(updatedMap))
        }
    }

    @JvmStatic
    fun findNearbyAltars(level: ServerLevel, pos: BlockPos, radius: Int): List<BlockPos> {
        val data = getData(level)
        val radiusSq = radius * radius
        val chunkRadius = ceil(radius / 16.0).toInt()
        val centerChunk = ChunkPos(pos)

        val nearbyAltars = mutableListOf<BlockPos>()

        for (xOffset in -chunkRadius..chunkRadius) {
            for (zOffset in -chunkRadius..chunkRadius) {
                val chunkX = centerChunk.x + xOffset
                val chunkZ = centerChunk.z + zOffset

                val chunkCenterX = chunkX * 16 + 8
                val chunkCenterZ = chunkZ * 16 + 8
                val chunkDx = chunkCenterX - pos.x
                val chunkDz = chunkCenterZ - pos.z
                val chunkDistSq = chunkDx * chunkDx + chunkDz * chunkDz

                val maxChunkOffset = 11
                if (chunkDistSq > (radius + maxChunkOffset) * (radius + maxChunkOffset)) {
                    continue
                }

                val chunkPosLong = ChunkPos.asLong(chunkX, chunkZ)
                data.chunkMap[chunkPosLong]?.forEach { altarPos ->
                    if (altarPos.distSqr(pos) <= radiusSq) {
                        nearbyAltars.add(altarPos)
                    }
                }
            }
        }
        return nearbyAltars
    }

    data class Data(val chunkMap: Map<Long, List<BlockPos>> = emptyMap()) {
        companion object {
            val ID: ResourceLocation = Witchery.id("chunked_altar_positions")

            private val CHUNK_ENTRY_CODEC: Codec<Pair<Long, List<BlockPos>>> = RecordCodecBuilder.create { inst ->
                inst.group(
                    Codec.LONG.fieldOf("chunkPos").forGetter(Pair<Long, List<BlockPos>>::first),
                    BlockPos.CODEC.listOf().fieldOf("positions").forGetter(Pair<Long, List<BlockPos>>::second)
                ).apply(inst, ::Pair)
            }

            val DATA_CODEC: Codec<Data> = CHUNK_ENTRY_CODEC.listOf()
                .xmap(
                    { list -> Data(list.toMap()) },
                    { data -> data.chunkMap.entries.map { it.key to it.value } }
                )
        }
    }
}