package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.MANIFESTATION_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.PlayerManifestationDataAttachment
import net.minecraft.world.entity.player.Player

object PlayerManifestationDataAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): PlayerManifestationDataAttachment.Data {
        return player.getData(MANIFESTATION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: PlayerManifestationDataAttachment.Data) {
        player.setData(MANIFESTATION_PLAYER_DATA_ATTACHMENT, data)
        PlayerManifestationDataAttachment.sync(player, data)
    }
}