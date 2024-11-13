package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.ManifestationPlayerAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncVampireS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: VampirePlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putInt("vampireLevel", data.vampireLevel)
        putInt("bloodPool", data.bloodPool)
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
        val vampireLevel = payload.nbt.getInt("vampireLevel")
        val bloodPool = payload.nbt.getInt("bloodPool")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                VampirePlayerAttachment.setData(player, VampirePlayerAttachment.Data(vampireLevel, bloodPool))
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