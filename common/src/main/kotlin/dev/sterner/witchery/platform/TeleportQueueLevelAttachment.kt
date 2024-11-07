package dev.sterner.witchery.platform

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.architectury.injectables.annotations.ExpectPlatform
import dev.sterner.witchery.Witchery
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel

object TeleportQueueLevelAttachment {

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

    fun addRequest(level: ServerLevel, request: TeleportRequest) {
        val data = getData(level)
        val updatedData = data.pendingTeleports.toMutableList()

        updatedData.add(request)
        setData(level, Data(updatedData))
    }

    fun processQueue(minecraftServer: MinecraftServer) {
        minecraftServer.allLevels.forEach { level ->
            val data = getData(level)
            val iterator = data.pendingTeleports.iterator()
            var dataModified = false

            while (iterator.hasNext()) {
                val request = iterator.next()
                val overworld = minecraftServer.overworld()
                if (overworld.hasChunk(request.chunkPos.x, request.chunkPos.z)) {
                    if (request.execute(minecraftServer)) {
                        overworld.setChunkForced(request.chunkPos.x, request.chunkPos.z, false)
                        SleepingPlayerLevelAttachment.remove(request.player, overworld)
                        iterator.remove()
                        dataModified = true
                    }
                } else {
                    overworld.setChunkForced(request.chunkPos.x, request.chunkPos.z, true)
                }
            }

            if (dataModified) {
                setData(level, data)
            }
        }
    }

    data class Data(val pendingTeleports: MutableList<TeleportRequest> = mutableListOf()) {

        companion object {
            val ID: ResourceLocation = Witchery.id("teleport_queue")

            val CODEC: Codec<Data> = RecordCodecBuilder.create { instance ->
                instance.group(
                    Codec.list(TeleportRequest.CODEC).fieldOf("pendingTeleports")
                        .forGetter { it.pendingTeleports }
                ).apply(instance, ::Data)
            }
        }
    }
}