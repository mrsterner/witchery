package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.necromancy.SoulPoolPlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncSoulS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: SoulPoolPlayerAttachment.Data) : this(CompoundTag().apply {
        putUUID("Player", player.uuid)
        putInt("maxSoul", data.maxSouls)
        putInt("soulPool", data.soulPool)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()

        client.execute {
            val uuid = nbt.getUUID("Player")
            val player = client.level?.getPlayerByUUID(uuid)

            if (player != null) {

                val maxBlood = nbt.getInt("maxSoul")
                val bloodPool = nbt.getInt("soulPool")
                SoulPoolPlayerAttachment.setData(
                    player,
                    SoulPoolPlayerAttachment.Data(maxBlood, bloodPool)
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncSoulS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_soul_living"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncSoulS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncSoulS2CPayload(buf) }
            )
    }
}