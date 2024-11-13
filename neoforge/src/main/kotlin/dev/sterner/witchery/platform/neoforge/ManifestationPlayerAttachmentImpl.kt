package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.MANIFESTATION_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.ManifestationPlayerAttachment
import net.minecraft.world.entity.player.Player

object ManifestationPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): ManifestationPlayerAttachment.Data {
        return player.getData(MANIFESTATION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: ManifestationPlayerAttachment.Data) {
        player.setData(MANIFESTATION_PLAYER_DATA_ATTACHMENT, data)
        ManifestationPlayerAttachment.sync(player, data)
    }
}