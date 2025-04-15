package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.transformation.TransformationPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncTransformationS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: TransformationPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)

        TransformationPlayerAttachment.Data.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("TransData", it.get())
        }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncTransformationS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")

        val dataTag = payload.nbt.getCompound("TransData")
        val vampData = TransformationPlayerAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()

        val player = client.level?.getPlayerByUUID(id)
        client.execute {
            if (player != null && vampData.isPresent) {
                TransformationPlayerAttachment.setData(player, vampData.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncTransformationS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_transformation_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncTransformationS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncTransformationS2CPayload(buf) }
            )
    }
}