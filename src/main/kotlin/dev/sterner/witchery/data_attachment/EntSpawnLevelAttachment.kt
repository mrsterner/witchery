package dev.sterner.witchery.data_attachment

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState

object EntSpawnLevelAttachment {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryNeoForgeAttachmentRegistry.ENT_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryNeoForgeAttachmentRegistry.ENT_DATA_ATTACHMENT, data)
    }

    data class Data(val entries: List<BlockEntry> = listOf()) {
        companion object {
            val ID: ResourceLocation = Witchery.id("ent_spawn")
            val DATA_CODEC: Codec<Data> =
                BlockEntry.CODEC.listOf().fieldOf("entries").xmap(::Data, Data::entries).codec()
        }
    }

    data class BlockEntry(val blockPos: BlockPos, val state: BlockState, val count: Int, val resetTimer: Int) {
        companion object {
            val CODEC: Codec<BlockEntry> = RecordCodecBuilder.create { inst ->
                inst.group(
                    BlockPos.CODEC.fieldOf("blockPos").forGetter(BlockEntry::blockPos),
                    BlockState.CODEC.fieldOf("state").forGetter(BlockEntry::state),
                    Codec.INT.fieldOf("count").forGetter(BlockEntry::count),
                    Codec.INT.fieldOf("resetTimer").forGetter(BlockEntry::resetTimer)
                ).apply(inst, ::BlockEntry)
            }
        }
    }
}