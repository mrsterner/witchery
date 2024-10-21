package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.infusion.*
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

class SyncLightInfusionS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(data: LightInfusionData): this(CompoundTag().apply {
        putBoolean("Invisible", data.isInvisible)
        putInt("InvisibleTimer", data.invisibleTimer)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncLightInfusionS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val charge = payload.nbt.getBoolean("Invisible")
        val timer = payload.nbt.getInt("InvisibleTimer")

        client.execute {
            LightInfusionDataAttachment.setInvisible(context.player, charge, timer)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncLightInfusionS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("sync_light_infusion"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SyncLightInfusionS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncLightInfusionS2CPacket(buf!!) }
            )
    }
}