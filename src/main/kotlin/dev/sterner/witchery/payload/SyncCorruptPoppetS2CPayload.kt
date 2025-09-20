package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.poppet.CorruptPoppetPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncCorruptPoppetS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: CorruptPoppetPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        CorruptPoppetPlayerAttachment.Data.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("corruptList", it.get())
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

        val dataTag = nbt.getCompound("corruptList")
        val playerCurseData = CorruptPoppetPlayerAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()


        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null && playerCurseData.isPresent) {
                CorruptPoppetPlayerAttachment.setData(player, playerCurseData.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncCorruptPoppetS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_corrupt_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncCorruptPoppetS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncCorruptPoppetS2CPayload(buf) }
            )
    }
}