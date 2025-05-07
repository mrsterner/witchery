package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SpawnPoofParticles
import dev.sterner.witchery.payload.SyncLightInfusionS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object LightInfusionPlayerAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setInvisible(player: Player, invisible: Boolean, invisibleTicks: Int) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun isInvisible(player: Player): Data {
        throw AssertionError()
    }

    fun sync(player: Player, data: Data) {
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

    class Data(val isInvisible: Boolean = false, val invisibleTimer: Int = 0) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.BOOL.fieldOf("isInvisible").forGetter { it.isInvisible },
                    Codec.INT.fieldOf("invisibleTimer").forGetter { it.invisibleTimer }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("light_infusion_player_data")
        }
    }
}