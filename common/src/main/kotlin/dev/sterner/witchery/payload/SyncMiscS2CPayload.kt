package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.MiscPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncMiscS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: MiscPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putBoolean("isWitcheryAligned", data.isWitcheryAligned)
        putBoolean("isDeath", data.isDeath)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncMiscS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")
        val isWitcheryAligned = payload.nbt.getBoolean("isWitcheryAligned")
        val isDeath = payload.nbt.getBoolean("isDeath")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                MiscPlayerAttachment.setData(player, MiscPlayerAttachment.Data(isWitcheryAligned, isDeath))
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncMiscS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_misc_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncMiscS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncMiscS2CPayload(buf) }
            )
    }
}