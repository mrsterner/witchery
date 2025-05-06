package dev.sterner.witchery.api


class ServerTickTask(private var ticksRemaining: Int, private val task: Runnable) : DelayedTask {
    override fun tick(): Boolean {
        if (ticksRemaining <= 0) {
            task.run()
            return true
        }

        ticksRemaining--
        return false
    }

    companion object {
        operator fun invoke(delayTicks: Int, task: () -> Unit): DelayedTask {
            return ServerTickTask(delayTicks) { task() }
        }
    }
}