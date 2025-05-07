package dev.sterner.witchery.curse

import dev.sterner.witchery.api.Curse
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfCorruptPoppet : Curse() {
    /*
    cat
   AP: 7000
   taglock
   exhale
   voodoo prot
   blaze powder
   brew of grotesque

   11x11 infernal
    */

    override fun onTickCurse(level: Level, player: Player, catBoosted: Boolean) {
        if (level is ServerLevel) {

        }


        super.onTickCurse(level, player, catBoosted)
    }
}