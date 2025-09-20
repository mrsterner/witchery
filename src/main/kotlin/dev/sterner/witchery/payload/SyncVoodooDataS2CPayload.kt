package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.poppet.VoodooPoppetLivingEntityAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncVoodooDataS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: VoodooPoppetLivingEntityAttachment.VoodooPoppetData) : this(CompoundTag().apply {
        putUUID("Id", player.uuid)
        putBoolean("isUnderWater", data.isUnderWater)
        putInt("ticks", data.underWaterTicks)
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
        val isUnderWater = nbt.getBoolean("isUnderWater")
        val ticks = nbt.getInt("ticks")

        val player = client.level?.getPlayerByUUID(id)

        client.execute {
            if (player != null) {
                VoodooPoppetLivingEntityAttachment.setPoppetData(
                    player,
                    VoodooPoppetLivingEntityAttachment.VoodooPoppetData(isUnderWater, ticks)
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncVoodooDataS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_voodoo_poppet"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncVoodooDataS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncVoodooDataS2CPayload(buf) }
            )
    }
}