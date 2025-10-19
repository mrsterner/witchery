package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.possession.PossessedDataAttachment
import net.minecraft.client.Minecraft
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.LivingEntity

class SyncPossessedDataS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(entityId: Int, data: PossessedDataAttachment.Data, registry: HolderLookup.Provider) : this(
        CompoundTag().apply {
            putInt("EntityId", entityId)

            PossessedDataAttachment.Data.codec(registry).encodeStart(NbtOps.INSTANCE, data)
                .resultOrPartial()?.let {
                    put("PossessedData", it.get())
                }
        }
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val level = client.level ?: return
        val entityId = nbt.getInt("EntityId")

        level.getEntity(entityId)?.let { entity ->
            if (entity is LivingEntity) {
                val dataTag = nbt.getCompound("PossessedData")
                val possessedData = PossessedDataAttachment.Data.codec(level.registryAccess())
                    .parse(NbtOps.INSTANCE, dataTag).resultOrPartial()

                possessedData?.let { data ->
                    client.execute {
                        PossessedDataAttachment.set(entity, data.get())
                    }
                }
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncPossessedDataS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_possessed_data"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncPossessedDataS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncPossessedDataS2CPayload(buf) }
            )
    }
}
