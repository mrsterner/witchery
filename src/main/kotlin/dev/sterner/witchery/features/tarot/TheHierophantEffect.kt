package dev.sterner.witchery.features.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player

class TheHierophantEffect : TarotEffect(6) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_hierophant.reversed" else "tarot.witchery.the_hierophant"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_hierophant.reversed.description" else "tarot.witchery.the_hierophant.description"
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