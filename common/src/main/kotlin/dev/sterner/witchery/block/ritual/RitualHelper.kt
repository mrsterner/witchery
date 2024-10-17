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

    fun runCommand(level: Level, blockPos: BlockPos, blockEntity: GoldenChalkBlockEntity, phase: String) {
        val server = level.server
        if (blockEntity.ritualRecipe != null) {
            for (commandType in blockEntity.ritualRecipe!!.commands) {
                if (commandType.type == phase) {
                    val commandListLength = commandType.ctx.size
                    if (commandListLength == 1) {
                        when (commandType.ctx[0]) {
                            CommandContext.NOTHING -> {
                                runCommand(blockEntity, level, server, blockPos, commandType.command, null, null)
                            }

                            CommandContext.PLAYER -> {
                                val playerUuid = blockEntity.targetPlayer
                                val player = playerUuid?.let { server?.playerList?.getPlayer(it) }
                                runCommand(blockEntity, level, server, blockPos, commandType.command, player, null)
                            }

                            CommandContext.PLAYER_OR_ENTITY -> {
                                val playerUuid = blockEntity.targetPlayer
                                val player = playerUuid?.let { server?.playerList?.getPlayer(it) }
                                if (player != null) {
                                    runCommand(blockEntity, level, server, blockPos, commandType.command, player, null)
                                } else {
                                    val targetEntity = blockEntity.targetEntity
                                    runCommand(
                                        blockEntity,
                                        level,
                                        server,
                                        blockPos,
                                        commandType.command,
                                        null,
                                        targetEntity
                                    )
                                }
                            }

                            CommandContext.ENTITY -> {
                                val targetEntity = blockEntity.targetEntity
                                runCommand(blockEntity, level, server, blockPos, commandType.command, null, targetEntity)
                            }

                            CommandContext.BLOCKPOS -> {
                                val targetPos = blockEntity.targetPos
                                if (targetPos != null) {
                                    val dimensionLevel =
                                        server?.getLevel(targetPos.dimension()) // Get the correct dimension's level
                                    if (dimensionLevel != null) {
                                        runCommand(
                                            blockEntity,
                                            dimensionLevel,
                                            server,
                                            targetPos.pos(),
                                            commandType.command,
                                            null,
                                            null
                                        )
                                    }
                                }
                            }
                        }
                    } else if(commandListLength == 2){
                        if (commandType.ctx.contains(CommandContext.PLAYER_OR_ENTITY) && commandType.ctx.contains(CommandContext.BLOCKPOS)) {
                            val targetPos = blockEntity.targetPos
                            if (targetPos != null) {
                                val targetEntity = blockEntity.targetEntity
                                val playerUuid = blockEntity.targetPlayer
                                val player = playerUuid?.let { server?.playerList?.getPlayer(it) }
                                val dimensionLevel =
                                    server?.getLevel(targetPos.dimension()) // Get the correct dimension's level
                                if (dimensionLevel != null) {
                                    runCommand(
                                        blockEntity,
                                        dimensionLevel,
                                        server,
                                        targetPos.pos(),
                                        commandType.command,
                                        player,
                                        targetEntity
                                    )
                                }
                            }
                        }
                    }

                }
            }
        }
    }

    private fun runCommand(
        blockEntity: GoldenChalkBlockEntity,
        level: Level,
        minecraftServer: MinecraftServer?,
        blockPos: BlockPos,
        command: String,
        player: Player?,
        entityId: Int?
    ) {
        var formattedCommand = command
        if (minecraftServer != null && formattedCommand.isNotEmpty()) {
            val commandSource: CommandSourceStack = minecraftServer.createCommandSourceStack().withSuppressedOutput()
            val commandManager: Commands = minecraftServer.commands

            if (player != null) {
                formattedCommand = formattedCommand.replace("{player}", player.name.string)
            }

            if (entityId != null) {
                val entity = level.getEntity(entityId)
                if (entity is LivingEntity) {
                    val tag = "Waystone_${entity.uuid}" // Create the dynamic tag based on the entity UUID

                    // Add the tag to the entity if it doesn't already have it
                    if (!entity.tags.contains(tag)) {
                        entity.addTag(tag)
                    }
                    // Replace {entity} with the @e[tag="Waystone_${entity.uuid}"] selector
                    formattedCommand = formattedCommand.replace("{entity}", "@e[tag=$tag]")
                }
            }

            formattedCommand = formattedCommand.replace("{time}", "${level.dayTime}")
            formattedCommand = formattedCommand.replace("{owner}", "${blockEntity.ownerName}")
            formattedCommand = formattedCommand.replace("{blockPos}", "${blockPos.x} ${blockPos.y} ${blockPos.z}")
            formattedCommand = "execute as ${blockEntity.ownerName} run execute in ${
                level.dimension().location().path
            } run " + formattedCommand
            val parseResults: ParseResults<CommandSourceStack> =
                commandManager.dispatcher.parse(formattedCommand, commandSource)
            commandManager.performCommand(parseResults, formattedCommand)
        }
    }
}