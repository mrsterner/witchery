package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.PlayerMiscDataAttachment
import dev.sterner.witchery.platform.infusion.LightInfusionDataAttachment
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object PlayerMiscDataAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): PlayerMiscDataAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.MISC_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: PlayerMiscDataAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.MISC_PLAYER_DATA_ATTACHMENT, data)
        PlayerMiscDataAttachment.sync(player, data)
    }
}