package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data_attachment.ManifestationPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncManifestationS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: ManifestationPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putBoolean("hasRiteOfManifestation", data.hasRiteOfManifestation)
        putInt("manifestationTimer", data.manifestationTimer)
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
        val hasRiteOfManifestation = nbt.getBoolean("hasRiteOfManifestation")
        val manifestationTimer = nbt.getInt("manifestationTimer")

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
        val ID: CustomPacketPayload.Type<SyncManifestationS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_manifestation_player"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncManifestationS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncManifestationS2CPayload(buf) }
            )
    }
}