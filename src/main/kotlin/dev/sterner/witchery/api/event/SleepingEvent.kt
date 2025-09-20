package dev.sterner.witchery.api.event

import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.Event

class SleepingEvent(
    val player: Player,
    val sleepCounter: Int,
    val wakeImmediately: Boolean
) : Event()