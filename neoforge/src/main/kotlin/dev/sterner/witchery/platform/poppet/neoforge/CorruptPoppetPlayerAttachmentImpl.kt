package dev.sterner.witchery.platform.poppet.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.poppet.CorruptPoppetPlayerAttachment.Data
import net.minecraft.world.entity.player.Player

object CorruptPoppetPlayerAttachmentImpl {

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryNeoForgeAttachmentRegistry.CORRUPT_POPPET_DATA_ATTACHMENT, data)
    }

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.CORRUPT_POPPET_DATA_ATTACHMENT)
    }

}