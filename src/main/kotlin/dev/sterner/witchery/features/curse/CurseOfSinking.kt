package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryApi
import net.minecraft.core.Direction
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfSinking : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        if (!player.isInWaterOrRain) {
            super.onTickCurse(level, player, catBoosted)
            return
        }

        val isWitch = WitcheryApi.isWitchy(player)

        var sinkStrength = if (isWitch) 0.06 else 0.03

        if (level.isRainingAt(player.blockPosition())) {
            sinkStrength *= 1.3
        }

        val motion = player.deltaMovement

        val newY = (motion.y - sinkStrength).coerceAtLeast(-0.5)

        val resistedY = if (motion.y > 0) {
            motion.y * if (isWitch) 0.5 else 0.75
        } else newY

        player.deltaMovement = motion.multiply(0.9, 1.0, 0.9)
            .with(Direction.Axis.Y, resistedY)

        super.onTickCurse(level, player, catBoosted)
    }
}
