package dev.sterner.witchery.features.curse

import dev.sterner.witchery.core.api.Curse
import dev.sterner.witchery.core.api.WitcheryApi
import net.minecraft.tags.BiomeTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.Tags

class CurseOfOverheating : Curse() {

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        if (player.level().isClientSide) return

        val biome = level.getBiome(player.blockPosition())

        val canSeeSun = player.level().canSeeSky(player.blockPosition()) && player.level().isDay
        val ultraWarm = player.level().dimensionType().ultraWarm()
        val isHot = biome.`is`(Tags.Biomes.IS_HOT)
        val randomChance = level.random.nextFloat() < 0.25f

        if (randomChance) {
            if ((canSeeSun && isHot) || ultraWarm) {
                val fireTicks = if (WitcheryApi.isWitchy(player)) {
                    40
                } else {
                    20
                }

                player.remainingFireTicks = fireTicks

            }
        }

        super.onTickCurse(level, player, catBoosted)
    }
}