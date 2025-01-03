package dev.sterner.witchery.platform.infusion

import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.payload.SyncInfusionS2CPacket
import dev.sterner.witchery.platform.infusion.InfusionData.Companion.MAX_CHARGE
import dev.sterner.witchery.registry.WitcheryPayloads
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

object PlayerInfusionDataAttachment {

    @JvmStatic
    @ExpectPlatform
    fun setPlayerInfusion(player: Player, infusionData: InfusionData) {
        throw AssertionError()
    }

    @JvmStatic
    @ExpectPlatform
    fun getPlayerInfusion(player: Player): InfusionData {
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

    @JvmStatic
    fun increaseInfusionCharge(player: Player, toAdd: Int) {
        val currentCharge = getInfusionCharge(player)
        val newCharge = (currentCharge + toAdd).coerceAtMost(MAX_CHARGE)
        setInfusionCharge(player, newCharge)
    }

    @JvmStatic
    fun decreaseInfusionCharge(player: Player, toRemove: Int) {
        val currentCharge = getInfusionCharge(player)
        if (currentCharge > 0) {
            setInfusionCharge(player, currentCharge - toRemove)
        }
    }

    fun sync(player: Player, data: InfusionData) {
        if (player.level() is ServerLevel) {
            WitcheryPayloads.sendToPlayers(player.level(), player.blockPosition(), SyncInfusionS2CPacket(
                CompoundTag().apply {
                    putUUID("Id", player.uuid)
                    putInt("Charge", data.charge)
                    putString("Type", data.type.serializedName)
                }
            ))
        }
    }
}