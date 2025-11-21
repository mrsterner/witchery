package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.curse.CursePlayerAttachment
import dev.sterner.witchery.features.mirror.MirrorStuckPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncMirrorStuckS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(data: MirrorStuckPlayerAttachment.Data) : this(CompoundTag().apply {
        MirrorStuckPlayerAttachment.Data.DATA_CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("List", it.get())
        }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()

        val dataTag = nbt.getCompound("List")
        val data = MirrorStuckPlayerAttachment.Data.DATA_CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()

        client.execute {
            if (client.level != null && data.isPresent) {
                MirrorStuckPlayerAttachment.setData(client.level!!, data.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncMirrorStuckS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_mirror_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncMirrorStuckS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncMirrorStuckS2CPayload(buf) }
            )
    }
}