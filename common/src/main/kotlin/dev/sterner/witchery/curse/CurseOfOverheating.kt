package dev.sterner.witchery.curse

import dev.sterner.witchery.api.Curse
import net.minecraft.tags.BiomeTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfOverheating : Curse() {

    /*
 AP: 2000
 taglock
 exhale
demonikc blood
 blaze rod
 brew of grotesque

 11x11 infernal
  */

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {

        val biome = level.getBiome(player.blockPosition())

        if (biome.`is`(BiomeTags.IS_SAVANNA) || biome.`is`(BiomeTags.IS_NETHER) || biome.`is`(BiomeTags.HAS_DESERT_PYRAMID)) {
            player.remainingFireTicks = 20
        }


        super.onTickCurse(level, player, catBoosted)
    }
}