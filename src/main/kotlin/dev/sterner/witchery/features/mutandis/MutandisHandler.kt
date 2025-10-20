package dev.sterner.witchery.features.mutandis

import dev.sterner.witchery.features.mutandis.MutandisLevelAttachment.MutandisData
import dev.sterner.witchery.features.mutandis.MutandisLevelAttachment.getData
import dev.sterner.witchery.features.mutandis.MutandisLevelAttachment.setData
import dev.sterner.witchery.network.MutandisRemenantParticleS2CPayload
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.network.PacketDistributor

object MutandisHandler {

    const val CACHE_LIFETIME = 20 * 3


    fun tick(level: Level) {
        if (level.isClientSide) return

        val serverLevel = level as ServerLevel

        val toRemove = mutableListOf<BlockPos>()

        val iterator = getMap(serverLevel).iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val (pos, mutandisData) = entry
            val (_, time) = mutandisData
            if (time <= 1) {
                toRemove.add(pos)
            } else {
                PacketDistributor.sendToPlayersTrackingChunk(
                    serverLevel,
                    ChunkPos(pos),
                    MutandisRemenantParticleS2CPayload(pos)
                )
                updateTimeForTagBlockPos(serverLevel, pos)
            }
        }

        for (pos in toRemove) {
            removeTagForBlockPos(serverLevel, pos)
        }
    }


    @JvmStatic
    fun getMap(level: ServerLevel): MutableMap<BlockPos, MutandisData> {
        return getData(level).mutandisCacheMap
    }

    @JvmStatic
    fun getTagForBlockPos(level: ServerLevel, pos: BlockPos): TagKey<Block>? {
        return getData(level).mutandisCacheMap[pos]?.tag
    }

    @JvmStatic
    fun setTagForBlockPos(level: ServerLevel, pos: BlockPos, tag: TagKey<Block>) {
        val data = getData(level)
        val updatedMap = data.mutandisCacheMap.toMutableMap()
        updatedMap[pos] = MutandisData(tag, CACHE_LIFETIME)
        setData(level, data.copy(mutandisCacheMap = updatedMap))
    }

    @JvmStatic
    fun removeTagForBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = getData(level)
        val updatedMap = data.mutandisCacheMap.toMutableMap()
        updatedMap.remove(pos)
        setData(level, data.copy(mutandisCacheMap = updatedMap))
    }

    @JvmStatic
    fun updateTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = getData(level)
        val existingData = data.mutandisCacheMap[pos] ?: return

        val updatedMap = data.mutandisCacheMap.toMutableMap()
        updatedMap[pos] = existingData.copy(time = existingData.time - 1)
        setData(level, data.copy(mutandisCacheMap = updatedMap))
    }

    @JvmStatic
    fun resetTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = getData(level)
        val existingData = data.mutandisCacheMap[pos] ?: return

        val updatedMap = data.mutandisCacheMap.toMutableMap()
        updatedMap[pos] = existingData.copy(time = CACHE_LIFETIME)
        setData(level, data.copy(mutandisCacheMap = updatedMap))
    }
}