package dev.sterner.witchery.features.tarot

import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player

class TheStarEffect : TarotEffect(18) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_star.reversed" else "tarot.witchery.the_star"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_star.reversed.description" else "tarot.witchery.the_star.description"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            if (!player.level().isDay && player.level().canSeeSky(player.blockPosition())) {
                if (player.level().gameTime % 40 == 0L) {
                    player.heal(0.5f)
                }
            }
        } else {
            if (player.level().gameTime % 100 == 0L) {
                player.causeFoodExhaustion(0.5f)
            }
        }
    }

    override fun onNightfall(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.health = player.maxHealth

            val negativeEffects = player.activeEffects
                .filter { !it.effect.value().isBeneficial }
                .map { it.effect }

            negativeEffects.forEach { player.removeEffect(it) }

            if (player.level() is ServerLevel) {
                (player.level() as ServerLevel).sendParticles(
                    ParticleTypes.END_ROD,
                    player.x, player.y + 2, player.z,
                    30, 0.5, 0.8, 0.5, 0.05
                )

                (player.level() as ServerLevel).sendParticles(
                    ParticleTypes.GLOW,
                    player.x, player.y + 1, player.z,
                    20, 0.3, 0.5, 0.3, 0.1
                )
            }
        }
    }
}