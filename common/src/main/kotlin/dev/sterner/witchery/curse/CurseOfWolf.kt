package dev.sterner.witchery.curse

import dev.sterner.witchery.api.Curse
import dev.sterner.witchery.handler.CurseHandler
import dev.sterner.witchery.handler.werewolf.WerewolfLeveling
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class CurseOfWolf : Curse() {

    /*
    full moon, full coven
   AP: 10000
   taglock
   exhale
   wolfsbane
   tongue
   amethyst
   brew of grotesque

   11x11 infernal
    */

    override fun onAdded(level: Level, player: Player, catBoosted: Boolean) {
        super.onAdded(level, player, catBoosted)
        val data = WerewolfPlayerAttachment.getData(player)

        if (player is ServerPlayer && data.getWerewolfLevel() == 0) {
            WerewolfLeveling.increaseWerewolfLevel(player)
        }

        CurseHandler.removeCurse(player, this)
    }
}