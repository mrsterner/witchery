package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.data_attachment.UnderWaterBreathPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncUnderWaterS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: UnderWaterBreathPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putInt("duration", data.duration)
        putInt("maxDuration", data.maxDuration)
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
        val duration = nbt.getInt("duration")
        val maxDuration = nbt.getInt("maxDuration")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                UnderWaterBreathPlayerAttachment.setData(
                    player,
                    UnderWaterBreathPlayerAttachment.Data(duration = duration, maxDuration = maxDuration)
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncUnderWaterS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_underwater"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncUnderWaterS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncUnderWaterS2CPayload(buf) }
            )
    }
}