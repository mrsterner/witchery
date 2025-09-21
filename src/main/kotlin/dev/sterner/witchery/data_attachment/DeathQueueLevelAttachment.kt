package dev.sterner.witchery.data_attachment

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.sterner.witchery.Witchery
import dev.sterner.witchery.registry.WitcheryDataAttachments
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import java.util.*

object DeathQueueLevelAttachment {

    @JvmStatic
    fun getData(level: ServerLevel): Data {
        return level.getData(WitcheryDataAttachments.DEATH_QUEUE_LEVEL_DATA_ATTACHMENT)
    }

    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        level.setData(WitcheryDataAttachments.DEATH_QUEUE_LEVEL_DATA_ATTACHMENT, data)
    }

    fun addDeathToQueue(level: ServerLevel, uuid: UUID) {
        val old = getData(level)
        if (!old.killerQueue.contains(uuid)) {
            val newList = old.killerQueue.toMutableList()
            newList.add(uuid)

            val new = old.copy(killerQueue = newList)
            setData(level, new)
        }
    }

    fun removeFromDeathQueue(level: ServerLevel, uuid: UUID) {
        val old = getData(level)
        if (old.killerQueue.contains(uuid)) {
            val newList = old.killerQueue.toMutableList()
            newList.remove(uuid)
            setData(level, old.copy(killerQueue = newList))
        }
    }

    data class Data(val killerQueue: List<UUID> = emptyList()) {

        companion object {
            val ID: ResourceLocation = Witchery.id("death_queue")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(Codecs.UUID).fieldOf("killerQueue").forGetter { it.killerQueue }
                ).apply(instance, ::Data)
            }
        }
    }
}