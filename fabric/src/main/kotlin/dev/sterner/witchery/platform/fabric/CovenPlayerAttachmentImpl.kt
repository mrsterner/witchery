package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.CovenPlayerAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.world.entity.player.Player

object CovenPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): CovenPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.COVEN_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(player: Player, data: CovenPlayerAttachment.Data, sync: Boolean = true) {
        player.setAttached(WitcheryFabricAttachmentRegistry.COVEN_PLAYER_DATA_TYPE, data)
        if (sync) {
            CovenPlayerAttachment.sync(player, data)
        }
    }

}