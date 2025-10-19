package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryApi
import net.minecraft.tags.BiomeTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfOverheating : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        val biome = level.getBiome(player.blockPosition())

        if (biome.`is`(BiomeTags.IS_SAVANNA) || biome.`is`(BiomeTags.IS_NETHER) || biome.`is`(BiomeTags.HAS_DESERT_PYRAMID)) {
            val fireTicks = if (WitcheryApi.isWitchy(player)) {
                20
            } else {
                5
            }
            player.remainingFireTicks = fireTicks
        }

        super.onTickCurse(level, player, catBoosted)
    }
}