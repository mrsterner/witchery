package dev.sterner.witchery.api.event

interface Event<T> {
    fun invoker(): T

    fun register(listener: T)

    fun unregister(listener: T)

    fun isRegistered(listener: T): Boolean

    fun clearListeners()
}