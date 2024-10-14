package dev.sterner.witchery.block.ritual

@JvmRecord
data class CommandType(val command: String, val type: String, val ctx: CommandContext) {
    companion object {
        val DEFAULT = CommandType("", "", CommandContext.NOTHING)
        const val START = "start"
        const val TICK = "tick"
        const val END = "end"
    }
}