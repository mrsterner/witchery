package dev.sterner.witchery.platform

import com.klikli_dev.modonomicon.util.Codecs
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import java.util.*

object DeathQueueLevelAttachment {

    @ExpectPlatform
    @JvmStatic
    fun getData(level: ServerLevel): Data {
        throw AssertionError()
    }

    @ExpectPlatform
    @JvmStatic
    fun setData(level: ServerLevel, data: Data) {
        throw AssertionError()
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