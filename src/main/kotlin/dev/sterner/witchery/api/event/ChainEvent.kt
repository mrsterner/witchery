package dev.sterner.witchery.api.event

import dev.sterner.witchery.entity.ChainEntity
import net.minecraft.world.entity.Entity
import net.neoforged.bus.api.Event

object ChainEvent {
    class Discard(var entity: Entity?, var chain: ChainEntity) : Event()
}