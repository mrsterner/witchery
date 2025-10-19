package dev.sterner.witchery.core.api.schedule

interface DelayedTask {
    fun tick(): Boolean
}
