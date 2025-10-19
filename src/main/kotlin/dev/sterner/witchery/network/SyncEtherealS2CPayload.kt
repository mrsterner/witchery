package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data_attachment.EtherealEntityAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.LivingEntity


class SyncEtherealS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

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

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val entityId = nbt.getInt("entity")
        val dataTag = nbt.getCompound("Data")

        val ownerUUID = if (dataTag.contains("owner")) dataTag.getUUID("owner") else null
        val canDropLoot = dataTag.getBoolean("canDropLoot")
        val isEthereal = dataTag.getBoolean("isEthereal")

        val data = EtherealEntityAttachment.Data(
            ownerUUID = ownerUUID,
            canDropLoot = canDropLoot,
            isEthereal = isEthereal
        )

        client.execute {
            val entity = client.level?.getEntity(entityId)
            if (entity is LivingEntity) {
                EtherealEntityAttachment.setData(entity, data)

                entity.refreshDimensions()
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncEtherealS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_ethereal"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncEtherealS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncEtherealS2CPayload(buf) }
            )
    }
}