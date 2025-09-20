package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.transformation.BloodPoolLivingEntityAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.LivingEntity

class SyncOtherBloodS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(living: LivingEntity, data: BloodPoolLivingEntityAttachment.Data) : this(CompoundTag().apply {
        putInt("living", living.id)
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

            val uuid = nbt.getInt("living")
            val target = client.level?.getEntity(uuid)

            if (target is LivingEntity) {
                val maxBlood = nbt.getInt("maxBlood")
                val bloodPool = nbt.getInt("bloodPool")
                BloodPoolLivingEntityAttachment.setData(
                    target,
                    BloodPoolLivingEntityAttachment.Data(maxBlood, bloodPool)
                )
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncOtherBloodS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_other_blood_living"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncOtherBloodS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncOtherBloodS2CPayload(buf) }
            )
    }
}