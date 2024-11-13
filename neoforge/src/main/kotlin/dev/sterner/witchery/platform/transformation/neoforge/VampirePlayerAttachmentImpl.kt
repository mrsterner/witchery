package dev.sterner.witchery.platform.transformation.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.VAMPIRE_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.minecraft.world.entity.player.Player

object VampirePlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): VampirePlayerAttachment.Data {
        return player.getData(VAMPIRE_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: VampirePlayerAttachment.Data) {
        player.setData(VAMPIRE_PLAYER_DATA_ATTACHMENT, data)
        VampirePlayerAttachment.sync(player, data)
    }

}