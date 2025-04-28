package dev.sterner.witchery.handler

import dev.sterner.witchery.payload.MutandisRemenantParticleS2CPacket
import dev.sterner.witchery.platform.MutandisLevelAttachment.getMap
import dev.sterner.witchery.platform.MutandisLevelAttachment.removeTagForBlockPos
import dev.sterner.witchery.platform.MutandisLevelAttachment.updateTimeForTagBlockPos
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel

object MutandisHandler {

    fun tick(serverLevel: ServerLevel?) {
        if (serverLevel == null) return

        val toRemove = mutableListOf<BlockPos>()

        val iterator = getMap(serverLevel).iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val (pos, mutandisData) = entry
            val (tag, time) = mutandisData
            if (time <= 1) {
                toRemove.add(pos)
            } else {
                WitcheryPayloads.sendToPlayers(serverLevel, pos, MutandisRemenantParticleS2CPacket(pos))
                updateTimeForTagBlockPos(serverLevel, pos)
            }
        }

        for (pos in toRemove) {
            removeTagForBlockPos(serverLevel, pos)
        }
    }
}