package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object MutandisLevelAttachment {

    const val CACHE_LIFETIME = 20 * 3

    @JvmStatic
    @ExpectPlatform
    fun getMap(level: ServerLevel): MutableMap<BlockPos, MutandisData> {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getTagForBlockPos(level: ServerLevel, pos: BlockPos): TagKey<Block>? {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun setTagForBlockPos(level: ServerLevel, pos: BlockPos, tag: TagKey<Block>) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun removeTagForBlockPos(level: ServerLevel, pos: BlockPos) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun updateTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun resetTimeForTagBlockPos(level: ServerLevel, pos: BlockPos) {
        throw AssertionError()
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

    data class MutandisDataCodec(var mutandisCacheMap: MutableMap<BlockPos, MutandisData> = mutableMapOf()) {
        companion object {
            val CODEC: Codec<MutandisDataCodec> = RecordCodecBuilder.create { inst ->
                inst.group(
                    Codec.unboundedMap(
                        BlockPos.CODEC,
                        MutandisData.MUTANDIS_DATA_CODEC
                    ).fieldOf("mutandisCacheMap")
                        .forGetter(MutandisDataCodec::mutandisCacheMap)
                ).apply(inst, ::MutandisDataCodec)
            }
        }
    }
}