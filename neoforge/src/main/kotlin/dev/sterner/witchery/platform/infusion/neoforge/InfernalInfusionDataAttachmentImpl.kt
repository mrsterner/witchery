package dev.sterner.witchery.platform.infusion.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.infusion.InfernalInfusionData
import dev.sterner.witchery.platform.infusion.InfernalInfusionDataAttachment
import net.minecraft.world.entity.player.Player

object InfernalInfusionDataAttachmentImpl {

    @JvmStatic
    fun setData(player: Player, data: InfernalInfusionData) {
        player.setData(INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT, data)
        InfernalInfusionDataAttachment.sync(player, data)
    }

    @JvmStatic
    fun getData(player: Player): InfernalInfusionData {
        return player.getData(INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT)
    }
}