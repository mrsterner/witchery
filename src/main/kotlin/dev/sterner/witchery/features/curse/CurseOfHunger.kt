package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.core.registry.WitcheryCurseRegistry
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfHunger : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        super.onTickCurse(level, player, catBoosted)

        val curseData = CursePlayerAttachment.getData(player).playerCurseList
            .find { it.curseId == WitcheryCurseRegistry.CURSES_REGISTRY.getKey(this) }

        val witchPower = curseData?.witchPower ?: 0


        val effectivenessMultiplier = getEffectivenessMultiplier(player)

        val witchPowerAmplifier = 1.0f + (witchPower * 0.01f).coerceAtMost(0.1f)
        val totalMultiplier = effectivenessMultiplier * witchPowerAmplifier

        val exhaustionRate = if (WitcheryApi.isWitchy(player)) {
            0.005f * totalMultiplier
        } else {
            0.001f * totalMultiplier
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