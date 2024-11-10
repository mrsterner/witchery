package dev.sterner.witchery.api

import dev.sterner.witchery.platform.PlayerMiscDataAttachment
import dev.sterner.witchery.worldgen.WitcheryWorldgenKeys
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

object WitcheryApi {

    fun isInSpiritWorld(player: Player): Boolean {
        val dim = player.level().dimension()
        return dim == WitcheryWorldgenKeys.NIGHTMARE || dim == WitcheryWorldgenKeys.DREAM
    }

    fun isInSpiritWorld(level: Level): Boolean {
        val dim = level.dimension()
        return dim == WitcheryWorldgenKeys.NIGHTMARE || dim == WitcheryWorldgenKeys.DREAM
    }

    fun makePlayerWitchy(player: Player) {
        if (!PlayerMiscDataAttachment.getData(player).isWitcheryAligned) {
            PlayerMiscDataAttachment.setWitcheryAligned(player, true)
        }
    }
}