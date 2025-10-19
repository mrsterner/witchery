package dev.sterner.witchery.core.api.event

import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.Event

object SleepingEvent {
    class Stop(
        val player: Player,
        val sleepCounter: Int,
        val wakeImmediately: Boolean
    ) : Event()
}