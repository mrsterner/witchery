package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.possession.PossessionComponentAttachment
import net.minecraft.client.Minecraft
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player

class SyncPossessionComponentS2CPayload(
    val playerId: Int,
    val data: PossessionComponentAttachment.PossessionData
) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(
        buf.readInt(),
        PossessionComponentAttachment.PossessionData(
            buf.readInt(),
            if (buf.readBoolean()) buf.readUUID() else null
        )
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeInt(playerId)
        buf.writeInt(data.possessedEntityId)
        buf.writeBoolean(data.possessedEntityUUID != null)
        data.possessedEntityUUID?.let { buf.writeUUID(it) }
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        val level = client.level ?: return

        val entity = level.getEntity(playerId)
        if (entity is Player) {
            client.execute {
                PossessionComponentAttachment.setPossessionData(entity, data)

                if (entity == client.player) {
                    val possessedEntity = if (data.possessedEntityId != -1) {
                        level.getEntity(data.possessedEntityId)
                    } else null

                    if (client.options.cameraType.isFirstPerson) {
                        updateCamera(client.player, possessedEntity ?: entity)
                    }
                }
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<SyncPossessionComponentS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_possession_component"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SyncPossessionComponentS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncPossessionComponentS2CPayload(buf) }
            )

        private fun updateCamera(player: Player?, cameraEntity: Entity?) {
            val mc: Minecraft = Minecraft.getInstance()
            if (mc.options.cameraType.isFirstPerson && player === mc.player) {
                mc.gameRenderer.checkEntityPostEffect(cameraEntity)
            }
        }
    }
}