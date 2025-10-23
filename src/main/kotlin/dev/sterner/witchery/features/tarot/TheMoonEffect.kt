package dev.sterner.witchery.features.tarot

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player

class TheMoonEffect : TarotEffect(19) {

    override fun getDisplayName(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_moon.reversed" else "tarot.witchery.the_moon"
    )

    override fun getDescription(isReversed: Boolean) = Component.translatable(
        if (isReversed) "tarot.witchery.the_moon.reversed.description" else "tarot.witchery.the_moon.description"
    )

    override fun onTick(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.addEffect(MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 11, 0, true, false))

            if (!player.level().isDay && player.level().gameTime % 80 == 0L) {
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
            if (player.level().isDay && player.level().gameTime % 100 == 0L) {
                if (player.level().random.nextFloat() < 0.05f) {
                    player.addEffect(MobEffectInstance(MobEffects.BLINDNESS, 100, 0, false, true))

                    player.displayClientMessage(
                        Component.literal("The harsh light overwhelms your senses!")
                            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC),
                        true
                    )
                }
            }
        }
    }

    override fun onNightfall(player: Player, isReversed: Boolean) {
        if (!isReversed) {
            player.addEffect(MobEffectInstance(MobEffects.MOVEMENT_SPEED, 12000, 1))
        }
    }
}