package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.BARK_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.BarkBeltPlayerAttachment
import net.minecraft.world.entity.player.Player

object BarkBeltPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): BarkBeltPlayerAttachment.Data {
        return player.getData(BARK_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: BarkBeltPlayerAttachment.Data) {
        player.setData(BARK_PLAYER_DATA_ATTACHMENT, data)
        BarkBeltPlayerAttachment.sync(player, data)
    }
}