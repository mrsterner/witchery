package dev.sterner.witchery.core.api.schedule

import net.minecraft.server.MinecraftServer

object TickTaskScheduler {
    private val tasks = mutableListOf<ServerTickTask>()

    fun addTask(task: ServerTickTask) {
        tasks.add(task)
    }

    fun tick(server: MinecraftServer) {
        tasks.removeIf { it.tick() }
    }
}
