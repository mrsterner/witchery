package dev.sterner.witchery.fetish

import dev.sterner.witchery.api.FetishEffect
import dev.sterner.witchery.platform.ManifestationPlayerAttachment
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class GhostWalkingFetishEffect : FetishEffect(rangeMod = 2) {

    private fun resetManifestationTimer(player: Player){
        val data = ManifestationPlayerAttachment.getData(player)
        if (data.manifestationTimer > 0) {
            ManifestationPlayerAttachment.setManifestationTimer(player)
        }
    }

    override fun onUnknownPlayerNearbyTick(level: Level, pos: BlockPos, nearby: List<Player>) {
        for (player in nearby) {
            resetManifestationTimer(player)
        }
    }

    override fun onKnownPlayerNearbyTick(level: Level, pos: BlockPos, taggedPlayer: Player) {
        resetManifestationTimer(taggedPlayer)
    }
}