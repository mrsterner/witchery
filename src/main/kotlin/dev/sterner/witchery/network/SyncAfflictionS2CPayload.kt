package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.affliction.AfflictionPlayerAttachment

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncAfflictionS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: AfflictionPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)

        AfflictionPlayerAttachment.Data.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("AffData", it.get())
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
        val dataTag = nbt.getCompound("AffData")
        val vampData = AfflictionPlayerAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()

        val player = client.level?.getPlayerByUUID(id)
        client.execute {
            if (player != null && vampData.isPresent) {
                AfflictionPlayerAttachment.setData(player, vampData.get(), sync = false)
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncAfflictionS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_affliction_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncAfflictionS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncAfflictionS2CPayload(buf) }
            )
    }
}