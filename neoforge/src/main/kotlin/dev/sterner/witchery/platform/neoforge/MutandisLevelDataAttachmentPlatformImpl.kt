package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.api.attachment.MutandisData
import dev.sterner.witchery.api.attachment.MutandisAttachmentData
import dev.sterner.witchery.platform.MutandisLevelDataAttachmentPlatform
import dev.sterner.witchery.platform.MutandisLevelDataAttachmentPlatform.CACHE_LIFETIME
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.attachment.AttachmentType
import net.neoforged.neoforge.registries.DeferredRegister
import net.neoforged.neoforge.registries.NeoForgeRegistries
import java.util.function.Supplier

object MutandisLevelDataAttachmentPlatformImpl {

    @JvmStatic
    fun getMap(level: ServerLevel): MutableMap<BlockPos, MutandisData> {
        return level.getData(LEVEL_DATA_ATTACHMENT).mutandisCacheMap
    }

    @JvmStatic
    fun getTagForBlockPos(level: ServerLevel, pos: BlockPos): TagKey<Block>? {
        val levelData = level.getData(LEVEL_DATA_ATTACHMENT.get()) ?: MutandisAttachmentData()
        return levelData.mutandisCacheMap[pos]?.tag
    }

    @JvmStatic
    fun setTagForBlockPos(level: ServerLevel, pos: BlockPos, tag: TagKey<Block>)  {
        val levelData = level.getData(LEVEL_DATA_ATTACHMENT.get()) ?: MutandisAttachmentData()
        levelData.mutandisCacheMap[pos] = MutandisData(tag, CACHE_LIFETIME)
    }

    @JvmStatic
    fun removeTagForBlockPos(level: ServerLevel, pos: BlockPos)  {
        level.getData(LEVEL_DATA_ATTACHMENT).mutandisCacheMap.remove(pos)
    }

    @JvmStatic
    fun updateTimeForTagBlockPos(level: ServerLevel, pos: BlockPos)  {
        val data = level.getData(LEVEL_DATA_ATTACHMENT).mutandisCacheMap[pos]
        if (data != null) {
            level.getData(LEVEL_DATA_ATTACHMENT).mutandisCacheMap[pos] =
                MutandisData(data.tag, data.time - 1)
        }
    }

    @JvmStatic
    fun resetTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getData(LEVEL_DATA_ATTACHMENT).mutandisCacheMap[pos]
        if (data != null) {
            level.getData(LEVEL_DATA_ATTACHMENT).mutandisCacheMap[pos] = MutandisData(data.tag, CACHE_LIFETIME)
        }
    }

    @JvmStatic
        val ATTACHMENT_TYPES: DeferredRegister<AttachmentType<*>> =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Witchery.MODID)

    @JvmStatic
    val LEVEL_DATA_ATTACHMENT: Supplier<AttachmentType<MutandisAttachmentData>> = ATTACHMENT_TYPES.register(
        "level_data",
        Supplier {
            AttachmentType.builder(Supplier { MutandisAttachmentData() })
                .serialize(MutandisLevelDataAttachmentPlatform.CODEC)
                .build()
        }
    )
}