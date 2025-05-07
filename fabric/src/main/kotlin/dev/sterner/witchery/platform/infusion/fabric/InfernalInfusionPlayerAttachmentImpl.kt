package dev.sterner.witchery.platform.infusion.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.infusion.InfernalInfusionPlayerAttachment
import net.minecraft.world.entity.player.Player

object InfernalInfusionPlayerAttachmentImpl {

    @JvmStatic
    fun setData(player: Player, data: InfernalInfusionPlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.INFERNAL_INFUSION_PLAYER_DATA_TYPE, data)
        InfernalInfusionPlayerAttachment.sync(player, data)
    }

    @JvmStatic
    fun getData(player: Player): InfernalInfusionPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.INFERNAL_INFUSION_PLAYER_DATA_TYPE)
    }
}