package dev.sterner.witchery.network

import com.mojang.serialization.Codec
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.data_attachment.affliction.AfflictionPlayerAttachment
import dev.sterner.witchery.data_attachment.affliction.SyncFieldRegistry

import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import java.util.UUID

class OptimizedSelectiveSyncPayload(
    val playerId: UUID,
    val changes: Map<String, Any?>
) : CustomPacketPayload {

    constructor(buf: RegistryFriendlyByteBuf) : this(
        buf.readUUID(),
        readChanges(buf)
    )

    fun write(buf: RegistryFriendlyByteBuf) {
        buf.writeUUID(playerId)
        writeChanges(buf, changes)
    }

    override fun type() = ID

    fun handleOnClient(ctx: IPayloadContext) {
        val client = Minecraft.getInstance()
        val player = client.level?.getPlayerByUUID(playerId) ?: return

        client.execute {
            val currentData = AfflictionPlayerAttachment.getData(player)
            val updatedData = applyChanges(currentData, changes)
            AfflictionPlayerAttachment.setData(player, updatedData, sync = false)
        }
    }

    companion object {
        val ID: CustomPacketPayload.Type<OptimizedSelectiveSyncPayload> =
            CustomPacketPayload.Type(Witchery.id("optimized_selective_sync"))

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, OptimizedSelectiveSyncPayload> =
            StreamCodec.of(
                { buf, payload -> payload.write(buf) },
                { buf -> OptimizedSelectiveSyncPayload(buf) }
            )

        private fun writeChanges(buf: RegistryFriendlyByteBuf, changes: Map<String, Any?>) {
            val tag = CompoundTag()

            // Write number of changes
            tag.putInt("count", changes.size)

            changes.forEach { (path, value) ->
                val field = SyncFieldRegistry.fieldsByPath[path]
                if (field != null && value != null) {
                    @Suppress("UNCHECKED_CAST")
                    val codec = field.codec as Codec<Any?>
                    codec.encodeStart(NbtOps.INSTANCE, value)
                        .resultOrPartial { error ->
                            Witchery.LOGGER.error("Failed to encode field $path: $error")
                        }
                        .ifPresent { encoded ->
                            tag.put(path, encoded)
                        }
                }
            }

            buf.writeNbt(tag)
        }

        private fun readChanges(buf: RegistryFriendlyByteBuf): Map<String, Any?> {
            val tag = buf.readNbt() ?: return emptyMap()
            val changes = mutableMapOf<String, Any?>()

            tag.allKeys.forEach { path ->
                if (path == "count") return@forEach

                val field = SyncFieldRegistry.fieldsByPath[path]
                if (field != null) {
                    val fieldTag = tag.get(path)
                    if (fieldTag != null) {
                        @Suppress("UNCHECKED_CAST")
                        val codec = field.codec as Codec<Any?>
                        codec.parse(NbtOps.INSTANCE, fieldTag)
                            .resultOrPartial { error ->
                                Witchery.LOGGER.error("Failed to decode field $path: $error")
                            }
                            .ifPresent { value ->
                                changes[path] = value
                            }
                    }
                }
            }

            return changes
        }

        fun applyChanges(
            data: AfflictionPlayerAttachment.Data,
            changes: Map<String, Any?>
        ): AfflictionPlayerAttachment.Data {
            return changes.entries.fold(data) { acc, (path, value) ->
                val field = SyncFieldRegistry.fieldsByPath[path]
                if (field != null && value != null) {
                    @Suppress("UNCHECKED_CAST")
                    val setter = field.setter as (AfflictionPlayerAttachment.Data, Any?) -> AfflictionPlayerAttachment.Data
                    setter(acc, value)
                } else {
                    acc
                }
            }
        }
    }
}