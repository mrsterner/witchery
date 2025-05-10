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
            if (event.level.isDay) {
                event.entity.setRespawnPosition(event.level.dimension(), event.pos, event.entity.yRot, false, true)
                event.problem = null
            } else {
                event.problem = Player.BedSleepingProblem.NOT_POSSIBLE_NOW
            }
        }
    }

    @SubscribeEvent
    fun canContinueSleepingEvent(event: CanContinueSleepingEvent) {
        val blockState = event.entity.sleepingPos
            .map { event.entity.level().getBlockState(it) }
            .orElse(null)

        if (blockState?.block is CoffinBlock) {
            if (event.entity.level().isDay) {
                event.setContinueSleeping(true)
            } else {
                event.setContinueSleeping(false)
            }
        }
    }

    @SubscribeEvent
    fun sleepFinishedTimeEvent(event: SleepFinishedTimeEvent) {
        val level = event.level
        if (level is ServerLevel) {
            val sleepingInCoffin = level.players().any { player ->
                player.sleepingPos
                    .map { level.getBlockState(it).block is CoffinBlock }
                    .orElse(false)
            }
        
            if (sleepingInCoffin && level.isDay) {
                val fullDays = level.dayTime / 24000L

                val newTime = (fullDays * 24000L) + 13000L

                event.setTimeAddition(newTime)
            }
        }
    }
}