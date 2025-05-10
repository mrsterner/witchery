package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.CovenPlayerAttachment
import net.minecraft.world.entity.player.Player

object CovenPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): CovenPlayerAttachment.CovenData {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.COVEN_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(player: Player, data: CovenPlayerAttachment.CovenData, sync: Boolean = true) {
        player.setAttached(WitcheryFabricAttachmentRegistry.COVEN_PLAYER_DATA_TYPE, data)
        if (sync) {
            CovenPlayerAttachment.sync(player, data)
        }
    }

}