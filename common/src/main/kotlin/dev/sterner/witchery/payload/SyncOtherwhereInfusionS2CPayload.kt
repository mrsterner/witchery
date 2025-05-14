package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.infusion.OtherwhereInfusionPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncOtherwhereInfusionS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: OtherwhereInfusionPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putInt("teleportHoldTicks", data.teleportHoldTicks)
        putInt("teleportCooldown", data.teleportCooldown)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncOtherwhereInfusionS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")
        val teleportHoldTicks = payload.nbt.getInt("teleportHoldTicks")
        val teleportCooldown = payload.nbt.getInt("teleportCooldown")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                OtherwhereInfusionPlayerAttachment.setInfusion(player, teleportHoldTicks, teleportCooldown)
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncOtherwhereInfusionS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_otherwhere_infusion"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncOtherwhereInfusionS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncOtherwhereInfusionS2CPayload(buf) }
            )
    }
}