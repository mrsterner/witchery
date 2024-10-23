package dev.sterner.witchery.block.ritual

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

@JvmRecord
data class CommandType(val command: String, val type: String) {
    companion object {
        val DEFAULT = CommandType("", "")
        const val START = "start"
        const val TICK = "tick"
        const val END = "end"

        val CODEC = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("command").forGetter(CommandType::command),
                Codec.STRING.fieldOf("type").forGetter(CommandType::type),
            ).apply(instance, ::CommandType)
        }
    }
}