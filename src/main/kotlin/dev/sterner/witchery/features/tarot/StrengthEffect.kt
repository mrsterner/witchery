package dev.sterner.witchery.features.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class StrengthEffect : TarotEffect(9) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "Strength (Reversed)" else "Strength"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Weakness overcomes you" else "Your strikes land true"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (isReversed) {
            if (!player.hasEffect(MobEffects.WEAKNESS)) {
                player.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 400, 0, true, false))
            }
        } else {
            if (!player.hasEffect(MobEffects.DAMAGE_BOOST)) {
                player.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 0, true, false))
            }
        }
    }

    override fun onEntityKill(player: Player, entity: LivingEntity, isReversed: Boolean) {
        if (!isReversed) {
            player.heal(2f)
        }
    }
}