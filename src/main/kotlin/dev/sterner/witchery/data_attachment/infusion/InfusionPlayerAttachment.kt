package dev.sterner.witchery.data_attachment.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncInfusionS2CPayload
import dev.sterner.witchery.registry.WitcheryDataAttachments
import dev.sterner.witchery.registry.WitcheryDataAttachments.INFUSION_PLAYER_DATA_ATTACHMENT
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.neoforged.neoforge.network.PacketDistributor

object InfusionPlayerAttachment {

    const val MAX_CHARGE = 6000

    @JvmStatic
    fun setPlayerInfusion(player: Player, infusionData: InfusionPlayerAttachment.Data) {
        player.setData(WitcheryDataAttachments.INFUSION_PLAYER_DATA_ATTACHMENT, infusionData)
        InfusionPlayerAttachment.sync(player, infusionData)
    }

    @JvmStatic
    fun getPlayerInfusion(player: Player): InfusionPlayerAttachment.Data {
        return player.getData(WitcheryDataAttachments.INFUSION_PLAYER_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setInfusionCharge(player: Player, toBe: Int) {
        val infusion = player.getData(WitcheryDataAttachments.INFUSION_PLAYER_DATA_ATTACHMENT)
        setPlayerInfusion(player, Data(infusion.type, toBe))
    }

    @JvmStatic
    fun getInfusionCharge(player: Player): Int {
        return player.getData(WitcheryDataAttachments.INFUSION_PLAYER_DATA_ATTACHMENT).charge
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(player, SyncInfusionS2CPayload(
                CompoundTag().apply {
                    putUUID("Id", player.uuid)
                    putInt("Charge", data.charge)
                    putString("Type", data.type.serializedName)
                }
            ))
        }
    }

    data class Data(val type: InfusionType = InfusionType.NONE, val charge: Int = MAX_CHARGE) {

        companion object {

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    InfusionType.CODEC.fieldOf("type").forGetter { it.type },
                    Codec.INT.fieldOf("charge").forGetter { it.charge }
                ).apply(instance, ::Data)
            }

            val ID: ResourceLocation = Witchery.id("infusion_player_data")
        }
    }
}