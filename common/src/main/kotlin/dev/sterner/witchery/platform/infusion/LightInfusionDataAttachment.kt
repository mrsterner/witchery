package dev.sterner.witchery.platform.infusion

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.payload.SpawnPoofParticles
import dev.sterner.witchery.payload.SyncLightInfusionS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object LightInfusionDataAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setInvisible(player: Player, invisible: Boolean, invisibleTicks: Int) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun isInvisible(player: Player): LightInfusionData {
        throw AssertionError()
    }

    fun sync(player: Player, data: LightInfusionData) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncLightInfusionS2CPacket(
                CompoundTag().apply {
                    putUUID("Id", player.uuid)
                    putBoolean("Invisible", data.isInvisible)
                    putInt("InvisibleTimer", data.invisibleTimer)
                }
            ))
        }
    }

    fun poof(player: Player) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SpawnPoofParticles(
                CompoundTag().apply {
                    putUUID("Id", player.uuid)
                }
            ))
        }
    }

    fun tick(player: Player?) {

        if (player != null && PlayerInfusionDataAttachment.getPlayerInfusion(player).type == InfusionType.LIGHT) {
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