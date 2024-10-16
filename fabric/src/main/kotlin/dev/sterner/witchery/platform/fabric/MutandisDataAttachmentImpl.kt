package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.WitcheryFabric
import dev.sterner.witchery.platform.MutandisDataAttachment
import dev.sterner.witchery.platform.MutandisDataAttachment.CACHE_LIFETIME
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object MutandisDataAttachmentImpl {

    @JvmStatic
    @Suppress("UnstableApiUsage")
    fun getMap(level: ServerLevel): MutableMap<BlockPos, MutandisDataAttachment.MutandisData> {
        return level.getAttachedOrCreate(WitcheryFabric.MUTANDIS_LEVEL_DATA_TYPE).mutandisCacheMap
    }

    @JvmStatic
    @Suppress("UnstableApiUsage")
    fun getTagForBlockPos(level: ServerLevel, pos: BlockPos): TagKey<Block>? {
        val attachments = level.getAttachedOrCreate(WitcheryFabric.MUTANDIS_LEVEL_DATA_TYPE)
        return attachments.mutandisCacheMap[pos]?.tag
    }

    @JvmStatic
    @Suppress("UnstableApiUsage")
    fun setTagForBlockPos(level: ServerLevel, pos: BlockPos, tag: TagKey<Block>) {
        val data = level.getAttachedOrCreate(WitcheryFabric.MUTANDIS_LEVEL_DATA_TYPE)
        val mutableMap = data.mutandisCacheMap.toMutableMap()
        mutableMap[pos] = MutandisDataAttachment.MutandisData(tag, CACHE_LIFETIME)
        data.mutandisCacheMap = mutableMap.toMutableMap()
        level.setAttached(WitcheryFabric.MUTANDIS_LEVEL_DATA_TYPE, data)
    }

    @JvmStatic
    @Suppress("UnstableApiUsage")
    fun removeTagForBlockPos(level: ServerLevel, pos: BlockPos)  {
        val data = level.getAttachedOrCreate(WitcheryFabric.MUTANDIS_LEVEL_DATA_TYPE)
        data.mutandisCacheMap.remove(pos)
        level.setAttached(WitcheryFabric.MUTANDIS_LEVEL_DATA_TYPE, data)
    }

    @JvmStatic
    @Suppress("UnstableApiUsage")
    fun updateTimeForTagBlockPos(level: ServerLevel, pos: BlockPos)  {
        val data = level.getAttachedOrCreate(WitcheryFabric.MUTANDIS_LEVEL_DATA_TYPE)
        if (data.mutandisCacheMap[pos] != null) {
            data.mutandisCacheMap[pos] = MutandisDataAttachment.MutandisData(data.mutandisCacheMap[pos]!!.tag, data.mutandisCacheMap[pos]!!.time - 1)
            level.setAttached(WitcheryFabric.MUTANDIS_LEVEL_DATA_TYPE, data)
        }
    }

    @JvmStatic
    @Suppress("UnstableApiUsage")
    fun resetTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getAttachedOrCreate(WitcheryFabric.MUTANDIS_LEVEL_DATA_TYPE)
        if (data.mutandisCacheMap[pos] != null) {
            data.mutandisCacheMap[pos] = MutandisDataAttachment.MutandisData(data.mutandisCacheMap[pos]!!.tag, CACHE_LIFETIME)
            level.setAttached(WitcheryFabric.MUTANDIS_LEVEL_DATA_TYPE, data)
        }
    }
}