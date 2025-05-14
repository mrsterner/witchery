package dev.sterner.witchery.platform.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.UnderWaterBreathPlayerAttachment
import net.minecraft.world.entity.player.Player

object UnderWaterBreathPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): UnderWaterBreathPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.UNDERWATER_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setData(player: Player, data: UnderWaterBreathPlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.UNDERWATER_PLAYER_DATA_TYPE, data)
        UnderWaterBreathPlayerAttachment.sync(player, data)
    }
}