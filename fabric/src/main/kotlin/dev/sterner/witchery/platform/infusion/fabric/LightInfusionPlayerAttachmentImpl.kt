package dev.sterner.witchery.platform.infusion.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.infusion.LightInfusionPlayerAttachment
import net.minecraft.world.entity.player.Player

object LightInfusionPlayerAttachmentImpl {

    @JvmStatic
    fun setInvisible(player: Player, invisible: Boolean, invisibleTicks: Int) {
        val prevData = player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.LIGHT_INFUSION_PLAYER_DATA_TYPE)
        if (!prevData.isInvisible && invisible) {
            LightInfusionPlayerAttachment.poof(player)
        }

        val data = LightInfusionPlayerAttachment.Data(invisible, invisibleTicks)
        player.setAttached(WitcheryFabricAttachmentRegistry.LIGHT_INFUSION_PLAYER_DATA_TYPE, data)
        LightInfusionPlayerAttachment.sync(player, data)
    }

    @JvmStatic
    fun isInvisible(player: Player): LightInfusionPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.LIGHT_INFUSION_PLAYER_DATA_TYPE)
    }
}