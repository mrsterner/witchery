package dev.sterner.witchery.platform.transformation.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.AFFLICTION_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import net.minecraft.world.entity.player.Player

object AfflictionPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): AfflictionPlayerAttachment.Data {
        return player.getData(AFFLICTION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: AfflictionPlayerAttachment.Data, sync: Boolean = true) {
        player.setData(AFFLICTION_PLAYER_DATA_ATTACHMENT, data)
        if (sync) {
            AfflictionPlayerAttachment.sync(player, data)
        }
    }
}