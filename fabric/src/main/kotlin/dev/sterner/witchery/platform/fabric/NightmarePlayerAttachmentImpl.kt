package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.NightmarePlayerAttachment
import net.minecraft.world.entity.player.Player

object NightmarePlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): NightmarePlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.NIGHTMARE_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(player: Player, data: NightmarePlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.NIGHTMARE_PLAYER_DATA_TYPE, data)
        NightmarePlayerAttachment.sync(player, data)
    }
}