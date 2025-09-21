package dev.sterner.witchery.api.event

import net.minecraft.world.InteractionResult
import org.apache.commons.lang3.BooleanUtils

class EventResult internal constructor(private val interruptsFurtherEvaluation: Boolean, private val value: Boolean?) {

    fun interruptsFurtherEvaluation(): Boolean {
        return interruptsFurtherEvaluation
    }

    fun value(): Boolean? {
        return value
    }

    val isEmpty: Boolean
        get() = value == null

    val isPresent: Boolean
        get() = value != null

    val isTrue: Boolean
        get() = BooleanUtils.isTrue(value)

    val isFalse: Boolean
        get() = BooleanUtils.isFalse(value)

    companion object {
        private val TRUE = EventResult(true, true)
        private val STOP = EventResult(true, null)
        private val PASS = EventResult(false, null)
        private val FALSE = EventResult(true, false)

        fun pass(): EventResult {
            return PASS
        }

        fun interrupt(value: Boolean?): EventResult {
            if (value == null) return STOP
            if (value) return TRUE
            return FALSE
        }

        fun interruptTrue(): EventResult {
            return TRUE
        }


        fun interruptDefault(): EventResult {
            return STOP
        }

        fun interruptFalse(): EventResult {
            return FALSE
        }
    }
}
