package dev.sterner.witchery.features.tarot

import dev.sterner.witchery.core.registry.WitcheryDamageSources
import dev.sterner.witchery.mixin.DamageSourcesInvoker
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player

class TheSunEffect : TarotEffect(20) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Sun (Reversed)" else "The Sun"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Scorching rays - daylight burns you beneath open sky"
        else "Full restoration each dawn, slow healing in daylight, strength and regeneration"
    )

    override fun onMorning(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.health = player.maxHealth
            player.foodData.foodLevel = 20
            player.foodData.setSaturation(20f)

            player.addEffect(MobEffectInstance(MobEffects.REGENERATION, 1200, 1))
            player.addEffect(MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 0))
        }
    }

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed && player.level().isDay) {
            if (player.level().gameTime % 60 == 0L) {
                player.heal(0.5f)
            }
        }
    }
}