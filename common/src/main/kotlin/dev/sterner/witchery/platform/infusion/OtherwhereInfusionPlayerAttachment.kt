package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncOtherwhereInfusionS2CPacket
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object OtherwhereInfusionPlayerAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setInfusion(player: Player, teleportHoldTicks: Int, teleportCooldown: Int) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getInfusion(player: Player): Data {
        throw AssertionError()
    }

    fun sync(player: Player, data: Data) {
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
        if (player != null && InfusionPlayerAttachment.getPlayerInfusion(player).type == InfusionType.OTHERWHERE) {

            val data = getInfusion(player)
            val ticks = data.teleportCooldown

            if (ticks <= 0) {
                setInfusion(player, data.teleportHoldTicks, 0)
            } else {
                setInfusion(player, data.teleportHoldTicks, ticks - 1)
            }
        }
    }

    class Data(val teleportHoldTicks: Int = 0, val teleportCooldown: Int = 0) {

        companion object {
            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.INT.fieldOf("teleportHoldTicks").forGetter { it.teleportHoldTicks },
                    Codec.INT.fieldOf("teleportCooldown").forGetter { it.teleportCooldown }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("otherwhere_infusion_player_data")
        }
    }
}