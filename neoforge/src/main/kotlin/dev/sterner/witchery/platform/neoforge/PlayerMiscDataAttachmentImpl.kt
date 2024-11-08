package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.PlayerMiscDataAttachment
import net.minecraft.world.entity.player.Player

object PlayerMiscDataAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): PlayerMiscDataAttachment.Data {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.MISC_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: PlayerMiscDataAttachment.Data) {
        player.setData(WitcheryNeoForgeAttachmentRegistry.MISC_PLAYER_DATA_ATTACHMENT, data)
        PlayerMiscDataAttachment.sync(player, data)
    }
}