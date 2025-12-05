package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryApi
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfBefuddlement : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        super.onTickCurse(level, player, catBoosted)

        if(level.isClientSide) return

        val effectivenessMultiplier = getEffectivenessMultiplier(player)

        val baseInterval = if (WitcheryApi.isWitchy(player)) {
            240L
        } else {
            420L
        }

        if (level.gameTime % baseInterval == 0L) {
            val nauseaChance = 0.18f * effectivenessMultiplier
            val confusionDuration = (20 * 14 * effectivenessMultiplier).toInt()

            if (level.random.nextFloat() < nauseaChance) {
                player.addEffect(MobEffectInstance(MobEffects.CONFUSION, confusionDuration, 0))
            }

            val miningFatigueChance = nauseaChance * 0.35f
            val miningFatigueDuration = (20 * (14 + 8) * effectivenessMultiplier).toInt()

            if (level.random.nextFloat() < miningFatigueChance) {
                player.addEffect(MobEffectInstance(MobEffects.DIG_SLOWDOWN, miningFatigueDuration, 0))
            }
        }

        if (level.isClientSide && level.random.nextFloat() < 0.05f * effectivenessMultiplier) {
            level.addParticle(
                ParticleTypes.PORTAL,
                player.x + (level.random.nextFloat() - 0.5) * 0.5,
                player.y + level.random.nextFloat() * player.bbHeight,
                player.z + (level.random.nextFloat() - 0.5) * 0.5,
                (level.random.nextFloat() - 0.5) * 0.1,
                0.0,
                (level.random.nextFloat() - 0.5) * 0.1
            )
        }
    }

    override fun onAdded(level: Level, player: Player, catBoosted: Boolean) {
        super.onAdded(level, player, catBoosted)

        player.displayClientMessage(
            Component.translatable("curse.witchery.befuddlement.applied")
                .withStyle(ChatFormatting.DARK_PURPLE),
            true
        )
    }
}