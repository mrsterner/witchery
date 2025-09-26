package dev.sterner.witchery.api.event

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.ICancellableEvent

object CurseEvent {
    class Added(
        var player: Player,
        var sourcePlayer: ServerPlayer?,
        var curse: ResourceLocation,
        var catBoosted: Boolean
    ) : Event(),
        ICancellableEvent
}
