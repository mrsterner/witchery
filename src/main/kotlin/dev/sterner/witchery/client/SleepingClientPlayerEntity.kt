package dev.sterner.witchery.client

import com.mojang.authlib.GameProfile
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.RemotePlayer
import net.minecraft.core.NonNullList
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.PlayerModelPart
import net.minecraft.world.item.ItemStack
import kotlin.experimental.and


class SleepingClientPlayerEntity(
    clientLevel: ClientLevel,
    gameProfile: GameProfile,
    equipmentList: NonNullList<ItemStack>,
    val model: Byte
) : RemotePlayer(clientLevel, gameProfile) {

    init {
        for (type in EquipmentSlot.entries) {
            setItemSlot(type, equipmentList[type.ordinal])
        }
    }

    override fun isSpectator(): Boolean {
        return false
    }

    override fun isModelPartShown(part: PlayerModelPart): Boolean {
        return (model and part.mask.toByte()) == part.mask.toByte()
    }
}