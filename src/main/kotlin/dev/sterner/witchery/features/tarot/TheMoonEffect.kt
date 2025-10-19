package dev.sterner.witchery.features.tarot

import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player

class TheMoonEffect : TarotEffect(19) {

    override fun getDisplayName(isReversed: Boolean) = Component.literal(
        if (isReversed) "The Moon (Reversed)" else "The Moon"
    )

    override fun getDescription(isReversed: Boolean) = Component.literal(
        if (isReversed) "Clarity at a cost" else "Embrace the shadows"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.addEffect(MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 11, 0, true, false))

            if (!player.level().isDay && player.level().gameTime % 20 == 0L) {
                val nearbyMobs = player.level().getEntitiesOfClass(
                    Mob::class.java,
                    player.boundingBox.inflate(16.0)
                )

                for (mob in nearbyMobs) {
                    if (mob.target == player && player.level().random.nextFloat() < 0.3f) {
                        mob.target = null
                    }
                }
            }
        } else {
            if (!player.hasEffect(MobEffects.CONFUSION)) {
                player.addEffect(MobEffectInstance(MobEffects.CONFUSION, 400, 0, true, false))
            }
        }
    }

    override fun onNightfall(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 12000, 1))
        }
    }
}