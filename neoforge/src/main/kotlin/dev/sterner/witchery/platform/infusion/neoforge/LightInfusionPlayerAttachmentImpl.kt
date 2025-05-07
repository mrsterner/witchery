package dev.sterner.witchery.platform.infusion.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.infusion.LightInfusionPlayerAttachment
import net.minecraft.world.entity.player.Player

object LightInfusionPlayerAttachmentImpl {

    @JvmStatic
    fun setInvisible(player: Player, invisible: Boolean, invisibleTicks: Int) {
        val data = LightInfusionPlayerAttachment.Data(invisible, invisibleTicks)
        player.setData(WitcheryNeoForgeAttachmentRegistry.LIGHT_INFUSION_PLAYER_DATA_ATTACHMENT, data)
        LightInfusionPlayerAttachment.sync(player, data)
    }

    @JvmStatic
    fun isInvisible(player: Player): LightInfusionPlayerAttachment.Data {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.LIGHT_INFUSION_PLAYER_DATA_ATTACHMENT)
    }
}