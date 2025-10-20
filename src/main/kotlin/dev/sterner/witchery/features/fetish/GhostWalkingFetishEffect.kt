package dev.sterner.witchery.features.fetish

import dev.sterner.witchery.core.api.FetishEffect
import dev.sterner.witchery.content.block.effigy.EffigyBlockEntity
import dev.sterner.witchery.features.spirit_world.ManifestationPlayerAttachment
import dev.sterner.witchery.features.spirit_world.ManifestationHandler
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class GhostWalkingFetishEffect : FetishEffect(rangeMod = 2) {

    private fun resetManifestationTimer(player: Player) {
        val data = ManifestationPlayerAttachment.getData(player)
        if (data.manifestationTimer > 0) {
            ManifestationHandler.setManifestationTimer(player)
        }
    }

    override fun onUnknownPlayerNearbyTick(
        level: Level,
        blockEntity: EffigyBlockEntity,
        pos: BlockPos,
        nearby: List<Player>
    ) {
        for (player in nearby) {
            resetManifestationTimer(player)
        }
    }

    override fun onKnownPlayerNearbyTick(
        level: Level,
        blockEntity: EffigyBlockEntity,
        pos: BlockPos,
        taggedPlayer: Player
    ) {
        resetManifestationTimer(taggedPlayer)
    }
}