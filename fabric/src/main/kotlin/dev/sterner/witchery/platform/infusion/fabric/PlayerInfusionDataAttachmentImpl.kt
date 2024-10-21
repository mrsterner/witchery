package dev.sterner.witchery.platform.infusion.fabric

import dev.sterner.witchery.fabric.WitcheryFabric.Companion.INFUSION_PLAYER_DATA_TYPE
import dev.sterner.witchery.platform.infusion.InfusionData
import dev.sterner.witchery.platform.infusion.PlayerInfusionDataAttachment
import net.minecraft.world.entity.player.Player

@Suppress("UnstableApiUsage")
object PlayerInfusionDataAttachmentImpl {

    @JvmStatic
    fun setPlayerInfusion(player: Player, infusionData: InfusionData) {
        player.setAttached(INFUSION_PLAYER_DATA_TYPE, infusionData)
        PlayerInfusionDataAttachment.sync(player, infusionData)
    }

    @JvmStatic
    fun getPlayerInfusion(player: Player): InfusionData {
        return player.getAttachedOrCreate(INFUSION_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setInfusionCharge(player: Player, toBe: Int) {
        val infusion = player.getAttachedOrCreate(INFUSION_PLAYER_DATA_TYPE)
        setPlayerInfusion(player, InfusionData(infusion.type, toBe))
    }

    @JvmStatic
    fun getInfusionCharge(player: Player): Int {
        return player.getAttachedOrCreate(INFUSION_PLAYER_DATA_TYPE).charge
    }
}