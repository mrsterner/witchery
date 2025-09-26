package dev.sterner.witchery.api.event


import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.ICancellableEvent

object VampireEvent {

    class LevelUp(var player: Player, var currentLevel: Int, var newLevel: Int) : Event(), ICancellableEvent
    class SunDamage(var player: Player) : Event(), ICancellableEvent
    class BloodDrink(var player: Player, var target: LivingEntity) : Event(), ICancellableEvent

}
