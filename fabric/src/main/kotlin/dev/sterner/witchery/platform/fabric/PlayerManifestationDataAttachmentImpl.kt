package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.PlayerManifestationDataAttachment
import dev.sterner.witchery.platform.PlayerMiscDataAttachment
import dev.sterner.witchery.platform.infusion.LightInfusionDataAttachment
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object PlayerManifestationDataAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): PlayerManifestationDataAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.MANIFESTATION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: PlayerManifestationDataAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.MANIFESTATION_PLAYER_DATA_ATTACHMENT, data)
        PlayerManifestationDataAttachment.sync(player, data)
    }
}