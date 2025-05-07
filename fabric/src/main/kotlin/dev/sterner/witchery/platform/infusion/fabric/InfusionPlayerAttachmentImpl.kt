package dev.sterner.witchery.platform.infusion.fabric

import dev.sterner.witchery.fabric.registry.WitcheryFabricAttachmentRegistry
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment
import net.minecraft.world.entity.player.Player

@Suppress("UnstableApiUsage")
object InfusionPlayerAttachmentImpl {

    @JvmStatic
    fun setPlayerInfusion(player: Player, infusionData: InfusionPlayerAttachment.Data) {
        player.setAttached(WitcheryFabricAttachmentRegistry.INFUSION_PLAYER_DATA_TYPE, infusionData)
        InfusionPlayerAttachment.sync(player, infusionData)
    }

    @JvmStatic
    fun getPlayerInfusion(player: Player): InfusionPlayerAttachment.Data {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.INFUSION_PLAYER_DATA_TYPE)
    }

    @JvmStatic
    fun setInfusionCharge(player: Player, toBe: Int) {
        val infusion = player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.INFUSION_PLAYER_DATA_TYPE)
        setPlayerInfusion(player, InfusionPlayerAttachment.Data(infusion.type, toBe))
    }

    @JvmStatic
    fun getInfusionCharge(player: Player): Int {
        return player.getAttachedOrCreate(WitcheryFabricAttachmentRegistry.INFUSION_PLAYER_DATA_TYPE).charge
    }
}