package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.CursePlayerAttachment
import dev.sterner.witchery.platform.PlayerManifestationDataAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncCurseS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: CursePlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        CursePlayerAttachment.Data.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("playerCurseList", it.get())
        }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncCurseS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")

        val dataTag = payload.nbt.getCompound("playerCurseList")
        val playerCurseData = CursePlayerAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()


        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null && playerCurseData.isPresent) {
                CursePlayerAttachment.setData(player, playerCurseData.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncCurseS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("sync_curse_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SyncCurseS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncCurseS2CPacket(buf!!) }
            )
    }
}