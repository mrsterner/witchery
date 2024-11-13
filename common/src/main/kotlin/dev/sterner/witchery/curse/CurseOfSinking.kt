package dev.sterner.witchery.curse

import dev.sterner.witchery.api.Curse
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfSinking : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        if (player.isInWater) {
            if (player.deltaMovement.y > -0.1) {
                player.deltaMovement = player.deltaMovement.add(0.0, -0.03, 0.0)
            }

            if (player.deltaMovement.y > 0) {
                player.deltaMovement = player.deltaMovement.multiply(1.0, 0.92, 1.0)
            }
        }

        super.onTickCurse(level, player, curseData.catBoosted)
    }
}