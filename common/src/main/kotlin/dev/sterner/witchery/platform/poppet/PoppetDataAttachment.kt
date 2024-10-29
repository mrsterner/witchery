package dev.sterner.witchery.platform.poppet

import dev.architectury.event.EventResult
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.block.poppet.PoppetBlockEntity
import dev.sterner.witchery.payload.SyncLightInfusionS2CPacket
import dev.sterner.witchery.payload.SyncPoppetDataS2CPacket
import dev.sterner.witchery.payload.SyncVoodooDataS2CPacket
import dev.sterner.witchery.platform.infusion.LightInfusionData
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import java.util.*

object PoppetDataAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setPoppetData(level: ServerLevel, data: PoppetData) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getPoppetData(level: ServerLevel): PoppetData {
        throw AssertionError()
    }


    fun sync(level: ServerLevel, data: PoppetData) {
        WitcheryPayloads.sendToPlayers(level, SyncPoppetDataS2CPacket(level, data))
    }

    fun handleBlockDestruction(level: ServerLevel, pos: BlockPos) {

    }
}