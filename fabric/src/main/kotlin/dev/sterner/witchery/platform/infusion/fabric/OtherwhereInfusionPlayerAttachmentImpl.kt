package dev.sterner.witchery.platform.infusion.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionPlayerAttachment
import net.minecraft.world.entity.player.Player

object OtherwhereInfusionPlayerAttachmentImpl {

    @JvmStatic
    fun setInfusion(player: Player, teleportHoldTicks: Int, teleportCooldown: Int) {
        val data = OtherwhereInfusionPlayerAttachment.Data(teleportHoldTicks, teleportCooldown)
        player.setAttached(WitcheryFabricAttachmentRegistry.OTHERWHERE_INFUSION_PLAYER_DATA_TYPE, data)
        OtherwhereInfusionPlayerAttachment.sync(player, data)
    }

    @JvmStatic
    fun getInfusion(player: Player): OtherwhereInfusionPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.OTHERWHERE_INFUSION_PLAYER_DATA_TYPE)
    }
}