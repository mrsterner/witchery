package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.event.EventResult
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.architectury.utils.value.IntValue
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.EntEntity
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

object EntSpawnLevelAttachment {


    @ExpectPlatform
    @JvmStatic
    fun getData(level: ServerLevel): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        throw AssertionError()
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