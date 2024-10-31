package dev.sterner.witchery.platform.infusion.neoforge

import dev.sterner.witchery.neoforge.WitcheryNeoForgeAttachmentRegistry
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.InfusionData.Companion.MAX_CHARGE
import dev.sterner.witchery.platform.infusion.PlayerInfusionDataAttachment
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.attachment.AttachmentType
import java.util.function.Supplier

object PlayerInfusionDataAttachmentImpl {

    @JvmStatic
    fun setPlayerInfusion(player: Player, infusionData: InfusionData) {
        player.setData(WitcheryNeoForgeAttachmentRegistry.INFUSION_PLAYER_DATA_ATTACHMENT, infusionData)
        PlayerInfusionDataAttachment.sync(player, infusionData)
    }

    @JvmStatic
    fun getPlayerInfusion(player: Player): InfusionData {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.INFUSION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setInfusionCharge(player: Player, toBe: Int) {
        val infusion = player.getData(WitcheryNeoForgeAttachmentRegistry.INFUSION_PLAYER_DATA_ATTACHMENT)
        setPlayerInfusion(player, InfusionData(infusion.type, toBe))
    }

    @JvmStatic
    fun getInfusionCharge(player: Player): Int {
        return player.getData(WitcheryNeoForgeAttachmentRegistry.INFUSION_PLAYER_DATA_ATTACHMENT).charge
    }
}