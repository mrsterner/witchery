package dev.sterner.witchery.features.brewing.brew

import dev.sterner.witchery.data_attachment.UnderWaterBreathPlayerAttachment
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player

class BrewOfDepthItem(color: Int, properties: Properties) : BrewItem(color, properties) {


    override fun applyEffectOnSelf(player: Player, hasFrog: Boolean) {
        if (!player.level().isClientSide) {
            val duration = if (hasFrog) 8400 else 6000

            val newData = UnderWaterBreathPlayerAttachment.Data(
                duration = duration,
                maxDuration = duration
            )

            UnderWaterBreathPlayerAttachment.setData(player, newData)

            player.level().playSound(
                null,
                player.x, player.y, player.z,
                SoundEvents.BUBBLE_COLUMN_UPWARDS_AMBIENT,
                SoundSource.PLAYERS,
                1.0f,
                0.8f + player.level().random.nextFloat() * 0.4f
            )

            (0 until 15).forEach { i ->
                val d0 = player.x + (player.level().random.nextDouble() - 0.5) * 1.0
                val d1 = player.y + player.level().random.nextDouble() * player.bbHeight
                val d2 = player.z + (player.level().random.nextDouble() - 0.5) * 1.0

                (player.level() as ServerLevel).sendParticles(
                    ParticleTypes.BUBBLE,
                    d0, d1, d2,
                    5, // count
                    0.2, 0.2, 0.2,
                    0.1 // speed
                )
            }

            player.addEffect(MobEffectInstance(MobEffects.CONDUIT_POWER, duration, 0, false, true, true))
        }
    }
}