package dev.sterner.witchery.platform.transformation.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import dev.sterner.witchery.platform.transformation.WerewolfPlayerAttachment
import net.minecraft.world.entity.player.Player

object WerewolfPlayerAttachmentImpl {


    @JvmStatic
    fun getData(player: Player): WerewolfPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.WEREWOLF_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(player: Player, data: WerewolfPlayerAttachment.Data, sync: Boolean = true) {
        player.setAttached(WitcheryFabricAttachmentRegistry.WEREWOLF_PLAYER_DATA_TYPE, data)
        if (sync) {
            WerewolfPlayerAttachment.sync(player, data)
        }
    }

}