package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForge.ATTACHMENT_TYPES
import dev.sterner.witchery.platform.MutandisDataAttachment
import dev.sterner.witchery.platform.MutandisDataAttachment.CACHE_LIFETIME
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.attachment.AttachmentType
import java.util.function.Supplier

object MutandisDataAttachmentImpl {

    @JvmStatic
    fun getMap(level: ServerLevel): MutableMap<BlockPos, MutandisDataAttachment.MutandisData> {
        return level.getData(LEVEL_DATA_ATTACHMENT).mutandisCacheMap
    }

    @JvmStatic
    fun getTagForBlockPos(level: ServerLevel, pos: BlockPos): TagKey<Block>? {
        val levelData = level.getData(LEVEL_DATA_ATTACHMENT.get()) ?: MutandisDataAttachment.MutandisDataCodec()
        return levelData.mutandisCacheMap[pos]?.tag
    }

    @JvmStatic
    @Suppress("UnstableApiUsage")
    fun setTagForBlockPos(level: ServerLevel, pos: BlockPos, tag: TagKey<Block>) {
        val data = level.getData(LEVEL_DATA_ATTACHMENT)
        val mutableMap = data.mutandisCacheMap.toMutableMap()
        mutableMap[pos] = MutandisDataAttachment.MutandisData(tag, CACHE_LIFETIME)
        data.mutandisCacheMap = mutableMap.toMutableMap()
        level.setData(LEVEL_DATA_ATTACHMENT, data)
    }

    @JvmStatic
    fun removeTagForBlockPos(level: ServerLevel, pos: BlockPos)  {
        val levelData = level.getData(LEVEL_DATA_ATTACHMENT)
        levelData.mutandisCacheMap.remove(pos)
        level.setData(LEVEL_DATA_ATTACHMENT, levelData)
    }

    @JvmStatic
    fun updateTimeForTagBlockPos(level: ServerLevel, pos: BlockPos)  {
        val data = level.getData(LEVEL_DATA_ATTACHMENT)
        if (data.mutandisCacheMap[pos] != null) {
            data.mutandisCacheMap[pos] = MutandisDataAttachment.MutandisData(data.mutandisCacheMap[pos]!!.tag, data.mutandisCacheMap[pos]!!.time - 1)
            level.setData(LEVEL_DATA_ATTACHMENT, data)
        }
    }

    @JvmStatic
    fun resetTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getData(LEVEL_DATA_ATTACHMENT)
        if (data.mutandisCacheMap[pos] != null) {
            data.mutandisCacheMap[pos] = MutandisDataAttachment.MutandisData(data.mutandisCacheMap[pos]!!.tag, CACHE_LIFETIME)
            level.setData(LEVEL_DATA_ATTACHMENT, data)
        }
    }


    @JvmStatic
    val LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<MutandisDataAttachment.MutandisDataCodec>> = ATTACHMENT_TYPES.register(
        "mutandis_level_data",
        Supplier {
            AttachmentType.builder(Supplier { MutandisDataAttachment.MutandisDataCodec() })
                .serialize(MutandisDataAttachment.MutandisDataCodec.CODEC)
                .build()
        }
    )
}