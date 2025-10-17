package dev.sterner.witchery.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player

class TheHermitEffect : TarotEffect(10) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Hermit (Reversed)" else "The Hermit"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Isolation brings madness" else "Wisdom in solitude"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        val nearbyPlayers = player.level().players().count {
            it != player && it.distanceTo(player) < 32.0
        }

        if (isReversed) {
            if (nearbyPlayers == 0 && player.level().gameTime % 200 == 0L) {
                player.hurt(player.damageSources().magic(), 0f)
            }
        } else {
            if (nearbyPlayers == 0 && player.level().gameTime % 100 == 0L) {
                player.giveExperiencePoints(1)
            }
        }
    }
}