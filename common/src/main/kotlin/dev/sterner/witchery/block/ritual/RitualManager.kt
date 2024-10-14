package dev.sterner.witchery.block.ritual

import com.mojang.brigadier.ParseResults
import dev.sterner.witchery.entity.FloatingItemEntity
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.world.Containers
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import kotlin.math.cos
import kotlin.math.sin


class RitualManager {

    fun summonItems(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity) {
        val x = blockPos.x + 0.5
        val y = blockPos.y + 0.5
        val z = blockPos.z + 0.5
        if (blockEntity.ritualRecipe != null) {
            for (output in blockEntity.ritualRecipe!!.outputItems) {
                if (blockEntity.ritualRecipe!!.floatingItemOutput) {
                    val itemEntity = FloatingItemEntity(level)
                    itemEntity.setItem(output.copy())
                    itemEntity.moveTo(blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5, 0f, 0f)
                    level.addFreshEntity(itemEntity)
                } else {
                    Containers.dropItemStack(level, x, y, z, output.copy())
                }
            }
        }
    }

    fun summonSummons(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity){
        if (blockEntity.ritualRecipe != null) {
            for (entityType in blockEntity.ritualRecipe!!.outputEntities) {
                val entity = entityType.create(level)
                if (entity is LivingEntity) {
                    val angle: Float = level.random.nextFloat() * 360
                    val distance: Double = level.random.nextDouble() * 2
                    val x = blockPos.x + distance * cos(Math.toRadians(angle.toDouble()))
                    val z = blockPos.z + distance * sin(Math.toRadians(angle.toDouble()))
                    entity.moveTo(x, blockPos.y + 1.0, z, level.random.nextFloat() * 360, 0f)
                    level.addFreshEntity(entity)
                }
            }
        }
    }

    fun consumeSacrifices(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity) : Boolean {
        return false
    }

    fun runCommand(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity, phase: String) {
        val server = level.server
        if (blockEntity.ritualRecipe != null) {
            for (commandType in blockEntity.ritualRecipe!!.commands) {
                if (commandType.type == phase) {
                    runCommand(server, blockPos, commandType.command)
                }
            }
        }
    }

    private fun runCommand(minecraftServer: MinecraftServer?, blockPos: BlockPos, command: String) {
        var formattedCommand = command
        if (minecraftServer != null && formattedCommand.isNotEmpty()) {
            formattedCommand = "execute positioned {pos} run $formattedCommand"
            val posString = blockPos.x.toString() + " " + blockPos.y + " " + blockPos.z
            val parsedCommand = formattedCommand.replace("\\{pos}".toRegex(), posString)
            val commandSource: CommandSourceStack = minecraftServer.createCommandSourceStack()
            val commandManager: Commands = minecraftServer.commands
            val parseResults: ParseResults<CommandSourceStack> = commandManager.dispatcher.parse(parsedCommand, commandSource)
            commandManager.performCommand(parseResults, parsedCommand)
        }
    }

    @JvmRecord
    data class CommandType(val command: String, val type: String) {
        companion object {
            const val START = "start"
            const val TICK = "tick"
            const val END = "end"
        }
    }
}