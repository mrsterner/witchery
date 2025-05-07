package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.MutandisLevelAttachment
import dev.sterner.witchery.platform.MutandisLevelAttachment.CACHE_LIFETIME
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

@Suppress("UnstableApiUsage")
object MutandisLevelAttachmentImpl {

    @JvmStatic
    fun getMap(level: ServerLevel): MutableMap<BlockPos, MutandisLevelAttachment.MutandisData> {
        return level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.MUTANDIS_LEVEL_DATA_TYPE).mutandisCacheMap
    }

    @JvmStatic
    fun getTagForBlockPos(level: ServerLevel, pos: BlockPos): TagKey<Block>? {
        val attachments = level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.MUTANDIS_LEVEL_DATA_TYPE)
        return attachments.mutandisCacheMap[pos]?.tag
    }

    @JvmStatic
    fun setTagForBlockPos(level: ServerLevel, pos: BlockPos, tag: TagKey<Block>) {
        val data = level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.MUTANDIS_LEVEL_DATA_TYPE)
        val mutableMap = data.mutandisCacheMap.toMutableMap()
        mutableMap[pos] = MutandisLevelAttachment.MutandisData(tag, CACHE_LIFETIME)
        data.mutandisCacheMap = mutableMap.toMutableMap()
        level.setAttached(WitcheryFabricAttachmentRegistry.MUTANDIS_LEVEL_DATA_TYPE, data)
    }

    @JvmStatic
    fun removeTagForBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.MUTANDIS_LEVEL_DATA_TYPE)
        data.mutandisCacheMap.remove(pos)
        level.setAttached(WitcheryFabricAttachmentRegistry.MUTANDIS_LEVEL_DATA_TYPE, data)
    }

    @JvmStatic
    fun updateTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.MUTANDIS_LEVEL_DATA_TYPE)
        if (data.mutandisCacheMap[pos] != null) {
            data.mutandisCacheMap[pos] = MutandisLevelAttachment.MutandisData(
                data.mutandisCacheMap[pos]!!.tag,
                data.mutandisCacheMap[pos]!!.time - 1
            )
            level.setAttached(WitcheryFabricAttachmentRegistry.MUTANDIS_LEVEL_DATA_TYPE, data)
        }
    }

    @JvmStatic
    fun resetTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.MUTANDIS_LEVEL_DATA_TYPE)
        if (data.mutandisCacheMap[pos] != null) {
            data.mutandisCacheMap[pos] =
                MutandisLevelAttachment.MutandisData(data.mutandisCacheMap[pos]!!.tag, CACHE_LIFETIME)
            level.setAttached(WitcheryFabricAttachmentRegistry.MUTANDIS_LEVEL_DATA_TYPE, data)
        }
    }
}