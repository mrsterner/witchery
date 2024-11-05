package dev.sterner.witchery.platform.infusion.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.platform.infusion.LightInfusionDataAttachment
import net.minecraft.world.entity.player.Player

object LightInfusionDataAttachmentImpl {

    @JvmStatic
    fun setInvisible(player: Player, invisible: Boolean, invisibleTicks: Int) {
        val prevData = player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.LIGHT_INFUSION_PLAYER_DATA_TYPE)
        if (!prevData.isInvisible && invisible) {
            LightInfusionDataAttachment.poof(player)
        }

        val data = LightInfusionData(invisible, invisibleTicks)
        player.setAttached(WitcheryFabricAttachmentRegistry.LIGHT_INFUSION_PLAYER_DATA_TYPE, data)
        LightInfusionDataAttachment.sync(player, data)
    }

    @JvmStatic
    fun isInvisible(player: Player): LightInfusionData {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.LIGHT_INFUSION_PLAYER_DATA_TYPE)
    }

}