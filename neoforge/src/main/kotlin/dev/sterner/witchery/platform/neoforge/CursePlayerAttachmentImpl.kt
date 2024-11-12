package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.CURSE_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.CursePlayerAttachment
import net.minecraft.world.entity.player.Player

object CursePlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): CursePlayerAttachment.Data {
        return player.getData(CURSE_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: CursePlayerAttachment.Data ) {
        player.setData(CURSE_PLAYER_DATA_ATTACHMENT, data)
        CursePlayerAttachment.sync(player, data)
    }
}