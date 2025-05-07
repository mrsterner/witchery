package dev.sterner.witchery.handler.infusion

import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.platform.infusion.InfusionType
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionPlayerAttachment.getInfusion
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionPlayerAttachment.setInfusion
import net.minecraft.world.entity.player.Player

object OtherwhereInfusionHandler {

    fun tick(player: Player?) {
        if (player != null && InfusionPlayerAttachment.getPlayerInfusion(player).type == InfusionType.OTHERWHERE) {

            val data = getInfusion(player)
            val ticks = data.teleportCooldown

            if (ticks <= 0) {
                setInfusion(player, data.teleportHoldTicks, 0)
            } else {
                setInfusion(player, data.teleportHoldTicks, ticks - 1)
            }
        }
    }
}