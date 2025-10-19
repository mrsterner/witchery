package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryApi
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfSinking : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        if (player.isInWater) {
            val sinkMultiplier = if (WitcheryApi.isWitchy(player)) {
                1.0
            } else {
                0.3
            }

            if (player.deltaMovement.y > -0.1) {
                player.deltaMovement = player.deltaMovement.add(0.0, -0.03 * sinkMultiplier, 0.0)
            }

            if (player.deltaMovement.y > 0) {
                val slowdownFactor = 0.92 + (0.08 * (1.0 - sinkMultiplier))
                player.deltaMovement = player.deltaMovement.multiply(1.0, slowdownFactor, 1.0)
            }
        }

        super.onTickCurse(level, player, catBoosted)
    }
}