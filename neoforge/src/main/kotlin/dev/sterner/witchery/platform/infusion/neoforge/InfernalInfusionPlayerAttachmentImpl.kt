package dev.sterner.witchery.platform.infusion.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry.INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.platform.infusion.InfernalInfusionPlayerAttachment
import net.minecraft.world.entity.player.Player

object InfernalInfusionPlayerAttachmentImpl {

    @JvmStatic
    fun setData(player: Player, data: InfernalInfusionPlayerAttachment.Data) {
        player.setData(INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT, data)
        InfernalInfusionPlayerAttachment.sync(player, data)
    }

    @JvmStatic
    fun getData(player: Player): InfernalInfusionPlayerAttachment.Data {
        return player.getData(INFERNAL_INFUSION_PLAYER_DATA_ATTACHMENT)
    }
}