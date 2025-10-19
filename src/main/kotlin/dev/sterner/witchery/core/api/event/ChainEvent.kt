package dev.sterner.witchery.core.api.event

import dev.sterner.witchery.content.entity.ChainEntity
import net.minecraft.world.entity.Entity
import net.neoforged.bus.api.Event

object ChainEvent {
    class Discard(var entity: Entity?, var chain: ChainEntity) : Event()
}