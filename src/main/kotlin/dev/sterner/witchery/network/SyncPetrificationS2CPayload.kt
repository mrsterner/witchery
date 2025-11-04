package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.curse.CursePlayerAttachment
import dev.sterner.witchery.features.petrification.PetrifiedEntityAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player


class SyncPetrificationS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(living: LivingEntity, data: PetrifiedEntityAttachment.Data) : this(CompoundTag().apply {
        putInt("EntityId", living.id)
        PetrifiedEntityAttachment.Data.CODEC.encodeStart(NbtOps.INSTANCE, data)
            .resultOrPartial()
            .ifPresent { put("Petri", it) }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val entityId = nbt.getInt("EntityId")
        val dataTag = nbt.getCompound("Petri")
        val petri = PetrifiedEntityAttachment.Data.CODEC.parse(NbtOps.INSTANCE, dataTag)
            .resultOrPartial()

        client.execute {
            val entity = client.level?.getEntity(entityId)
            if (entity is LivingEntity && petri.isPresent) {
                PetrifiedEntityAttachment.setData(entity, petri.get())
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncPetrificationS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_petri_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncPetrificationS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncPetrificationS2CPayload(buf) }
            )
    }
}