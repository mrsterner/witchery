package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.NIGHTMARE_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.NightmarePlayerAttachment
import net.minecraft.world.entity.player.Player

object NightmarePlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): NightmarePlayerAttachment.Data {
        return player.getData(NIGHTMARE_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: NightmarePlayerAttachment.Data) {
        player.setData(NIGHTMARE_PLAYER_DATA_ATTACHMENT, data)
    }
}