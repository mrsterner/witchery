package dev.sterner.witchery.platform.infusion.fabric

import dev.sterner.witchery.fabric.WitcheryFabric.Companion.OTHERWHERE_INFUSION_PLAYER_DATA_TYPE
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionData
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionDataAttachment
import net.minecraft.world.entity.player.Player

object OtherwhereInfusionDataAttachmentImpl {

    @JvmStatic
    fun setInfusion(player: Player, teleportHoldTicks: Int, teleportCooldown: Int){
        val data = OtherwhereInfusionData(teleportHoldTicks, teleportCooldown)
        player.setAttached(OTHERWHERE_INFUSION_PLAYER_DATA_TYPE, data)
        OtherwhereInfusionDataAttachment.sync(player, data)
    }

    @JvmStatic
    fun getInfusion(player: Player): OtherwhereInfusionData {
       return player.getAttachedOrCreate(OTHERWHERE_INFUSION_PLAYER_DATA_TYPE)
    }
}