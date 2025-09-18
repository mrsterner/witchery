package dev.sterner.witchery.platform.transformation.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.transformation.SoulPoolPlayerAttachment
import net.minecraft.world.entity.player.Player

object SoulPoolPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): SoulPoolPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.SOUL_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(player: Player, data: SoulPoolPlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.SOUL_PLAYER_DATA_TYPE, data)
        SoulPoolPlayerAttachment.sync(player, data)
    }
}