package dev.sterner.witchery.platform.poppet

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.payload.SyncVoodooDataS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object VoodooPoppetDataAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setPoppetData(livingEntity: LivingEntity, data: VoodooPoppetData) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getPoppetData(livingEntity: LivingEntity): VoodooPoppetData {
        throw AssertionError()
    }

    fun sync(player: Player, data: VoodooPoppetData) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(
                player.level(),
                player.blockPosition(),
                SyncVoodooDataS2CPacket(player, data)
            )
        }
    }
}