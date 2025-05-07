package dev.sterner.witchery.api.schedule

import dev.architectury.event.events.common.TickEvent
import net.minecraft.server.MinecraftServer

object TickTaskScheduler {
    private val tasks = mutableListOf<ServerTickTask>()

    fun addTask(task: ServerTickTask) {
        tasks.add(task)
    }

    fun tick(server: MinecraftServer) {
        tasks.removeIf { it.tick() }
    }

    fun registerEvents() {
        TickEvent.SERVER_POST.register(::tick)
    }
}
