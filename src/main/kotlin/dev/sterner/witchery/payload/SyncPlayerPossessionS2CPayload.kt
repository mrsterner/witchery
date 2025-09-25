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
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import java.util.UUID

class SyncPlayerPossessionS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readNbt()!!)

    constructor(playerId: UUID, data: PossessionAttachment.PlayerPossessionData) : this(CompoundTag().apply {
        putUUID("PlayerId", playerId)

        PossessionAttachment.PlayerPossessionData.CODEC.encodeStart(NbtOps.INSTANCE, data)
            .resultOrPartial { error ->
                println("Error encoding player possession data: $error")
            }
            .ifPresent { put("PossessionData", it) }
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val level = client.level ?: return
        val playerId = nbt.getUUID("PlayerId")

        level.players().firstOrNull { it.uuid == playerId }?.let { player ->
            val dataTag = nbt.getCompound("PossessionData")
            PossessionAttachment.PlayerPossessionData.CODEC.parse(NbtOps.INSTANCE, dataTag)
                .resultOrPartial { error ->
                    println("Error parsing player possession data: $error")
                }
                .ifPresent { data ->
                    client.execute {
                        player.setData(WitcheryDataAttachments.PLAYER_POSSESSION, data)

                        // Update camera if this is the local player and possession state changed
                        if (player == client.player) {
                            updateCamera(player, data)
                        }
                    }
                }
        }
    }

    private fun updateCamera(player: Player, data: PossessionAttachment.PlayerPossessionData) {
        val client = Minecraft.getInstance()

        if (data.possessedEntityNetworkId != -1) {
            // Find the possessed entity using network ID and set it as camera
            client.level?.getEntity(data.possessedEntityNetworkId)?.let { host ->
                if (client.options.cameraType.isFirstPerson && player == client.player) {
                    client.cameraEntity = host
                }
            }
        } else {
            // Reset camera to player
            if (client.cameraEntity != player) {
                client.cameraEntity = player
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncPlayerPossessionS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_player_possessable"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncPlayerPossessionS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncPlayerPossessionS2CPayload(buf) }
            )
    }
}