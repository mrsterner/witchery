package dev.sterner.witchery.features.misc

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.core.api.TeleportRequest
import dev.sterner.witchery.core.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel

object TeleportQueueLevelAttachment {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.TELEPORT_QUEUE_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.TELEPORT_QUEUE_DATA_ATTACHMENT, data)
    }


    data class Data(val pendingTeleports: MutableList<TeleportRequest> = mutableListOf()) {

        companion object {
            val ID: ResourceLocation = Witchery.Companion.id("teleport_queue")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(TeleportRequest.Companion.CODEC).fieldOf("pendingTeleports")
                        .forGetter { it.pendingTeleports }
                ).apply(instance, TeleportQueueLevelAttachment::Data)
            }
        }
    }
}