package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block
import kotlin.text.set

object MutandisLevelAttachment {

    const val CACHE_LIFETIME = 20 * 3

    @JvmStatic
    fun getMap(level: ServerLevel): MutableMap<BlockPos, MutandisLevelAttachment.MutandisData> {
        return level.getData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT).mutandisCacheMap
    }

    @JvmStatic
    fun getTagForBlockPos(level: ServerLevel, pos: BlockPos): TagKey<Block>? {
        val levelData = level.getData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT.get())
            ?: MutandisLevelAttachment.MutandisDataCodec()
        return levelData.mutandisCacheMap[pos]?.tag
    }

    @JvmStatic
    @Suppress("UnstableApiUsage")
    fun setTagForBlockPos(level: ServerLevel, pos: BlockPos, tag: TagKey<Block>) {
        val data = level.getData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT)
        val mutableMap = data.mutandisCacheMap.toMutableMap()
        mutableMap[pos] = MutandisLevelAttachment.MutandisData(tag, CACHE_LIFETIME)
        data.mutandisCacheMap = mutableMap.toMutableMap()
        level.setData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT, data)
    }

    @JvmStatic
    fun removeTagForBlockPos(level: ServerLevel, pos: BlockPos) {
        val levelData = level.getData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT)
        levelData.mutandisCacheMap.remove(pos)
        level.setData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT, levelData)
    }

    @JvmStatic
    fun updateTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT)
        if (data.mutandisCacheMap[pos] != null) {
            data.mutandisCacheMap[pos] = MutandisLevelAttachment.MutandisData(
                data.mutandisCacheMap[pos]!!.tag,
                data.mutandisCacheMap[pos]!!.time - 1
            )
            level.setData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT, data)
        }
    }

    @JvmStatic
    fun resetTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT)
        if (data.mutandisCacheMap[pos] != null) {
            data.mutandisCacheMap[pos] =
                MutandisLevelAttachment.MutandisData(data.mutandisCacheMap[pos]!!.tag, CACHE_LIFETIME)
            level.setData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT, data)
        }
    }

    val ID: ResourceLocation = Witchery.id("mutandis_level_data")

    data class MutandisData(val tag: TagKey<Block>, val time: Int) {
        companion object {
            val MUTANDIS_DATA_CODEC: Codec<MutandisData> = RecordCodecBuilder.create { inst ->
                inst.group(
                    TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(MutandisData::tag),
                    Codec.INT.fieldOf("time").forGetter(MutandisData::time)
                ).apply(inst, ::MutandisData)
            }
        }
    }

    data class Data(var mutandisCacheMap: MutableMap<BlockPos, MutandisData> = mutableMapOf()) {
        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { inst ->
                inst.group(
                    Codec.unboundedMap(
                        BlockPos.CODEC,
                        MutandisData.MUTANDIS_DATA_CODEC
                    ).fieldOf("mutandisCacheMap")
                        .forGetter(Data::mutandisCacheMap)
                ).apply(inst, ::Data)
            }
        }
    }
}