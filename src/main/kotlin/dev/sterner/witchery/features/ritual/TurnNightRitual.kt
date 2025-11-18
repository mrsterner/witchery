package dev.sterner.witchery.features.ritual

import dev.sterner.witchery.content.block.ritual.GoldenChalkBlockEntity
import dev.sterner.witchery.core.api.Ritual
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level

class TurnNightRitual : Ritual("set_midnight") {

    override fun onEndRitual(
        level: Level,
        blockPos: BlockPos,
        goldenChalkBlockEntity: GoldenChalkBlockEntity
    ) {
        super.onEndRitual(level, blockPos, goldenChalkBlockEntity)

        if (level is ServerLevel) {
            val currentGameTime = level.dayTime
            val currentDay = currentGameTime / 24000L

            val nextDay = currentDay + 1
            val midnight = 18000L

            val newTime = nextDay * 24000L + midnight

            for (serverLevel in level.server.allLevels) {
                serverLevel.dayTime = newTime
            }
        }
    }
}
