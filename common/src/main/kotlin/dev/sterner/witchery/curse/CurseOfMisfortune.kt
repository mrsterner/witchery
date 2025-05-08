package dev.sterner.witchery.curse

import dev.sterner.witchery.api.Curse
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfMisfortune : Curse() {



    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {

        if (level.gameTime % 20 == 0L) {
            if (level.random.nextDouble() < 0.01) {
                player.addEffect(MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20 * 2, 0))
            }
            if (level.random.nextDouble() < 0.01) {
                player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 2, 0))
            }
            if (level.random.nextDouble() < 0.01) {
                player.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 20 * 2, 0))
            }
            if (level.random.nextDouble() < 0.01) {
                player.addEffect(MobEffectInstance(MobEffects.WEAKNESS, 20 * 2, 0))
            }
            if (level.random.nextDouble() < 0.01) {
                player.addEffect(MobEffectInstance(MobEffects.GLOWING, 20 * 2, 0))
            }
            if (level.random.nextDouble() < 0.01) {
                player.addEffect(MobEffectInstance(MobEffects.DARKNESS, 20 * 2, 0))
            }
        }

        super.onTickCurse(level, player, catBoosted)
    }
}