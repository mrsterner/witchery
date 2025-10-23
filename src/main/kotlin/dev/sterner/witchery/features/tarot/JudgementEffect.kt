package dev.sterner.witchery.features.tarot

import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class JudgementEffect : TarotEffect(21) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "Judgement (Reversed)" else "Judgement"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Each kill weighs on your soul, damaging you in turn"
        else "A second chance when death looms - rise reborn from mortal wounds, heal from victory"
    )

    override fun onPlayerHurt(player: Player, source: DamageSource, amount: Float, isReversed: Boolean): Float {
        if (!isReversed && amount >= player.health && player.level() is ServerLevel) {
            val level = player.level() as ServerLevel

            if (level.random.nextFloat() < 0.5f) {
                player.health = 1f
                player.addEffect(MobEffectInstance(MobEffects.REGENERATION, 200, 2))
                player.addEffect(MobEffectInstance(MobEffects.FIRE_RESISTANCE, 200, 0))
                player.addEffect(MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1))

                level.sendParticles(
                    ParticleTypes.TOTEM_OF_UNDYING,
                    player.x, player.y + 1, player.z,
                    50, 0.5, 0.5, 0.5, 0.5
                )

                player.level().playSound(
                    null, player.blockPosition(),
                    SoundEvents.TOTEM_USE, SoundSource.PLAYERS,
                    1.0f, 1.0f
                )

                player.displayClientMessage(
                    Component.literal("Judgement spares you!")
                        .withStyle(ChatFormatting.LIGHT_PURPLE, ChatFormatting.BOLD),
                    false
                )

                removeCardFromReading(player, this.cardNumber)

                return 0f
            }
        }
        return amount
    }

    override fun onEntityKill(player: Player, entity: LivingEntity, isReversed: Boolean) {
        if (isReversed) {
            player.hurt(player.damageSources().magic(), 2f)
        } else {
            player.heal(1f)
        }
    }
}