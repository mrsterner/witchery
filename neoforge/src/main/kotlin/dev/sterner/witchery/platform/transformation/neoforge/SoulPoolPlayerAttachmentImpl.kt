package dev.sterner.witchery.platform.transformation.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.SOUL_POOL_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.transformation.SoulPoolPlayerAttachment
import net.minecraft.world.entity.player.Player

object SoulPoolPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): SoulPoolPlayerAttachment.Data {
        return player.getData(SOUL_POOL_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: SoulPoolPlayerAttachment.Data) {
        player.setData(SOUL_POOL_PLAYER_DATA_ATTACHMENT, data)
        SoulPoolPlayerAttachment.sync(player, data)
    }
}