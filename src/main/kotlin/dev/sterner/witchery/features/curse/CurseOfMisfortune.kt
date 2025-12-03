package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryApi
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfMisfortune : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        if(level.isClientSide) return

        if (level.gameTime % 80 == 0L) {
            val effectChance = if (WitcheryApi.isWitchy(player)) {
                0.01
            } else {
                0.003
            }

            val effectDuration = if (WitcheryApi.isWitchy(player)) {
                20 * 8
            } else {
                20 * 5
            }

            if (level.random.nextDouble() < effectChance) {
                player.addEffect(MobEffectInstance(MobEffects.DIG_SLOWDOWN, effectDuration, 0))
            }
            if (level.random.nextDouble() < effectChance) {
                player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, effectDuration, 0))
            }
            if (level.random.nextDouble() < effectChance) {
                player.addEffect(MobEffectInstance(MobEffects.BLINDNESS, effectDuration, 0))
            }
            if (level.random.nextDouble() < effectChance) {
                player.addEffect(MobEffectInstance(MobEffects.WEAKNESS, effectDuration, 0))
            }
            if (level.random.nextDouble() < effectChance) {
                player.addEffect(MobEffectInstance(MobEffects.GLOWING, effectDuration, 0))
            }
            if (level.random.nextDouble() < effectChance) {
                player.addEffect(MobEffectInstance(MobEffects.DARKNESS, effectDuration, 0))
            }
        }

        super.onTickCurse(level, player, catBoosted)
    }
}