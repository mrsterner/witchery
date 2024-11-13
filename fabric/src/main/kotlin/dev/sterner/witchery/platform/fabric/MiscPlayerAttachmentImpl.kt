package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.MiscPlayerAttachment
import net.minecraft.world.entity.player.Player

object MiscPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): MiscPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.MISC_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: MiscPlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.MISC_PLAYER_DATA_ATTACHMENT, data)
        MiscPlayerAttachment.sync(player, data)
    }
}