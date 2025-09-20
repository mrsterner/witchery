package dev.sterner.witchery.block.ritual

@JvmRecord
data class CommandType(val command: String, val type: String) {
    companion object {
        val DEFAULT = CommandType("", "")
        const val START = "start"
        const val TICK = "tick"
        const val END = "end"
    }
}