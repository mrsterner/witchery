package dev.sterner.witchery.block.ritual

@JvmRecord
data class CommandType(val command: String, val type: String, val ctx: List<CommandContext>) {
    companion object {
        val DEFAULT = CommandType("", "", listOf(CommandContext.NOTHING))
        const val START = "start"
        const val TICK = "tick"
        const val END = "end"
    }
    constructor(command: String, type: String, ctx: CommandContext) : this(command, type, listOf(ctx))
}