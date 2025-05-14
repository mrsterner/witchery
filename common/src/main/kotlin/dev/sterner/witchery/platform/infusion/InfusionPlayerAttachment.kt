package dev.sterner.witchery.platform.infusion

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.payload.SyncInfusionS2CPayload
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object InfusionPlayerAttachment {

    const val MAX_CHARGE = 6000

    @JvmStatic
    @ExpectPlatform
    fun setPlayerInfusion(player: Player, infusionData: Data) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getPlayerInfusion(player: Player): Data {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun setInfusionCharge(player: Player, toAdd: Int) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getInfusionCharge(player: Player): Int {
        throw AssertionError()
    }

    fun sync(player: Player, data: Data) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncInfusionS2CPayload(
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