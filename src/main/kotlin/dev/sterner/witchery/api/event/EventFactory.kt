package dev.sterner.witchery.api.event

import java.lang.invoke.MethodHandles
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.*
import java.util.function.Function

object EventFactory {

    fun <T> of(function: Function<List<T>, T>): Event<T> {
        return EventImpl(function)
    }

    @SafeVarargs
    fun <T> createLoop(vararg typeGetter: T): Event<T> {
        if (typeGetter.isNotEmpty()) throw IllegalStateException("array must be empty!")
        @Suppress("UNCHECKED_CAST")
        return createLoop(typeGetter.javaClass.componentType as Class<T>)
    }

    fun <T> createEventResult(clazz: Class<T>): Event<T> {
        return of(Function { listeners ->
            @Suppress("UNCHECKED_CAST")
            Proxy.newProxyInstance(
                EventFactory::class.java.classLoader,
                arrayOf(clazz)
            ) { _, method, args ->
                for (listener in listeners) {
                    val result = Objects.requireNonNull(
                        invokeMethod<Any?, EventResult>(listener, method, args ?: emptyArray())
                    )
                    if (result.interruptsFurtherEvaluation()) {
                        return@newProxyInstance result
                    }
                }
                EventResult.pass()
            } as T
        })
    }


    @SafeVarargs
    fun <T> createEventResult(vararg typeGetter: T): Event<T> {
        if (typeGetter.isNotEmpty()) throw IllegalStateException("array must be empty!")
        @Suppress("UNCHECKED_CAST")
        return createEventResult(typeGetter.javaClass.componentType as Class<T>)
    }


    fun <T> createLoop(clazz: Class<T>): Event<T> {
        return of(Function { listeners ->
            @Suppress("UNCHECKED_CAST")
            Proxy.newProxyInstance(
                EventFactory::class.java.classLoader,
                arrayOf(clazz)
            ) { _, method, args ->
                for (listener in listeners) {
                    invokeMethod<Any?, Any?>(listener, method, args ?: emptyArray())
                }
                null
            } as T
        })
    }

    private fun <T, R> invokeMethod(listener: T, method: Method, args: Array<out Any?>): R {
        @Suppress("UNCHECKED_CAST")
        return MethodHandles.lookup().unreflect(method)
            .bindTo(listener)
            .invokeWithArguments(*args) as R
    }

    private class EventImpl<T>(
        private val function: Function<List<T>, T>
    ) : Event<T> {

        private var invoker: T? = null
        private val listeners: ArrayList<T> = ArrayList()

        override fun invoker(): T {
            if (invoker == null) {
                update()
            }
            return invoker!!
        }

        override fun register(listener: T) {
            listeners.add(listener)
            invoker = null
        }

        override fun unregister(listener: T) {
            listeners.remove(listener)
            listeners.trimToSize()
            invoker = null
        }

        override fun isRegistered(listener: T): Boolean {
            return listeners.contains(listener)
        }

        override fun clearListeners() {
            listeners.clear()
            listeners.trimToSize()
            invoker = null
        }

        fun update() {
            invoker = if (listeners.size == 1) {
                listeners[0]
            } else {
                function.apply(listeners)
            }
        }
    }
}