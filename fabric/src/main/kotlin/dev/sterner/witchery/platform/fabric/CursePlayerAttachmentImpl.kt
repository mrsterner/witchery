package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.CursePlayerAttachment
import net.minecraft.world.entity.player.Player

object CursePlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): CursePlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.CURSE_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(player: Player, data: CursePlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.CURSE_PLAYER_DATA_TYPE, data)
        CursePlayerAttachment.sync(player, data)
    }
}