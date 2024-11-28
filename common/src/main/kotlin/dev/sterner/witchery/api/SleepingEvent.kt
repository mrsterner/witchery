package dev.sterner.witchery.api

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import net.minecraft.world.entity.player.Player

interface SleepingEvent {

    companion object {
        /**
         * Fired when the Player is waking up from sleeping in a bed
         */
        val POST: Event<Post> = EventFactory.createLoop()
    }
}

typealias Post = (Player, Int, Boolean) -> Unit
