package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.BarkBeltPlayerAttachment
import dev.sterner.witchery.data_attachment.possession.EntityAiToggle
import dev.sterner.witchery.data_attachment.possession.PossessionAttachment
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import java.util.UUID

class SyncAIEntityToggleS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(entityId: Int, data: EntityAiToggle.Data) : this(CompoundTag().apply {
        putInt("EntityId", entityId)
        EntityAiToggle.Data.CODEC.encodeStart(NbtOps.INSTANCE, data)
            .resultOrPartial { error ->
                println("Error encoding player possession data: $error")
            }
            .ifPresent { put("AIData", it) }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = ID

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val level = client.level ?: return
        val id = nbt.getInt("EntityId")

        val dataTag = nbt.getCompound("AIData")
        val barkData = EntityAiToggle.Data.CODEC.parse(NbtOps.INSTANCE, dataTag).resultOrPartial()

        client.execute {

            level.getEntity(id).let { entity ->
                if (entity is LivingEntity) {
                    EntityAiToggle.setEntityToggle(entity, barkData.get())
                }
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncAIEntityToggleS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_entity_ai"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncAIEntityToggleS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncAIEntityToggleS2CPayload(buf) }
            )
    }
}