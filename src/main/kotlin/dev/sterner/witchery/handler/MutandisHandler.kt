package dev.sterner.witchery.handler

import dev.sterner.witchery.data_attachment.MutandisLevelAttachment
import dev.sterner.witchery.payload.MutandisRemenantParticleS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor

object MutandisHandler {



    fun tick(level: Level) {
        if (level.isClientSide) return

        val serverLevel = level as ServerLevel

        val toRemove = mutableListOf<BlockPos>()

        val iterator = MutandisLevelAttachment.getMap(serverLevel).iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val (pos, mutandisData) = entry
            val (tag, time) = mutandisData
            if (time <= 1) {
                toRemove.add(pos)
            } else {
                PacketDistributor.sendToPlayersTrackingChunk(serverLevel, ChunkPos(pos), MutandisRemenantParticleS2CPayload(pos))
                MutandisLevelAttachment.updateTimeForTagBlockPos(serverLevel, pos)
            }
        }

        for (pos in toRemove) {
            MutandisLevelAttachment.removeTagForBlockPos(serverLevel, pos)
        }
    }
}