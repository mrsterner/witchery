package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.infusion.InfusionPlayerAttachment
import dev.sterner.witchery.platform.infusion.InfusionType
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncInfusionS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: InfusionPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putInt("Charge", data.charge)
        putString("Type", data.type.serializedName) // serializedName should be in lowercase
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncInfusionS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getUUID("Id")
        val charge = payload.nbt.getInt("Charge")
        val type = InfusionType.valueOf(payload.nbt.getString("Type").uppercase())

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                InfusionPlayerAttachment.setPlayerInfusion(player, InfusionPlayerAttachment.Data(type, charge))
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncInfusionS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_infusion"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncInfusionS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncInfusionS2CPayload(buf) }
            )
    }
}