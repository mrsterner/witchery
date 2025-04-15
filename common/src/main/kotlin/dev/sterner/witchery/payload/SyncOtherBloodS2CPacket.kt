package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.LivingEntity

class SyncOtherBloodS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(living: LivingEntity, data: BloodPoolLivingEntityAttachment.Data) : this(CompoundTag().apply {
        putInt("living", living.id)
        putInt("maxBlood", data.maxBlood)
        putInt("bloodPool", data.bloodPool)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncOtherBloodS2CPacket, context: NetworkManager.PacketContext) {

        val client = Minecraft.getInstance()

        client.execute {

            val uuid = payload.nbt.getInt("living")
            val target = client.level?.getEntity(uuid)

            if (target is LivingEntity) {
                val maxBlood = payload.nbt.getInt("maxBlood")
                val bloodPool = payload.nbt.getInt("bloodPool")
                BloodPoolLivingEntityAttachment.setData(
                    target,
                    BloodPoolLivingEntityAttachment.Data(maxBlood, bloodPool)
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncOtherBloodS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("sync_other_blood_living"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SyncOtherBloodS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncOtherBloodS2CPacket(buf) }
            )
    }
}