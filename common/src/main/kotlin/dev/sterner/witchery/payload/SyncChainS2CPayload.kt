package dev.sterner.witchery.payload

import dev.architectury.networking.NetworkManager
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.entity.ChainEntity
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.Entity

class SyncChainS2CPayload(val nbt: CompoundTag) : CustomPacketPayload {

    constructor(friendlyByteBuf: RegistryFriendlyByteBuf) : this(friendlyByteBuf.readNbt()!!)

    constructor(chain: ChainEntity, target: Entity) : this(
        CompoundTag().apply {
            putUUID("chainUUID", chain.uuid)
            putInt("targetId", target.id)
        }
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    private fun write(friendlyByteBuf: RegistryFriendlyByteBuf) {
        friendlyByteBuf.writeNbt(nbt)
    }

    fun handleS2C(payload: SyncChainS2CPayload, context: NetworkManager.PacketContext) {
        val client = Minecraft.getInstance()

        client.execute {
            val level = client.level ?: return@execute
            val chainUUID = payload.nbt.getUUID("chainUUID")

            var chainEntity: ChainEntity? = null

            for (entity in level.entitiesForRendering()) {
                if (entity is ChainEntity && entity.uuid == chainUUID) {
                    chainEntity = entity
                    break
                }
            }

            if (payload.nbt.contains("targetId")) {
                val targetId = payload.nbt.getInt("targetId")
                if (targetId >= 0) {
                    val targetEntity = level.getEntity(targetId)
                    if (targetEntity != null) {
                        chainEntity?.setTargetEntity(targetEntity)
                    }
                }
            }
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<SyncChainS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("sync_chain"))

        val STREAM_CODEC: StreamCodec<in RegistryFriendlyByteBuf, SyncChainS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> SyncChainS2CPayload(buf) }
            )
    }
}