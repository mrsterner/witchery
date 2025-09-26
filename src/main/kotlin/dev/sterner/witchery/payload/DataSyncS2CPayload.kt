package dev.sterner.witchery.payload

import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.possession.movement.ClientMovementRegistry
import dev.sterner.witchery.data_attachment.possession.movement.MovementAltererAttachment
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.EntityType
import kotlin.let

class DataSyncS2CPayload(
    val configs: Map<EntityType<*>, MovementAltererAttachment.SerializableMovementConfig>
) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(
        deserializeConfigs(buf)
    )

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    fun write(buf: RegistryFriendlyByteBuf) {
        serializeConfigs(buf, configs)
    }

    fun handleOnClient() {
        val client = Minecraft.getInstance()
        client.execute {
            ClientMovementRegistry.entityMovementConfigs.clear()
            ClientMovementRegistry.entityMovementConfigs.putAll(configs)
        }
    }

    companion object {
        val TYPE: CustomPacketPayload.Type<DataSyncS2CPayload> =
            CustomPacketPayload.Type(Witchery.id("data_sync"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DataSyncS2CPayload> =
            CustomPacketPayload.codec(
                { payload, buf -> payload.write(buf) },
                { buf -> DataSyncS2CPayload(buf) }
            )

        private fun serializeConfigs(
            buf: RegistryFriendlyByteBuf,
            configs: Map<EntityType<*>, MovementAltererAttachment.SerializableMovementConfig>
        ) {
            buf.writeInt(configs.size)
            configs.forEach { (type, config) ->
                buf.writeResourceLocation(BuiltInRegistries.ENTITY_TYPE.getKey(type))
                val tag = CompoundTag()
                MovementAltererAttachment.SerializableMovementConfig.CODEC
                    .encodeStart(NbtOps.INSTANCE, config)
                    .resultOrPartial()?.let { tag.put("config", it.get()) }
                buf.writeNbt(tag)
            }
        }

        private fun deserializeConfigs(buf: RegistryFriendlyByteBuf): Map<EntityType<*>, MovementAltererAttachment.SerializableMovementConfig> {
            val configs = mutableMapOf<EntityType<*>, MovementAltererAttachment.SerializableMovementConfig>()
            val size = buf.readInt()
            repeat(size) {
                val typeId = buf.readResourceLocation()
                val tag = buf.readNbt()

                val entityType = BuiltInRegistries.ENTITY_TYPE.get(typeId)
                if (tag != null && tag.contains("config")) {
                    val configTag = tag.getCompound("config")
                    MovementAltererAttachment.SerializableMovementConfig.CODEC
                        .parse(NbtOps.INSTANCE, configTag)
                        .resultOrPartial()?.let { config ->
                            configs[entityType] = config.get()
                        }
                }
            }
            return configs
        }
    }
}