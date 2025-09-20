package dev.sterner.witchery.api.schedule

interface DelayedTask {
    fun tick(): Boolean
}
