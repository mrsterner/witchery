package dev.sterner.witchery.features.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.WitcheryApi
import dev.sterner.witchery.network.SyncInfusionS2CPayload
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object InfusionPlayerAttachment {

    const val MAX_CHARGE = 6000

    @JvmStatic
    fun getData(player: Player): Data {
        return player.getData(WitcheryDataAttachments.INFUSION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(player: Player, data: Data) {
        player.setData(WitcheryDataAttachments.INFUSION_PLAYER_DATA_ATTACHMENT, data)
        sync(player, data)
    }

    @JvmStatic
    fun setInfusionCharge(player: Player, charge: Int) {
        val current = getData(player)
        setData(player, current.copy(charge = charge))
        WitcheryApi.makePlayerWitchy(player)
    }

    @JvmStatic
    fun getInfusionCharge(player: Player): Int {
        return getData(player).charge
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(
                player,
                SyncInfusionS2CPayload(
                    CompoundTag().apply {
                        putUUID("Id", player.uuid)
                        putInt("Charge", data.charge)
                        putString("Type", data.type.serializedName)
                    }
                )
            )
        }
    }

    data class Data(val type: InfusionType = InfusionType.NONE, val charge: Int = MAX_CHARGE) {

        companion object {
            val ID: ResourceLocation = Witchery.id("infusion_player_data")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    InfusionType.CODEC.fieldOf("type").forGetter { it.type },
                    Codec.INT.fieldOf("charge").forGetter { it.charge }
                ).apply(instance, ::Data)
            }
        }
    }
}