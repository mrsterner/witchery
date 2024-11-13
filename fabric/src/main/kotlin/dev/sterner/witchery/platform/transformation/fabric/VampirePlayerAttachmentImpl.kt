package dev.sterner.witchery.platform.transformation.fabric

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.minecraft.world.entity.player.Player

object VampirePlayerAttachmentImpl {


    @JvmStatic
    fun getData(player: Player): VampirePlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.VAMPIRE_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(player: Player, data: VampirePlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.VAMPIRE_PLAYER_DATA_TYPE, data)
        VampirePlayerAttachment.sync(player, data)
    }

}