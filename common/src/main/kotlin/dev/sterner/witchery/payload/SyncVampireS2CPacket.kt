package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.CursePlayerAttachment
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncVampireS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: VampirePlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putInt("killedBlazes", data.killedBlazes)
        putInt("usedSunGrenades", data.usedSunGrenades)
        putInt("vampireLevel", data.vampireLevel)
        putInt("villagersHalfBlood", data.villagersHalfBlood)
        putInt("nightsCount", data.nightsCount)
        putInt("visitedVillages", data.visitedVillages)
        putInt("trappedVillagers", data.trappedVillagers)
        putInt("abilityIndex", data.abilityIndex)
        putInt("inSunTick", data.inSunTick)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncVampireS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")
        val killedBlazes = payload.nbt.getInt("killedBlazes")
        val usedSunGrenades = payload.nbt.getInt("usedSunGrenades")
        val vampireLevel = payload.nbt.getInt("vampireLevel")
        val villagersHalfBlood = payload.nbt.getInt("villagersHalfBlood")
        val nightsCount = payload.nbt.getInt("nightsCount")
        val visitedVillages = payload.nbt.getInt("visitedVillages")
        val trappedVillagers = payload.nbt.getInt("trappedVillagers")
        val abilityIndex = payload.nbt.getInt("abilityIndex")
        val inSunTick = payload.nbt.getInt("inSunTick")

        val player = client.level?.getPlayerByUUID(id)
        client.execute {
            if (player != null) {
                VampirePlayerAttachment.setData(player, VampirePlayerAttachment.Data(
                    vampireLevel,
                    killedBlazes,
                    usedSunGrenades,
                    villagersHalfBlood,
                    nightsCount,
                    visitedVillages,
                    trappedVillagers,
                    abilityIndex,
                    inSunTick
                ))
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncVampireS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("sync_vampire_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SyncVampireS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncVampireS2CPacket(buf!!) }
            )
    }
}