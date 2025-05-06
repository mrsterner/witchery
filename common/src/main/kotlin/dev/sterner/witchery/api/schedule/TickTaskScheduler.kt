package dev.sterner.witchery.api.schedule

object TickTaskScheduler {
    private val tasks = mutableListOf<ServerTickTask>()

    fun addTask(task: ServerTickTask) {
        tasks.add(task)
    }

    fun tick() {
        tasks.removeIf { it.tick() }
    }
}
