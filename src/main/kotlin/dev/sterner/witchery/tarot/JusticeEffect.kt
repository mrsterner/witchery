package dev.sterner.witchery.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class JusticeEffect : TarotEffect(12) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "Justice (Reversed)" else "Justice"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Injustice prevails" else "Balance is maintained"
    )

    override fun onEntityHit(player: Player, target: Entity, isReversed: Boolean) {
        if (!isReversed && target is LivingEntity) {
            if (target.lastHurtMob == player) {
                target.hurt(player.damageSources().thorns(player), 2f)
            }
        }
    }

    override fun onPlayerHurt(player: Player, source: DamageSource, amount: Float, isReversed: Boolean): Float {
        if (isReversed) {
            return amount * 1.3f
        }
        return amount
    }
}