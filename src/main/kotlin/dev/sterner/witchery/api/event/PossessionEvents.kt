package dev.sterner.witchery.api.event

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.ICancellableEvent

object PossessionEvents {

    class PossessionAttempted(host: Mob, player: Player, simulate: Boolean): Event(), ICancellableEvent

    class PossessionStateChange(player: Player, host: Mob?): Event()

    class ShouldTransferInventory(player: Player, possessed: LivingEntity): Event(), ICancellableEvent

    class CleanUpAfterDissociation(player: Player, possessed: LivingEntity): Event()

    class AllowRender(possessed: Entity): Event(), ICancellableEvent
}