package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncLightInfusionS2CPacket
import dev.sterner.witchery.payload.SyncMiscS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player

object PlayerMiscDataAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(player: Player): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(player: Player, data: Data) {
        throw AssertionError()
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncMiscS2CPacket(
                CompoundTag().apply {
                    putUUID("Id", player.uuid)
                    putBoolean("hasRiteOfManifestation", data.hasRiteOfManifestation)
                }
            ))
        }
    }

    class Data(val hasRiteOfManifestation: Boolean = false) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("hasRiteOfManifestation").forGetter { it.hasRiteOfManifestation },
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("misc_player_data")
        }
    }
}