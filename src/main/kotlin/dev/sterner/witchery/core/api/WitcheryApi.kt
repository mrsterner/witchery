package dev.sterner.witchery.core.api

import dev.sterner.witchery.content.worldgen.WitcheryWorldgenKeys
import dev.sterner.witchery.features.misc.MiscPlayerAttachment
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

object WitcheryApi {

    /**
     * True if the player is in the spirit world, aka the nightmare world or the dream world
     */
    fun isInSpiritWorld(player: Player): Boolean {
        val level = player.level()
        return isInSpiritWorld(level)
    }

    /**
     * True if the level is the spirit world
     */
    fun isInSpiritWorld(level: Level): Boolean {
        val dim = level.dimension()
        return dim == WitcheryWorldgenKeys.NIGHTMARE || dim == WitcheryWorldgenKeys.DREAM
    }

    /**
     * Used to make the player a witch, Witchery will be stronger towards this player
     */
    fun makePlayerWitchy(player: Player) {
        if (!MiscPlayerAttachment.getData(player).isWitcheryAligned) {
            MiscPlayerAttachment.setWitcheryAligned(player, true)
        }
    }

    /**
     * True if the player is considered a witch. Witchery against this player will be stronger.
     * This to not wreck players who are not interested in witchery
     */
    fun isWitchy(maybeEntity: Player): Boolean {
        return MiscPlayerAttachment.getData(maybeEntity).isWitcheryAligned
    }
}