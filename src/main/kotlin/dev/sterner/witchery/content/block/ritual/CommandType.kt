package dev.sterner.witchery.content.block.ritual

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder

@JvmRecord
data class CommandType(
    val command: String,
    val type: String,
    val minWitchPower: Int = 0,
    val maxWitchPower: Int = Int.MAX_VALUE) {

    companion object {
        val DEFAULT = CommandType("", "")
        const val START = "start"
        const val TICK = "tick"
        const val END = "end"

        val CODEC: Codec<CommandType> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("command").forGetter(CommandType::command),
                Codec.STRING.fieldOf("type").forGetter(CommandType::type),
                Codec.INT.optionalFieldOf("minWitchPower", 0).forGetter(CommandType::minWitchPower),
                Codec.INT.optionalFieldOf("maxWitchPower", Int.MAX_VALUE).forGetter(CommandType::maxWitchPower)
            ).apply(instance, ::CommandType)
        }
    }

    fun isInWitchPowerRange(witchPower: Int): Boolean {
        return witchPower >= minWitchPower && witchPower < maxWitchPower
    }
}