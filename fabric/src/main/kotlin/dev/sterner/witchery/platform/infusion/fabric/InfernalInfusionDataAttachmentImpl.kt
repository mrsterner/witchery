package dev.sterner.witchery.platform.infusion.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.infusion.InfernalInfusionData
import dev.sterner.witchery.platform.infusion.InfernalInfusionDataAttachment
import net.minecraft.world.entity.player.Player

object InfernalInfusionDataAttachmentImpl {

    @JvmStatic
    fun setData(player: Player, data: InfernalInfusionData) {
        player.setAttached(WitcheryFabricAttachmentRegistry.INFERNAL_INFUSION_PLAYER_DATA_TYPE, data)
        InfernalInfusionDataAttachment.sync(player, data)
    }

    @JvmStatic
    fun getData(player: Player): InfernalInfusionData {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.INFERNAL_INFUSION_PLAYER_DATA_TYPE)
    }
}