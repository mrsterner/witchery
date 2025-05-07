package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.COVEN_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.CovenPlayerAttachment
import net.minecraft.world.entity.player.Player

object CovenPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): CovenPlayerAttachment.Data {
        return player.getData(COVEN_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: CovenPlayerAttachment.Data, sync: Boolean = true) {
        player.setData(COVEN_PLAYER_DATA_ATTACHMENT, data)
        if (sync) {
            CovenPlayerAttachment.sync(player, data)
        }
    }
}