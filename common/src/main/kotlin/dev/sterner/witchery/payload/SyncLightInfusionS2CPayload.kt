package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.infusion.LightInfusionPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncLightInfusionS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: LightInfusionPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putBoolean("Invisible", data.isInvisible)
        putInt("InvisibleTimer", data.invisibleTimer)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncLightInfusionS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")
        val charge = payload.nbt.getBoolean("Invisible")
        val timer = payload.nbt.getInt("InvisibleTimer")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                LightInfusionPlayerAttachment.setInvisible(player, charge, timer)
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncLightInfusionS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_light_infusion"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncLightInfusionS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncLightInfusionS2CPayload(buf) }
            )
    }
}