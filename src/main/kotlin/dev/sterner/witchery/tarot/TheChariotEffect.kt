package dev.sterner.witchery.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player

class TheChariotEffect : TarotEffect(8) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Chariot (Reversed)" else "The Chariot"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Your path is blocked" else "Swift as the wind"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (isReversed) {
            if (!player.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)) {
                player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 0, true, false))
            }
        } else {
            if (!player.hasEffect(MobEffects.MOVEMENT_SPEED)) {
                player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 400, 1, true, false))
            }
        }
    }
}