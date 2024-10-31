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

    private const val MAX_DISTANCE = 24

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

    fun breakBlock(level: Level?, blockPos: BlockPos?, blockState: BlockState, serverPlayer: ServerPlayer?, intValue: IntValue?): EventResult? {
        if (level is ServerLevel && blockPos != null) {
            val isRowan = blockState.`is`(WitcheryBlocks.ROWAN_LOG.get())
            val isHawthorn = blockState.`is`(WitcheryBlocks.HAWTHORN_LOG.get())
            val isAlder = blockState.`is`(WitcheryBlocks.ALDER_LOG.get())

            if (isRowan || isHawthorn || isAlder) {
                val hasLeafAbove = (1..8).any { offset ->
                    level.getBlockState(blockPos.above(offset)).`is`(BlockTags.LEAVES)
                }

                if (hasLeafAbove) {
                    val data = getData(level)
                    val nearbyEntry = data.entries.find { it.blockPos.closerThan(blockPos, MAX_DISTANCE.toDouble()) }

                    val updatedEntries = if (nearbyEntry != null) {
                        data.entries.map { entry ->
                            if (entry == nearbyEntry) {
                                entry.copy(
                                    count = entry.count + 1,
                                    resetTimer = 20 * 60 * 5,
                                    state = blockState
                                )
                            } else {
                                entry
                            }
                        }
                    } else {
                        data.entries + BlockEntry(blockPos, blockState, 1, 20 * 60 * 5)
                    }

                    setData(level, data.copy(entries = updatedEntries))
                }
            }
        }

        return EventResult.pass()
    }

    fun serverTick(minecraftServer: MinecraftServer) {
        minecraftServer.allLevels.forEach { level ->
            if (level is ServerLevel) {
                val data = getData(level)

                if (data.entries.isNotEmpty()) {
                    val updatedEntries = data.entries.mapNotNull { entry ->
                        val newTimer = entry.resetTimer - 1
                        if (entry.count >= 24) {
                            performSpawn(level, entry.blockPos, entry.state)
                            entry.copy(count = 0, resetTimer = 20 * 60 * 5)
                        } else if (newTimer > 0) {
                            entry.copy(resetTimer = newTimer)
                        } else {
                            null
                        }
                    }

                    if (updatedEntries != data.entries) {
                        setData(level, data.copy(entries = updatedEntries))
                    }
                }
            }
        }
    }

    private fun performSpawn(level: ServerLevel, blockPos: BlockPos, state: BlockState) {
        val ent = WitcheryEntityTypes.ENT.get().create(level)
        val variant = if (state.`is`(WitcheryBlocks.HAWTHORN_LOG.get())) {
            EntEntity.Type.HAWTHORN
        } else if(state.`is`(WitcheryBlocks.ALDER_LOG.get())){
            EntEntity.Type.ALDER
        } else {
            EntEntity.Type.ROWAN
        }
        ent?.setVariant(variant)
        ent?.moveTo(blockPos.x + 0.5, blockPos.y+ 0.5, blockPos.z+ 0.5)
        ent?.let { level.addFreshEntity(it) }
    }

    data class Data(val entries: List<BlockEntry> = listOf()) {
        companion object {
            val ID: ResourceLocation = Witchery.id("ent_spawn")
            val DATA_CODEC: Codec<Data> = BlockEntry.CODEC.listOf().fieldOf("entries").xmap(::Data, Data::entries).codec()
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