package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.possession.PossessionAttachment
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.LivingEntity

class SyncPossessableS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(living: LivingEntity, data: PossessionAttachment.PossessableData) : this(CompoundTag().apply {
        putUUID("EntityId", living.uuid)

        PossessionAttachment.PossessableData.CODEC.encodeStart(NbtOps.INSTANCE, data)
            .resultOrPartial { error ->
                println("Error encoding possessable data: $error")
            }
            .ifPresent { put("PossessableData", it) }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val level = client.level ?: return
        val entityId = nbt.getUUID("EntityId")

        level.entities.get(entityId)?.let { entity ->
            if (entity is LivingEntity) {
                val dataTag = nbt.getCompound("PossessableData")
                PossessionAttachment.PossessableData.CODEC.parse(NbtOps.INSTANCE, dataTag)
                    .resultOrPartial { error ->
                        println("Error parsing possessable data: $error")
                    }
                    .ifPresent { data ->
                        client.execute {
                            entity.setData(WitcheryDataAttachments.POSSESSABLE, data)
                        }
                    }
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncPossessableS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_possessable"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncPossessableS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncPossessableS2CPayload(buf) }
            )
    }
}