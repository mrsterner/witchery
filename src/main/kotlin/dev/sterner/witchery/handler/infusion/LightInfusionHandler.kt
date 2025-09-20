package dev.sterner.witchery.handler.infusion

import dev.architectury.event.events.common.TickEvent
import dev.sterner.witchery.payload.SpawnPoofParticlesS2CPayload
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.platform.infusion.InfusionType
import dev.sterner.witchery.platform.infusion.LightInfusionPlayerAttachment.isInvisible
import dev.sterner.witchery.platform.infusion.LightInfusionPlayerAttachment.setInvisible
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object LightInfusionHandler {



    fun poof(player: Player) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SpawnPoofParticlesS2CPayload(
                CompoundTag().apply {
                    putUUID("Id", player.uuid)
                }
            ))
        }
    }

    fun tick(player: Player?) {

        if (player != null && InfusionPlayerAttachment.getPlayerInfusion(player).type == InfusionType.LIGHT) {
            if (isInvisible(player).isInvisible) {
                val ticks = isInvisible(player).invisibleTimer

                if (ticks <= 0) {
                    setInvisible(player, false, 0)
                } else {
                    setInvisible(player, true, ticks - 1)
                }
            }
        }
    }
}