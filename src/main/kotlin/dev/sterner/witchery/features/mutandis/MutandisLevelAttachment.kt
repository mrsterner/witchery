package dev.sterner.witchery.features.mutandis

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

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.MUTANDIS_LEVEL_DATA_ATTACHMENT, data)
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