package dev.sterner.witchery.tarot

import dev.sterner.witchery.block.altar.AltarBlockEntity
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.player.Player

class TheHangedManEffect : TarotEffect(13) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Hanged Man (Reversed)" else "The Hanged Man"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "You cannot let go" else "Sacrifice brings reward"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (isReversed) {
            //handled in event
        } else {
            if (player.deltaMovement.y < 0 && !player.onGround()) {
                player.deltaMovement = player.deltaMovement.multiply(1.0, 0.7, 1.0)
            }
        }
    }

    override fun onPlayerHurt(player: Player, source: DamageSource, amount: Float, isReversed: Boolean): Float {
        if (!isReversed && amount > 5f && player.level() is ServerLevel) {
            val altar = AltarBlockEntity.getClosestAltar(
                player.level() as ServerLevel,
                player.blockPosition(),
                16
            )
            altar?.let {
                it.currentPower = (it.currentPower + (amount * 100).toInt()).coerceAtMost(it.maxPower)
            }
        }
        return amount
    }
}