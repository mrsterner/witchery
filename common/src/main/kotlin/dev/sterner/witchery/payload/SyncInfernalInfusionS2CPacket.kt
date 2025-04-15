package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.infusion.InfernalInfusionData
import dev.sterner.witchery.platform.infusion.InfernalInfusionDataAttachment
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncInfernalInfusionS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: InfernalInfusionData) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        val entityTypeKey = data.currentCreature.entityType?.let {
            BuiltInRegistries.ENTITY_TYPE.getKey(it).toString()
        } ?: "none"
        putString("EntityType", entityTypeKey)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncInfernalInfusionS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")
        val entityType = payload.nbt.getString("EntityType")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                val creatureType = runCatching {
                    InfernalInfusionData.CreatureType.valueOf(entityType.uppercase())
                }.getOrElse {
                    InfernalInfusionData.CreatureType.NONE
                }
                InfernalInfusionDataAttachment.setData(player, InfernalInfusionData(creatureType))
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncInfernalInfusionS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("sync_infernal_infusion"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SyncInfernalInfusionS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncInfernalInfusionS2CPacket(buf) }
            )
    }
}