package dev.sterner.witchery.features.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player

class TheHierophantEffect : TarotEffect(6) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Hierophant (Reversed)" else "The Hierophant"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Divine grace withheld - take damage each morning"
        else "Blessed each dawn with absorption, sleeping fully restores your health"
    )

    override fun onMorning(player: Player, isReversed: Boolean) {
        if (isReversed) {
            player.hurt(player.damageSources().magic(), 2f)
        } else {
            player.addEffect(MobEffectInstance(MobEffects.ABSORPTION, 12000, 1))
        }
    }

    override fun onSleep(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.health = player.maxHealth
        }
    }
}