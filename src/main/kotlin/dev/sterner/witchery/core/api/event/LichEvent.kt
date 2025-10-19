package dev.sterner.witchery.core.api.event


import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.ICancellableEvent

object LichEvent {

    class LevelUp(var player: Player, var currentLevel: Int, var newLevel: Int) : Event(), ICancellableEvent

}
