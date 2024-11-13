package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.MutandisLevelAttachment
import dev.sterner.witchery.platform.MutandisLevelAttachment.CACHE_LIFETIME
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object MutandisLevelAttachmentImpl {

    @JvmStatic
    fun getMap(level: ServerLevel): MutableMap<BlockPos, MutandisLevelAttachment.MutandisData> {
        return level.getData(WitcheryNeoForgeAttachmentRegistry.MUTANDIS_LEVEL_DATA_ATTACHMENT).mutandisCacheMap
    }

    @JvmStatic
    fun getTagForBlockPos(level: ServerLevel, pos: BlockPos): TagKey<Block>? {
        val levelData = level.getData(WitcheryNeoForgeAttachmentRegistry.MUTANDIS_LEVEL_DATA_ATTACHMENT.get())
            ?: MutandisLevelAttachment.MutandisDataCodec()
        return levelData.mutandisCacheMap[pos]?.tag
    }

    @JvmStatic
    @Suppress("UnstableApiUsage")
    fun setTagForBlockPos(level: ServerLevel, pos: BlockPos, tag: TagKey<Block>) {
        val data = level.getData(WitcheryNeoForgeAttachmentRegistry.MUTANDIS_LEVEL_DATA_ATTACHMENT)
        val mutableMap = data.mutandisCacheMap.toMutableMap()
        mutableMap[pos] = MutandisLevelAttachment.MutandisData(tag, CACHE_LIFETIME)
        data.mutandisCacheMap = mutableMap.toMutableMap()
        level.setData(WitcheryNeoForgeAttachmentRegistry.MUTANDIS_LEVEL_DATA_ATTACHMENT, data)
    }

    @JvmStatic
    fun removeTagForBlockPos(level: ServerLevel, pos: BlockPos) {
        val levelData = level.getData(WitcheryNeoForgeAttachmentRegistry.MUTANDIS_LEVEL_DATA_ATTACHMENT)
        levelData.mutandisCacheMap.remove(pos)
        level.setData(WitcheryNeoForgeAttachmentRegistry.MUTANDIS_LEVEL_DATA_ATTACHMENT, levelData)
    }

    @JvmStatic
    fun updateTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getData(WitcheryNeoForgeAttachmentRegistry.MUTANDIS_LEVEL_DATA_ATTACHMENT)
        if (data.mutandisCacheMap[pos] != null) {
            data.mutandisCacheMap[pos] = MutandisLevelAttachment.MutandisData(
                data.mutandisCacheMap[pos]!!.tag,
                data.mutandisCacheMap[pos]!!.time - 1
            )
            level.setData(WitcheryNeoForgeAttachmentRegistry.MUTANDIS_LEVEL_DATA_ATTACHMENT, data)
        }
    }

    @JvmStatic
    fun resetTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getData(WitcheryNeoForgeAttachmentRegistry.MUTANDIS_LEVEL_DATA_ATTACHMENT)
        if (data.mutandisCacheMap[pos] != null) {
            data.mutandisCacheMap[pos] =
                MutandisLevelAttachment.MutandisData(data.mutandisCacheMap[pos]!!.tag, CACHE_LIFETIME)
            level.setData(WitcheryNeoForgeAttachmentRegistry.MUTANDIS_LEVEL_DATA_ATTACHMENT, data)
        }
    }
}