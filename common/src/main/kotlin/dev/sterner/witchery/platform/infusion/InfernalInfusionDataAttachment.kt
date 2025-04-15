package dev.sterner.witchery.platform.infusion

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.payload.SyncInfernalInfusionS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object InfernalInfusionDataAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setData(player: Player, data: InfernalInfusionData) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getData(player: Player): InfernalInfusionData {
        throw AssertionError()
    }

    fun sync(player: Player, data: InfernalInfusionData) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(
                player.level(),
                player.blockPosition(),
                SyncInfernalInfusionS2CPacket(player, data)
            )
        }
    }
}