package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.MiscPlayerAttachment
import net.minecraft.world.entity.player.Player

object MiscPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): MiscPlayerAttachment.Data {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.MISC_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: MiscPlayerAttachment.Data) {
        player.setData(WitcheryNeoForgeAttachmentRegistry.MISC_PLAYER_DATA_ATTACHMENT, data)
        MiscPlayerAttachment.sync(player, data)
    }
}