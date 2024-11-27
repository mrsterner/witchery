package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.BarkBeltPlayerAttachment
import net.minecraft.world.entity.player.Player

object BarkBeltPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): BarkBeltPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.BARK_BELT_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: BarkBeltPlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.BARK_BELT_PLAYER_DATA_ATTACHMENT, data)
        BarkBeltPlayerAttachment.sync(player, data)
    }
}