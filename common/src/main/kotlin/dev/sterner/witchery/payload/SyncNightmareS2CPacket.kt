package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.NightmarePlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncNightmareS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: NightmarePlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        NightmarePlayerAttachment.Data.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("NightmareData", it.get())
        }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncNightmareS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")

        val dataTag = payload.nbt.getCompound("NightmareData")
        val playerCurseData = NightmarePlayerAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()


        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null && playerCurseData.isPresent) {
                NightmarePlayerAttachment.setData(player, playerCurseData.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncNightmareS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("sync_nightmare_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncNightmareS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncNightmareS2CPacket(buf) }
            )
    }
}