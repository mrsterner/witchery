package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.platform.transformation.BloodPoolLivingEntityAttachment
import dev.sterner.witchery.platform.transformation.VampirePlayerAttachment
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class SyncBloodS2CPacket(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(livingEntity: LivingEntity, data: BloodPoolLivingEntityAttachment.Data) : this(CompoundTag().apply {
        putInt("Id", livingEntity.id)
        putInt("maxBlood", data.maxBlood)
        putInt("bloodPool", data.bloodPool)
    })

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf?) {
        friendlyByteBuf?.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncBloodS2CPacket, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        val id = payload.nbt.getInt("Id")
        val vampireLevel = payload.nbt.getInt("vampireLevel")
        val bloodPool = payload.nbt.getInt("bloodPool")

        val living = client.level?.getEntity(id)

        client.execute {
            if (living is LivingEntity) {
                BloodPoolLivingEntityAttachment.setData(living, BloodPoolLivingEntityAttachment.Data(vampireLevel, bloodPool))
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncBloodS2CPacket> =
            CustomPacketPayload.Type(Witchery.id("sync_blood_living"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf?, SyncBloodS2CPacket> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncBloodS2CPacket(buf!!) }
            )
    }
}