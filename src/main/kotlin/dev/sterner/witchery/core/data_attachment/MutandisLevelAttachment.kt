package dev.sterner.witchery.core.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object MutandisLevelAttachment {

    const val CACHE_LIFETIME = 20 * 3

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT, data)
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

    data class MutandisData(val tag: TagKey<Block>, val time: Int) {

        companion object {
            val CODEC: Codec<MutandisData> = RecordCodecBuilder.create { instance ->
                instance.group(
                    TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter { it.tag },
                    Codec.INT.fieldOf("time").forGetter { it.time }
                ).apply(instance, ::MutandisData)
            }
        }
    }

    data class Data(val mutandisCacheMap: MutableMap<BlockPos, MutandisData> = mutableMapOf()) {

        companion object {
            val ID: ResourceLocation = Witchery.id("mutandis_level_data")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.unboundedMap(BlockPos.CODEC, MutandisData.CODEC)
                        .fieldOf("mutandisCacheMap")
                        .forGetter { it.mutandisCacheMap }
                ).apply(instance, ::Data)
            }
        }
    }
}