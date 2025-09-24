package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.possession.PossessionManager
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Mob
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.util.UUID

// Request to start possessing an entity
class PossessionRequestC2SPayload(val targetEntityId: UUID) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(buf.readUUID())

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeUUID(targetEntityId)
    }

    fun handleOnServer(context: IPayloadContext) {
        val player = context.player()
        if (player !is ServerPlayer) return

        context.enqueueWork {
            player.serverLevel().getEntity(targetEntityId)?.let { entity ->
                if (entity is Mob) {
                    PossessionManager.startPossessing(player, entity)
                }
            }
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<PossessionRequestC2SPayload> =
            CustomPacketPayload.Type(Witchery.id("possession_request"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, PossessionRequestC2SPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> PossessionRequestC2SPayload(buf) }
            )
    }
}