package dev.sterner.witchery.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player

class TheDevilEffect : TarotEffect(16) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Devil (Reversed)" else "The Devil"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Breaking free from chains" else "Dark power at a cost"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            if (!player.hasEffect(MobEffects.DAMAGE_BOOST)) {
                player.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 1, true, false))
            }
            if (player.level().gameTime % 200 == 0L) {
                player.hurt(player.damageSources().magic(), 1f)
            }
        } else {
            if (player.level().gameTime % 600 == 0L) {
                val debuffs = player.activeEffects
                    .filter { !it.effect.value().isBeneficial }
                    .map { it.effect }
                debuffs.forEach { player.removeEffect(it) }
                player.giveExperiencePoints(-5)
            }
        }
    }
}