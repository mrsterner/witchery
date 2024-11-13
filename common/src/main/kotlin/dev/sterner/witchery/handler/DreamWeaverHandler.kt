package dev.sterner.witchery.handler

import dev.sterner.witchery.block.dream_weaver.DreamWeaverBlockEntity
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import java.util.stream.Stream

object DreamWeaverHandler {

    fun onWake(player: Player, sleepCount: Int, wakeImmediately: Boolean) {
        if (sleepCount >= 100) {
            var corrupt = 0

            val dreamWeaverPositions: List<BlockPos> = BlockPos.MutableBlockPos.betweenClosedStream(player.boundingBox.inflate(10.0))
                .filter { player.level().getBlockEntity(it) is DreamWeaverBlockEntity }
                .toList()

            for (pos in dreamWeaverPositions) {
                val dreamWeaver = player.level().getBlockState(pos)
                if (dreamWeaver.`is`(WitcheryBlocks.DREAM_WEAVER_OF_NIGHTMARES.get())) {
                    corrupt += 1
                }
            }

            for (pos in dreamWeaverPositions) {
                val dreamWeaver = player.level().getBlockEntity(pos) as DreamWeaverBlockEntity
                dreamWeaver.applyWakeUpEffect(player, corrupt)
            }
        }
    }
}