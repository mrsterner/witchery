package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.ManifestationPlayerAttachment
import net.minecraft.world.entity.player.Player

object ManifestationPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): ManifestationPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.MANIFESTATION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: ManifestationPlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.MANIFESTATION_PLAYER_DATA_ATTACHMENT, data)
        ManifestationPlayerAttachment.sync(player, data)
    }
}