package dev.sterner.witchery.handler

import dev.sterner.witchery.data_attachment.EntSpawnLevelAttachment
import dev.sterner.witchery.entity.EntEntity
import dev.sterner.witchery.registry.WitcheryBlocks
import dev.sterner.witchery.registry.WitcheryEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

object EntSpawningHandler {

    private const val MAX_DISTANCE = 24



    /**
     * Handles breaking of special tree logs (Rowan, Hawthorn, Alder).
     * Tracks the number of nearby logs broken and prepares for Ent spawning
     * if conditions are met.
     */
    fun breakBlock(
        level: Level?,
        blockPos: BlockPos?,
        blockState: BlockState,
        player: Player?
    ) {
        if (level is ServerLevel && blockPos != null) {
            val isRowan = blockState.`is`(WitcheryBlocks.ROWAN_LOG.get())
            val isHawthorn = blockState.`is`(WitcheryBlocks.HAWTHORN_LOG.get())
            val isAlder = blockState.`is`(WitcheryBlocks.ALDER_LOG.get())

            if (isRowan || isHawthorn || isAlder) {
                val hasLeafAbove = (1..8).any { offset ->
                    level.getBlockState(blockPos.above(offset)).`is`(BlockTags.LEAVES)
                }

                if (hasLeafAbove) {
                    val data = EntSpawnLevelAttachment.getData(level)
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
                        data.entries + EntSpawnLevelAttachment.BlockEntry(blockPos, blockState, 1, 20 * 60 * 5)
                    }

                    EntSpawnLevelAttachment.setData(level, data.copy(entries = updatedEntries))
                }
            }
        }
    }

    /**
     * Ticks the Ent spawning system each server tick.
     * Decreases timers, resets counts, and spawns Ents when enough logs
     * have been broken nearby.
     */
    fun serverTick(minecraftServer: MinecraftServer) {
        minecraftServer.allLevels.forEach { level ->
            if (level is ServerLevel) {
                val data = EntSpawnLevelAttachment.getData(level)

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
                        EntSpawnLevelAttachment.setData(level, data.copy(entries = updatedEntries))
                    }
                }
            }
        }
    }

    /**
     * Spawns an Ent entity at a given block position,
     * selecting its variant (Rowan, Hawthorn, Alder) based on the block type.
     */
    private fun performSpawn(level: ServerLevel, blockPos: BlockPos, state: BlockState) {
        val ent = WitcheryEntityTypes.ENT.get().create(level)
        val variant = if (state.`is`(WitcheryBlocks.HAWTHORN_LOG.get())) {
            EntEntity.Type.HAWTHORN
        } else if (state.`is`(WitcheryBlocks.ALDER_LOG.get())) {
            EntEntity.Type.ALDER
        } else {
            EntEntity.Type.ROWAN
        }
        ent?.setVariant(variant)
        ent?.moveTo(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5)
        ent?.let { level.addFreshEntity(it) }
    }
}