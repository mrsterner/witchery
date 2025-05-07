package dev.sterner.witchery.platform.infusion.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment
import net.minecraft.world.entity.player.Player

object InfusionPlayerAttachmentImpl {

    @JvmStatic
    fun setPlayerInfusion(player: Player, infusionData: InfusionPlayerAttachment.Data) {
        player.setData(WitcheryNeoForgeAttachmentRegistry.INFUSION_PLAYER_DATA_ATTACHMENT, infusionData)
        InfusionPlayerAttachment.sync(player, infusionData)
    }

    @JvmStatic
    fun getPlayerInfusion(player: Player): InfusionPlayerAttachment.Data {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.INFUSION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setInfusionCharge(player: Player, toBe: Int) {
        val infusion = player.getData(WitcheryNeoForgeAttachmentRegistry.INFUSION_PLAYER_DATA_ATTACHMENT)
        setPlayerInfusion(player, InfusionPlayerAttachment.Data(infusion.type, toBe))
    }

    @JvmStatic
    fun getInfusionCharge(player: Player): Int {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.INFUSION_PLAYER_DATA_ATTACHMENT).charge
    }
}