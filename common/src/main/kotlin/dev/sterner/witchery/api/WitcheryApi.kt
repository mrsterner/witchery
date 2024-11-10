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

    /**
     * Used to make the player a witch, Witchery will be stronger towards this player
     */
    fun makePlayerWitchy(player: Player) {
        if (!PlayerMiscDataAttachment.getData(player).isWitcheryAligned) {
            PlayerMiscDataAttachment.setWitcheryAligned(player, true)
        }
    }

    /**
     * True if the player is considered a witch. Witchery against this player will be stronger.
     * This to not wreck players who are not interested in witchery
     */
    fun isWitchy(maybeEntity: Player): Boolean {
        return PlayerMiscDataAttachment.getData(maybeEntity).isWitcheryAligned
    }
}