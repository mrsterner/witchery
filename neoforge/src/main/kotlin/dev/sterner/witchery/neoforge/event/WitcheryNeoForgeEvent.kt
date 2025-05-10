package dev.sterner.witchery.neoforge.event

import dev.sterner.witchery.block.coffin.CoffinBlock
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.LevelAccessor
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.event.entity.player.CanContinueSleepingEvent
import net.neoforged.neoforge.event.entity.player.CanPlayerSleepEvent
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent


object WitcheryNeoForgeEvent {

    @SubscribeEvent
    fun canPlayerSleepEvent(event: CanPlayerSleepEvent) {
        if (event.state.block is CoffinBlock) {
            when (event.problem) {
                Player.BedSleepingProblem.OTHER_PROBLEM,
                Player.BedSleepingProblem.NOT_POSSIBLE_HERE,
                Player.BedSleepingProblem.TOO_FAR_AWAY -> {
                    return
                }

                else -> {
                    event.entity.setRespawnPosition(event.level.dimension(), event.pos, event.entity.yRot, false, true)
                    event.problem = if (!isDay(event.level)) {
                        Player.BedSleepingProblem.NOT_POSSIBLE_NOW
                    } else {
                        null
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun canContinueSleepingEvent(event: CanContinueSleepingEvent) {
        val blockState = event.entity.sleepingPos
            .map { event.entity.level().getBlockState(it) }
            .orElse(null)

        if (blockState?.block is CoffinBlock) {
            val isDay = isDay(event.entity.level())
            event.setContinueSleeping(isDay && event.problem == Player.BedSleepingProblem.NOT_POSSIBLE_NOW)
            if (!isDay) {
                print("daytime: ${event.entity.level().dayTime}")
                event.setContinueSleeping(false)
            }
        }
    }

    fun isDay(level: LevelAccessor): Boolean {
        val angle = level.getTimeOfDay(1.0f)
        return angle > 0.78 || angle < 0.24
    }

    @SubscribeEvent
    fun sleepFinishedTimeEvent(event: SleepFinishedTimeEvent) {
        val level = event.level
        if (level is ServerLevel && isDay(level)) {
            val sleepingInCoffin = level.players().any { player ->
                player.sleepingPos
                    .map { level.getBlockState(it).block is CoffinBlock }
                    .orElse(false)
            }
            print(sleepingInCoffin)
            if (sleepingInCoffin) {
                val dayTime = level.dayTime % 24000L
                val timeAdjustment = if (dayTime > 12000L) 13000 else -11000
                event.setTimeAddition(event.newTime + timeAdjustment)
            }
        }
    }
}
