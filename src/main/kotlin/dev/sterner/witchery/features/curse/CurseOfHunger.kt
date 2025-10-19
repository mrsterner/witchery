package dev.sterner.witchery.features.curse

import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.api.WitcheryApi
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfHunger : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        super.onTickCurse(level, player, catBoosted)

        val effectivenessMultiplier = getEffectivenessMultiplier(player)

        val exhaustionRate = if (WitcheryApi.isWitchy(player)) {
            0.005f * effectivenessMultiplier
        } else {
            0.001f * effectivenessMultiplier
        }

        if (level.gameTime % 20 == 0L) {
            player.causeFoodExhaustion(exhaustionRate)
        }

        if (level.isClientSide && level.random.nextFloat() < 0.02f) {
            level.addParticle(
                ParticleTypes.SMOKE,
                player.x,
                player.y + 0.5,
                player.z,
                0.0, 0.0, 0.0
            )
        }
    }
}