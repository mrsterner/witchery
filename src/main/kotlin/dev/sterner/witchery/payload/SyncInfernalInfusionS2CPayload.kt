package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.infusion.CreatureType
import dev.sterner.witchery.data_attachment.infusion.InfernalInfusionPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncInfernalInfusionS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: InfernalInfusionPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        val entityTypeKey = data.currentCreature.entityType?.let {
            BuiltInRegistries.ENTITY_TYPE.getKey(it).toString()
        } ?: "none"
        putString("EntityType", entityTypeKey)
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
        val entityType = nbt.getString("EntityType")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                val creatureType = runCatching {
                    CreatureType.valueOf(entityType.uppercase())
                }.getOrElse {
                    CreatureType.NONE
                }
                InfernalInfusionPlayerAttachment.setData(player, InfernalInfusionPlayerAttachment.Data(creatureType))
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncInfernalInfusionS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_infernal_infusion"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncInfernalInfusionS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncInfernalInfusionS2CPayload(buf) }
            )
    }
}