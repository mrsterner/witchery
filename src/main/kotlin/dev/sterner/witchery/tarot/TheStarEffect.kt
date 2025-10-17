package dev.sterner.witchery.tarot

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player

class TheStarEffect : TarotEffect(18) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Star (Reversed)" else "The Star"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Hope fades away" else "Guiding light in darkness"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            if (!player.hasEffect(MobEffects.REGENERATION)) {
                player.addEffect(MobEffectInstance(MobEffects.REGENERATION, 400, 0, true, false))
            }
        } else {
            if (player.level().gameTime % 300 == 0L && player.health > 1f) {
                player.hurt(player.damageSources().starve(), 1f)
            }
        }
    }

    override fun onNightfall(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.heal(4f)

            if (player.level() is ServerLevel) {
                (player.level() as ServerLevel).sendParticles(
                    ParticleTypes.END_ROD,
                    player.x, player.y + 2, player.z,
                    20, 0.5, 0.5, 0.5, 0.05
                )
            }
        }
    }
}
