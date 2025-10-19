package dev.sterner.witchery.features.infusion

import dev.sterner.witchery.data_attachment.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.data_attachment.infusion.InfusionType
import dev.sterner.witchery.data_attachment.infusion.LightInfusionPlayerAttachment
import dev.sterner.witchery.payload.SpawnPoofParticlesS2CPayload
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object LightInfusionHandler {

    fun poof(player: Player) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player, SpawnPoofParticlesS2CPayload(
                    CompoundTag().apply {
                        putUUID("Id", player.uuid)
                    }
                ))
        }
    }

    fun tick(player: Player?) {

        if (player != null && InfusionPlayerAttachment.getPlayerInfusion(player).type == InfusionType.LIGHT) {
            if (LightInfusionPlayerAttachment.isInvisible(player).isInvisible) {
                val ticks = LightInfusionPlayerAttachment.isInvisible(player).invisibleTimer

                if (ticks <= 0) {
                    LightInfusionPlayerAttachment.setInvisible(player, false, 0)
                } else {
                    LightInfusionPlayerAttachment.setInvisible(player, true, ticks - 1)
                }
            }
        }
    }
}