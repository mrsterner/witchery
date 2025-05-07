package dev.sterner.witchery.platform.infusion.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionPlayerAttachment
import net.minecraft.world.entity.player.Player

object OtherwhereInfusionPlayerAttachmentImpl {

    @JvmStatic
    fun setInfusion(player: Player, teleportHoldTicks: Int, teleportCooldown: Int) {
        val data = OtherwhereInfusionPlayerAttachment.Data(teleportHoldTicks, teleportCooldown)
        player.setData(WitcheryNeoForgeAttachmentRegistry.OTHERWHERE_INFUSION_PLAYER_DATA_ATTACHMENT, data)
        OtherwhereInfusionPlayerAttachment.sync(player, data)
    }

    @JvmStatic
    fun getInfusion(player: Player): OtherwhereInfusionPlayerAttachment.Data {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.OTHERWHERE_INFUSION_PLAYER_DATA_ATTACHMENT)
    }
}