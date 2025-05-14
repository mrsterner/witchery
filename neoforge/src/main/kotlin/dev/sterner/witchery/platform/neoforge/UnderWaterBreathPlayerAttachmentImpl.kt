package dev.sterner.witchery.platform.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.UnderWaterBreathPlayerAttachment
import net.minecraft.world.entity.player.Player

object UnderWaterBreathPlayerAttachmentImpl {

    @JvmStatic
    fun getData(player: Player): UnderWaterBreathPlayerAttachment.Data {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.UNDER_WATER_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: UnderWaterBreathPlayerAttachment.Data) {
        player.setData(WitcheryNeoForgeAttachmentRegistry.UNDER_WATER_PLAYER_DATA_ATTACHMENT, data)
        UnderWaterBreathPlayerAttachment.sync(player, data)
    }
}