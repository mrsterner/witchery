package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.CovenPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncCovenS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: CovenPlayerAttachment.CovenData) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)

        CovenPlayerAttachment.CovenData.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("CovenData", it.get())
        }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncCovenS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")


        val dataTag = payload.nbt.getCompound("CovenData")
        val wereData = CovenPlayerAttachment.CovenData.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()

        val player = client.level?.getPlayerByUUID(id)
        client.execute {
            if (player != null && wereData.isPresent) {
                CovenPlayerAttachment.setData(player, wereData.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncCovenS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_coven_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncCovenS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncCovenS2CPayload(buf) }
            )
    }
}