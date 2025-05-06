package dev.sterner.witchery.api

interface DelayedTask {
    fun tick(): Boolean
}
