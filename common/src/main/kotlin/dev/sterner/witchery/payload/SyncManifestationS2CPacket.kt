package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.ManifestationPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncManifestationS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: ManifestationPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putBoolean("hasRiteOfManifestation", data.hasRiteOfManifestation)
        putInt("manifestationTimer", data.manifestationTimer)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncManifestationS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")
        val hasRiteOfManifestation = payload.nbt.getBoolean("hasRiteOfManifestation")
        val manifestationTimer = payload.nbt.getInt("manifestationTimer")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                ManifestationPlayerAttachment.setData(
                    player,
                    ManifestationPlayerAttachment.Data(hasRiteOfManifestation, manifestationTimer)
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncManifestationS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("sync_manifestation_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SyncManifestationS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncManifestationS2CPacket(buf) }
            )
    }
}