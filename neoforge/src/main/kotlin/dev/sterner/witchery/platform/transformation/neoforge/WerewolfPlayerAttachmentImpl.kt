package dev.sterner.witchery.platform.transformation.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.WEREWOLF_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.world.entity.player.Player

object WerewolfPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): WerewolfPlayerAttachment.Data {
        return player.getData(WEREWOLF_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: WerewolfPlayerAttachment.Data, sync: Boolean = true) {
        player.setData(WEREWOLF_PLAYER_DATA_ATTACHMENT, data)
        if (sync) {
            WerewolfPlayerAttachment.sync(player, data)
        }
    }
}