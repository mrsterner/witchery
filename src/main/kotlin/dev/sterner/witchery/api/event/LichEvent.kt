package dev.sterner.witchery.api.event


import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.ICancellableEvent

class LichEvent {

    class LevelUp(var player: Player, var currentLevel: Int, var newLevel: Int) : Event(), ICancellableEvent

}
