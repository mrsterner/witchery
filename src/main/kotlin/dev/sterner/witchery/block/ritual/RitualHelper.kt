package dev.sterner.witchery.block.ritual

import com.mojang.brigadier.ParseResults
import dev.sterner.witchery.entity.FloatingItemEntity
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.world.Containers
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import kotlin.math.cos
import kotlin.math.sin


object RitualHelper {

    fun isDaytime(level: Level): Boolean {
        val timeOfDay = level.dayTime % 24000
        return timeOfDay in 0..11999
    }

    fun isNighttime(level: Level): Boolean {
        val timeOfDay = level.dayTime % 24000
        level.moonPhase
        return timeOfDay in 12000..23999
    }

    fun isFullMoon(level: Level): Boolean {
        // Full moon occurs when the moon phase is 0.
        return level.moonPhase == 0 && isNighttime(level)
    }

    fun isNewMoon(level: Level): Boolean {
        // New moon occurs when the moon phase is 4.
        return level.moonPhase == 4 && isNighttime(level)
    }

    fun isWaxing(level: Level): Boolean {
        // Waxing phases: moon phases 5, 6, or 7, occurring during the night
        return level.moonPhase in 5..7 && isNighttime(level)
    }

    fun isWaning(level: Level): Boolean {
        // Waning phases: moon phases 1, 2, or 3, occurring during the night
        return level.moonPhase in 1..3 && isNighttime(level)
    }

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

    fun summonSummons(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity) {
        if (blockEntity.ritualRecipe != null) {
            for (entityType in blockEntity.ritualRecipe!!.outputEntities) {
                val entity = entityType.create(level)
                if (entity is LivingEntity) {
                    val angle: Float = level.random.nextFloat() * 360
                    val distance: Double = level.random.nextDouble() * 2
                    val x = blockPos.x + distance * cos(Math.toRadians(angle.toDouble()))
                    val z = blockPos.z + distance * sin(Math.toRadians(angle.toDouble()))
                    entity.moveTo(x, blockPos.y + 0.2, z, level.random.nextFloat() * 360, 0f)
                    level.addFreshEntity(entity)
                }
            }
        }
    }


    //Rewrite
    fun runCommand(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity, phase: String) {
        val server = level.server
        if (blockEntity.ritualRecipe != null) {
            for (commandType in blockEntity.ritualRecipe!!.commands) {
                if (commandType.type == phase) {
                    val playerUuid = blockEntity.targetPlayer
                    val player = playerUuid?.let { server?.playerList?.getPlayer(it) }
                    val targetEntity = blockEntity.targetEntity
                    val targetPos = blockEntity.targetPos
                    val dimensionLevel = targetPos?.dimension()?.let { server?.getLevel(it) }

                    runCommand(
                        blockEntity,
                        dimensionLevel ?: level,
                        server,
                        blockPos,
                        targetPos?.pos,
                        commandType.command,
                        player,
                        targetEntity
                    )
                }
            }
        }
    }

    private fun runCommand(
        blockEntity: GoldenChalkBlockEntity,
        level: Level,
        minecraftServer: MinecraftServer?,
        blockPos: BlockPos,
        waystonePos: BlockPos?,
        command: String,
        player: Player?,
        entityId: Int?
    ) {
        var formattedCommand = command
        if (minecraftServer != null && formattedCommand.isNotEmpty()) {
            val commandSource: CommandSourceStack = minecraftServer.createCommandSourceStack().withSuppressedOutput()
            val commandManager: Commands = minecraftServer.commands

            if (player != null) {
                formattedCommand = formattedCommand.replace("{taglockPlayer}", player.name.string)
            }

            if (entityId != null) {
                for (serverLevels in level.server!!.allLevels) {
                    val entity = serverLevels.getEntity(entityId)
                    if (entity is LivingEntity) {
                        val tag = "Waystone_${entity.uuid}"

                        if (!entity.tags.contains(tag)) {
                            entity.addTag(tag)
                        }
                        formattedCommand = formattedCommand.replace("{taglockEntity}", "@e[tag=$tag]")
                        break
                    }
                }
            }

            if (formattedCommand.contains("{taglockPlayerOrEntity}")) {
                if (player != null) {
                    formattedCommand = formattedCommand.replace("{taglockPlayerOrEntity}", player.name.string)
                } else if (entityId != null) {
                    for (serverLevels in level.server!!.allLevels) {
                        val entity = serverLevels.getEntity(entityId)
                        if (entity is LivingEntity) {
                            val tag = "Waystone_${entity.uuid}"

                            if (!entity.tags.contains(tag)) {
                                entity.addTag(tag)
                            }
                            formattedCommand = formattedCommand.replace("{taglockPlayerOrEntity}", "@e[tag=$tag]")
                            break
                        }
                    }
                }
            }

            if (waystonePos != null) {
                formattedCommand =
                    formattedCommand.replace("{waystonePos}", "${waystonePos.x} ${waystonePos.y} ${waystonePos.z}")
            }
            formattedCommand = formattedCommand.replace("{time}", "${level.dayTime}")
            formattedCommand = formattedCommand.replace("{owner}", "${blockEntity.ownerName}")
            formattedCommand = formattedCommand.replace("{chalkPos}", "${blockPos.x} ${blockPos.y} ${blockPos.z}")
            formattedCommand = "execute as ${blockEntity.ownerName} run execute in ${
                level.dimension().location().path
            } run " + formattedCommand
            val parseResults: ParseResults<CommandSourceStack> =
                commandManager.dispatcher.parse(formattedCommand, commandSource)
            commandManager.performCommand(parseResults, formattedCommand)
        }
    }
}