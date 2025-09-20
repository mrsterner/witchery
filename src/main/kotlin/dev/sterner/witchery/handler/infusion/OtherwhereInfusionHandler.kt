package dev.sterner.witchery.handler.infusion

import dev.sterner.witchery.data_attachment.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.data_attachment.infusion.InfusionType
import dev.sterner.witchery.data_attachment.infusion.OtherwhereInfusionPlayerAttachment
import net.minecraft.world.entity.player.Player

object OtherwhereInfusionHandler {

    fun tick(player: Player?) {
        if (player != null && InfusionPlayerAttachment.getPlayerInfusion(player).type == InfusionType.OTHERWHERE) {

            val data = OtherwhereInfusionPlayerAttachment.getInfusion(player)
            val ticks = data.teleportCooldown

            if (ticks <= 0) {
                OtherwhereInfusionPlayerAttachment.setInfusion(player, data.teleportHoldTicks, 0)
            } else {
                OtherwhereInfusionPlayerAttachment.setInfusion(player, data.teleportHoldTicks, ticks - 1)
            }
        }
    }
}