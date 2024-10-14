package dev.sterner.witchery.block.ritual

import com.mojang.brigadier.ParseResults
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import java.util.*


class RitualManager {
    var userUuid: UUID? = null


    fun summonItems(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity) {
        val x = blockPos.x + 0.5
        val y = blockPos.y + 0.5
        val z = blockPos.z + 0.5

    }

    fun summonSummons(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity){

    }

    fun consumeSacrifices(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity) : Boolean {
        return false
    }

    fun runCommand(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity, phase: String) {
        val server = level.server
        if (blockEntity.ritualRecipe != null) {
            for (commandType in blockEntity.ritualRecipe.commands) {
                if (commandType.type == phase) {
                    runCommand(server, blockPos, commandType.command)
                }
            }
        }
    }

    private fun runCommand(minecraftServer: MinecraftServer?, blockPos: BlockPos, command: String) {
        var command = command
        if (minecraftServer != null && command.isNotEmpty()) {
            command = "execute positioned {pos} run $command"
            val posString = blockPos.x.toString() + " " + blockPos.y + " " + blockPos.z
            val parsedCommand = command.replace("\\{pos}".toRegex(), posString)
            val commandSource: CommandSourceStack = minecraftServer.createCommandSourceStack()
            val commandManager: Commands = minecraftServer.commands
            val parseResults: ParseResults<CommandSourceStack> = commandManager.dispatcher.parse(parsedCommand, commandSource)
            commandManager.performCommand(parseResults, parsedCommand)
        }
    }

    @JvmRecord
    data class CommandType(val command: String, val type: String) {

    }
}