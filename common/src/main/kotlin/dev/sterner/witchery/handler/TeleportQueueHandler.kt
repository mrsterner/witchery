package dev.sterner.witchery.handler

import dev.sterner.witchery.platform.SleepingLevelAttachment
import dev.sterner.witchery.platform.TeleportQueueLevelAttachment.Data
import dev.sterner.witchery.platform.TeleportQueueLevelAttachment.getData
import dev.sterner.witchery.platform.TeleportQueueLevelAttachment.setData
import dev.sterner.witchery.platform.TeleportRequest
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel

object TeleportQueueHandler {

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
                        SleepingPlayerHandler.remove(request.player, overworld)
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
}