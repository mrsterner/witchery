package dev.sterner.witchery.features.curse

import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.api.WitcheryApi
import dev.sterner.witchery.features.curse.CurseHandler
import dev.sterner.witchery.registry.WitcheryCurseRegistry
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfFragility : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        super.onTickCurse(level, player, catBoosted)

        if (level.isClientSide && level.random.nextFloat() < 0.03f) {
            level.addParticle(
                ParticleTypes.DAMAGE_INDICATOR,
                player.x + (level.random.nextFloat() - 0.5),
                player.y + level.random.nextFloat() * player.bbHeight,
                player.z + (level.random.nextFloat() - 0.5),
                0.0, 0.0, 0.0
            )
        }
    }

    companion object {
        fun modifyDamage(player: Player, damage: Float): Float {
            if (!CurseHandler.hasCurse(player, WitcheryCurseRegistry.FRAGILITY.get())) {
                return damage
            }

            val multiplier = if (WitcheryApi.isWitchy(player)) {
                1.5f
            } else {
                1.15f
            }

            return damage * multiplier
        }
    }
}