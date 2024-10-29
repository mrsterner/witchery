package dev.sterner.witchery.platform.poppet

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.payload.SyncLightInfusionS2CPacket
import dev.sterner.witchery.payload.SyncPoppetDataS2CPacket
import dev.sterner.witchery.payload.SyncVoodooDataS2CPacket
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

object PoppetDataAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setPoppetData(livingEntity: LivingEntity, data: PoppetData){
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getPoppetData(livingEntity: LivingEntity): PoppetData {
        throw AssertionError()
    }

    fun sync(player: Player, data: PoppetData) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncPoppetDataS2CPacket(player, data))
        }
    }
}