package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.EtherealEntityAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.LivingEntity


class SyncEtherealS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(entity: Int, data: EtherealEntityAttachment.Data) : this(CompoundTag().apply {
        putInt("entity", entity)
        EtherealEntityAttachment.Data.CODEC.encodeStart(NbtOps.INSTANCE, data).resultOrPartial().let {
            put("Data", it.get())
        }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncEtherealS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val dataTag = payload.nbt.getCompound("Data")
        val data = EtherealEntityAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()

        val entity = client.level?.getEntity(payload.nbt.getInt("entity"))
        client.execute {
            if (entity is LivingEntity && data.isPresent) {
                EtherealEntityAttachment.setData(entity, data.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncEtherealS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("sync_ethereal"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncEtherealS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncEtherealS2CPacket(buf) }
            )
    }
}