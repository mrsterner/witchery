package dev.sterner.witchery.network

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.features.blood.BloodPoolLivingEntityAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player

class SyncBloodS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(player: Player, data: BloodPoolLivingEntityAttachment.Data) : this(CompoundTag().apply {
        putUUID("Player", player.uuid)
        putInt("maxBlood", data.maxBlood)
        putInt("bloodPool", data.bloodPool)
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

                val maxBlood = nbt.getInt("maxBlood")
                val bloodPool = nbt.getInt("bloodPool")
                BloodPoolLivingEntityAttachment.setData(
                    player,
                    BloodPoolLivingEntityAttachment.Data(maxBlood, bloodPool)
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncBloodS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_blood_living"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncBloodS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncBloodS2CPayload(buf) }
            )
    }
}