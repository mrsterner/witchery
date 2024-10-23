package dev.sterner.witchery.platform.infusion

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.payload.SpawnPoofParticles
import dev.sterner.witchery.payload.SyncLightInfusionS2CPacket
import dev.sterner.witchery.payload.SyncOtherwhereInfusionS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object OtherwhereInfusionDataAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setInfusion(player: Player, teleportHoldTicks: Int, teleportCooldown: Int){
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getInfusion(player: Player): OtherwhereInfusionData {
        throw AssertionError()
    }

    fun sync(player: Player, data: OtherwhereInfusionData) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncOtherwhereInfusionS2CPacket(
                CompoundTag().apply {
                    putUUID("Id", player.uuid)
                    putInt("teleportHoldTicks", data.teleportHoldTicks)
                    putInt("teleportCooldown", data.teleportCooldown)
                }
            ))
        }
    }

    fun tick(player: Player?) {
        if (player != null && PlayerInfusionDataAttachment.getPlayerInfusion(player).type == InfusionType.OTHERWHERE) {

            val data = getInfusion(player)
            val ticks = data.teleportCooldown

            if (ticks <= 0) {
                setInfusion(player, data.teleportHoldTicks, 0)
            } else {
                setInfusion(player, data.teleportHoldTicks, ticks - 1)
            }
        }
    }
}