package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.EtherealEntityAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.LivingEntity


/**
 * Fixed S2C packet for ethereal entity sync
 */
class SyncEtherealS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(entityId: Int, data: EtherealEntityAttachment.Data) : this(CompoundTag().apply {
        putInt("entity", entityId)

        put("Data", CompoundTag().apply {
            if (data.ownerUUID != null) {
                putUUID("owner", data.ownerUUID)
            }
            putBoolean("canDropLoot", data.canDropLoot)
            putBoolean("isEthereal", data.isEthereal)
        })
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncEtherealS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()
        val entityId = payload.nbt.getInt("entity")
        val dataTag = payload.nbt.getCompound("Data")

        val ownerUUID = if (dataTag.contains("owner")) dataTag.getUUID("owner") else null
        val canDropLoot = dataTag.getBoolean("canDropLoot")
        val isEthereal = dataTag.getBoolean("isEthereal")

        val data = EtherealEntityAttachment.Data(
            ownerUUID = ownerUUID,
            canDropLoot = canDropLoot,
            isEthereal = isEthereal
        )

        client.execute {
            try {
                val entity = client.level?.getEntity(entityId)
                if (entity is LivingEntity) {
                    // Set data directly
                    EtherealEntityAttachment.setData(entity, data)

                    entity.refreshDimensions()
                }
            } catch (_: Exception) {

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