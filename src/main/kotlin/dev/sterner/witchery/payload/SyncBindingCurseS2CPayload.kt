package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.BindingCurseAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncBindingCurseS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: BindingCurseAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        BindingCurseAttachment.Data.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("bindingData", it.get())
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

        val id = nbt.getUUID("Id")

        val dataTag = nbt.getCompound("bindingData")
        val bindingData = BindingCurseAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()


        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null && bindingData.isPresent) {
                BindingCurseAttachment.setData(player, bindingData.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncBindingCurseS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_binding_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncBindingCurseS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncBindingCurseS2CPayload(buf) }
            )
    }
}