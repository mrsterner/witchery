package dev.sterner.witchery.handler

import dev.sterner.witchery.api.event.SleepingEvent
import dev.sterner.witchery.block.dream_weaver.DreamWeaverBlockEntity
import dev.sterner.witchery.registry.WitcheryBlocks
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.Blocks

object DreamWeaverHandler {

    fun registerEvents() {
        SleepingEvent.POST.register(DreamWeaverHandler::onWake)
    }
    /**
     * Applies dream weaver effects on a player
     *
     * @param player
     * @param sleepCount
     * @param wakeImmediately
     */
    fun onWake(player: Player, sleepCount: Int, wakeImmediately: Boolean) {
        if (sleepCount >= 100) {
            var corrupt = 0

            val dreamWeaverPositions: List<BlockPos> =
                BlockPos.MutableBlockPos.betweenClosedStream(player.boundingBox.inflate(10.0))
                    .filter { player.level().getBlockEntity(it) is DreamWeaverBlockEntity }
                    .toList()

            for (pos in dreamWeaverPositions) {
                val dreamWeaver = player.level().getBlockState(pos)
                if (dreamWeaver.`is`(WitcheryBlocks.DREAM_WEAVER_OF_NIGHTMARES.get())) {
                    corrupt += 1
                }
            }

            for (pos in dreamWeaverPositions) {
                val blockEntity = player.level().getBlockEntity(pos)
                if (blockEntity is DreamWeaverBlockEntity) {
                    blockEntity.applyWakeUpEffect(player, corrupt)
                }
            }
        }
    }
}