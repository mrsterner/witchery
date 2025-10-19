package dev.sterner.witchery.features.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncLightInfusionS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object LightInfusionPlayerAttachment {

    @JvmStatic
    fun setInvisible(player: Player, invisible: Boolean, invisibleTicks: Int) {
        val data = Data(invisible, invisibleTicks)
        player.setData(WitcheryDataAttachments.LIGHT_INFUSION_PLAYER_DATA_ATTACHMENT, data)
        sync(player, data)
    }

    @JvmStatic
    fun isInvisible(player: Player): Data {
        return player.getData(WitcheryDataAttachments.LIGHT_INFUSION_PLAYER_DATA_ATTACHMENT)
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player, SyncLightInfusionS2CPayload(
                    CompoundTag().apply {
                        putUUID("Id", player.uuid)
                        putBoolean("Invisible", data.isInvisible)
                        putInt("InvisibleTimer", data.invisibleTimer)
                    }
                ))
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