package dev.sterner.witchery.features.tarot

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player

class TheFoolEffect : TarotEffect(1) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Fool (Reversed)" else "The Fool"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Clumsy mishaps and increased damage plague your journey"
        else "Naive luck protects you - reduced damage taken, random beneficial effects"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (isReversed) {
            if (player.level().random.nextFloat() < 0.0001f && player.onGround()) {
                player.hurt(player.damageSources().fall(), 1f)
                player.displayClientMessage(
                    Component.literal("Turning your ankle at a small stone, fool!")
                        .withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                    false
                )
            }
        } else {
            if (player.level().random.nextFloat() < 0.0005f) {
                val effects = listOf(
                    MobEffects.MOVEMENT_SPEED,
                    MobEffects.JUMP,
                    MobEffects.LUCK
                )
                player.addEffect(MobEffectInstance(effects.random(), 200, 0))
            }
        }
    }

    override fun onPlayerHurt(player: Player, source: DamageSource, amount: Float, isReversed: Boolean): Float {
        return if (isReversed) amount * 1.2f else amount * 0.9f
    }
}