package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel

object AltarLevelAttachment {

    @JvmStatic
    fun setAltarPos(level: ServerLevel, pos: BlockPos) {
        if (!level.getData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT).altarSet.contains(pos)) {
            val data = level.getData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT)
            data.altarSet.add(pos)
            level.setData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT, data)
        }
    }

    @JvmStatic
    fun removeAltarPos(level: ServerLevel, pos: BlockPos) {
        val data = level.getData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT)
        data.altarSet.remove(pos)
        level.setData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT, data)
    }

    @JvmStatic
    fun getAltarPos(level: ServerLevel): MutableSet<BlockPos> {
        return level.getData(WitcheryNeoForgeAttachmentRegistry.ALTAR_LEVEL_DATA_ATTACHMENT).altarSet
    }

    data class AltarDataCodec(val altarSet: MutableSet<BlockPos> = mutableSetOf()) {
        companion object {
            val CODEC: Codec<AltarDataCodec> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(BlockPos.CODEC).xmap({ it.toMutableSet() }, { it.toList() })
                        .fieldOf("altarSet").forGetter { it.altarSet }
                ).apply(instance, ::AltarDataCodec)
            }
            val ID: ResourceLocation = Witchery.id("altar_level_data")
        }
    }
}