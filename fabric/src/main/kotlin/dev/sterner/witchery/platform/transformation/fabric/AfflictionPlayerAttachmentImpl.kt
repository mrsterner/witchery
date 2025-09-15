package dev.sterner.witchery.platform.transformation.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.transformation.AfflictionPlayerAttachment
import net.minecraft.world.entity.player.Player

object AfflictionPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): AfflictionPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.AFFLICTION_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(player: Player, data: AfflictionPlayerAttachment.Data, sync: Boolean = true) {
        player.setAttached(WitcheryFabricAttachmentRegistry.AFFLICTION_PLAYER_DATA_TYPE, data)
        if (sync) {
            AfflictionPlayerAttachment.sync(player, data)
        }
    }
}