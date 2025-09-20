package dev.sterner.witchery.api.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import dev.architectury.event.EventResult
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

interface CurseEvent {
    companion object {

        val ON_CURSE: Event<OnCurse> = EventFactory.createEventResult()
    }
}

typealias OnCurse = (Player, ServerPlayer?, ResourceLocation, Boolean) -> EventResult