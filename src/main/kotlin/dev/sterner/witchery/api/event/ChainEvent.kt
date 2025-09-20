package dev.sterner.witchery.api.event

import dev.architectury.event.Event
import dev.architectury.event.EventFactory
import dev.architectury.event.EventResult
import dev.sterner.witchery.entity.ChainEntity
import net.minecraft.world.entity.Entity

interface ChainEvent {
    companion object {

        val ON_DISCARD: Event<OnDiscard> = EventFactory.createEventResult()
    }
}

typealias OnDiscard = (Entity?, ChainEntity) -> EventResult